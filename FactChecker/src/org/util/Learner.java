package org.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;

public class Learner {
	String filePath;
	Map<String, Map<String, List<String>>> model;

	Learner(String filePath) {
		this.filePath = filePath;
	}

	void learn() {
		model = new HashMap<String, Map<String, List<String>>>();
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");

		List<String> list = new ArrayList<>();
		List<Fact> factList = new ArrayList<Fact>();
		try (BufferedReader br = new BufferedReader(new FileReader(new java.io.File(this.filePath)))) {
			list = br.lines().collect(Collectors.toList());

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (String line : list) {

			if (!line.startsWith("FactID")) {

				Fact fact = new Fact(Integer.valueOf(line.split("\t")[0]), line.split("\t")[1].trim(),
						Float.valueOf(line.split("\t")[2]));
				// System.out.println(fact.getFactString() + " == " + fact.getFactValue());

				factList.add(fact);
				for (Sentence sent : new Document(fact.getFactString()).sentences()) {

					for (RelationTriple triple : sent.openieTriples()) {

						// System.out.println(triple + " == " + fact.getFactValue());

						String subject = "";
						for (CoreLabel coreLabel : triple.subject) {
							subject += coreLabel.originalText() + "_";

						}
						// System.out.println("subjects: " + subject.substring(0, subject.length() -
						// 1));

						String object = "";

						for (CoreLabel coreLabel : triple.object) {
							object += coreLabel.originalText() + "_";
						}
						// System.out.println("objects: " + object.substring(0, object.length() - 1));

						String predicate = "";
						for (CoreLabel coreLabel : triple.relation) {
							predicate += coreLabel.originalText() + "_";
						}
						// System.out.println("predicate: " + predicate.substring(0, predicate.length()
						// - 1));

						if (model.containsKey(subject.substring(0, subject.length() - 1))) {

							if (model.get(subject.substring(0, subject.length() - 1))
									.containsKey(predicate.substring(0, predicate.length() - 1))) {

								model.get(subject.substring(0, subject.length() - 1))
										.get(predicate.substring(0, predicate.length() - 1))
										.add(object.substring(0, object.length() - 1));

							} else {

								List<String> objectList = new ArrayList<String>();
								objectList.add(object.substring(0, object.length() - 1));

								model.get(subject.substring(0, subject.length() - 1))
										.put(predicate.substring(0, predicate.length() - 1), objectList);

							}

						} else {
							Map<String, List<String>> relation = new HashMap<String, List<String>>();
							List<String> objectList = new ArrayList<String>();
							objectList.add(object.substring(0, object.length() - 1));
							relation.put(predicate.substring(0, predicate.length() - 1), objectList);

							model.put(subject.substring(0, subject.length() - 1), relation);
						}
					}
				}

			}
		}
	}

	public static void main(String[] args) {
		Learner learner = new Learner("SNLP2019_training.tsv");
		learner.learn();
		System.out.println(learner.model);
	}
}