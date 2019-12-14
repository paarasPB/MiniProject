package org.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashSet;

public class WebCrawler {
	private static final int MAX_DEPTH = 2;
	private HashSet<String> links;

	public WebCrawler() {
		links = new HashSet<>();
	}

	public void getPageLinks(String URL, int depth) {
		if ((!links.contains(URL) && (depth < MAX_DEPTH))) {
			System.out.println(">> Depth: " + depth + " [" + URL + "]");
			try {
				links.add(URL);

				Document document = Jsoup.connect(URL).get();
				Elements linksOnPage = document.select("a[href]");

				depth++;
				for (Element page : linksOnPage) {
					getPageLinks(page.attr("abs:href"), depth);
				}
			} catch (IOException e) {
				System.err.println("For '" + URL + "': " + e.getMessage());
			}
		}
	}

	public String scraping(String subject) throws IOException {
		URL url = new URL("https://en.wikipedia.org/w/index.php?action=raw&title=" + subject.replace(" ", "_"));
		String text = "";
		try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()))) {
			String line = null;
			while (null != (line = br.readLine())) {
				line = line.trim();
				if (!line.startsWith("|") && !line.startsWith("{") && !line.startsWith("}")
						&& !line.startsWith("<center>") && !line.startsWith("---")) {
					text += line;
				}
				if (text.length() > 200) {
					break;
				}
			}
		}
		return text;

	}

	public static void main(String[] args) {
		// new WebCrawler().getPageLinks("https://www.wikipedia.org", 1);
		try {
			System.out.println(new WebCrawler().scraping("Camp Rock"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
