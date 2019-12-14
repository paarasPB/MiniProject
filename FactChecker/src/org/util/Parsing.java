package org.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse.File;

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Triple;

public class Parsing {

	public static void main(String[] args) throws IOException {

		// creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER,
		// parsing, and coreference resolution
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		// read some text in the text variable

		List<String> list = new ArrayList<>();
		List<Fact> factList = new ArrayList<Fact>();
		try (BufferedReader br = new BufferedReader(new FileReader(new java.io.File("SNLP2019_training.tsv")))) {
			list = br.lines().collect(Collectors.toList());

		}

		// for (String line : list) {

		// if (!line.startsWith("FactID")) {

		// Fact fact = new Fact(Integer.valueOf(line.split("\t")[0]),
		// line.split("\t")[1].trim(),
		// Float.valueOf(line.split("\t")[2]));
		Fact fact = new Fact();
		NLPTriple nlpTriple = new NLPTriple();

		// System.out.println(fact.getFactString() + " == " + fact.getFactValue());

		// Annotation document = new Annotation(fact.getFactString());
		Annotation document = new Annotation("Poul Anderson is The Boat of a Million Years' generator.");
		// New York City is IBM's innovation place.
		pipeline.annotate(document);

		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		for (CoreMap sentence : sentences) {
			int i = 0;
			String prevNe = "";
			String entity = "";
			boolean isSubjectSet = false;

			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {

				String word = token.get(TextAnnotation.class);

				String pos = token.get(PartOfSpeechAnnotation.class);

				String ne = token.get(NamedEntityTagAnnotation.class);

				System.out.println(word + " ," + ne + " ," + pos);
				if (pos.equalsIgnoreCase("NNP") && !isSubjectSet) {

					nlpTriple.setSubject(
							nlpTriple.getSubject() == null ? word + "_" : nlpTriple.getSubject() + word + "_");
				}
				if (pos.equalsIgnoreCase("NNP") && isSubjectSet) {

					nlpTriple.setObject(
							nlpTriple.getObject() == null ? word + "_" : nlpTriple.getObject() + word + "_");
				}

				if (pos.equalsIgnoreCase("NN")) {
					nlpTriple.setPredicate(
							nlpTriple.getPredicate() == null ? word + "_" : nlpTriple.getPredicate() + word + "_");

				}

				/*
				 * if (i >= 1) {
				 * 
				 * if (pos.equalsIgnoreCase(prevNe)) { entity += word + "_"; } else { if (entity
				 * != "") { if (nlpTriple.getSubject() == null ||
				 * nlpTriple.getSubject().equals("")) { nlpTriple.setSubject(entity.substring(0,
				 * entity.length() - 1)); } else if (nlpTriple.getObject() == null ||
				 * nlpTriple.getObject().equals("")) { nlpTriple.setObject(entity.substring(0,
				 * entity.length() - 1)); }
				 * 
				 * } if (pos.equalsIgnoreCase("NNP") || pos.equalsIgnoreCase("NP")) { entity =
				 * word + "_"; } else { entity = ""; }
				 * 
				 * }
				 * 
				 * } if (i == 0) { entity = word + "_";
				 * 
				 * } prevNe = pos; i++;
				 */

			}

		}
		nlpTriple.setSubject(nlpTriple.getSubject().substring(0, nlpTriple.getSubject().length() - 1));
		nlpTriple.setObject(nlpTriple.getObject().substring(0, nlpTriple.getObject().length() - 1));
		nlpTriple.setPredicate(nlpTriple.getPredicate().substring(0, nlpTriple.getPredicate().length() - 1));

		fact.setTriple(nlpTriple);
		factList.add(fact);
		System.out.println(
				"subject: " + fact.getTriple().getSubject() + " predicate: " + fact.getTriple().getPredicate());
		// }
		// }

	}
}
