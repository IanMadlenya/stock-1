package stock.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

public class WordService implements Runnable {

    private static final String NEWS_PATH = "news-500";

    private static final String DICT_PATH = "dict";

    private String symbol;

    public WordService(String symbol) {
	this.symbol = symbol;
    }

    @Override
    public void run() {

	Map<String, Integer> freq = new HashMap<String, Integer>();

	File subdir = new File(NEWS_PATH + "/" + symbol);
	if (subdir.isDirectory()) {
	    for (String dn : subdir.list()) {
		File subsubdir = new File(NEWS_PATH + "/" + symbol + "/" + dn);
		if (subsubdir.isDirectory()) {
		    for (String nn : subsubdir.list()) {
			if (!nn.startsWith(".")) {
			    File nf = new File(NEWS_PATH + "/" + symbol + "/"
				    + dn + "/" + nn);
			    Scanner scanner;
			    try {
				scanner = new Scanner(nf);
			    } catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			    }
			    String title = scanner.nextLine();
			    scanner.nextLine();
			    scanner.nextLine();
			    scanner.nextLine();
			    scanner.nextLine();
			    scanner.nextLine();
			    String content = scanner.nextLine();
			    String[] tokens = NewsTokenizer.tokenize(title
				    + " " + content);
			    for (String token : tokens) {
				if (freq.containsKey(token)) {
				    freq.put(token, freq.get(token) + 1);
				} else {
				    freq.put(token, 1);
				}
			    }
			}
		    }
		}
	    }
	}

	List<Map.Entry<String, Integer>> entries = new ArrayList<Map.Entry<String, Integer>>(
		freq.entrySet());
	Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
	    @Override
	    public int compare(Entry<String, Integer> e0,
		    Entry<String, Integer> e1) {
		return e1.getValue().compareTo(e0.getValue());
	    }
	});

	PrintWriter writer;
	try {
	    writer = new PrintWriter(DICT_PATH + "/" + symbol + "-dict.txt");
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	    return;
	}
	for (Map.Entry<String, Integer> entry : entries) {
	    writer.println(entry.getKey() + " " + entry.getValue());
	}
	writer.flush();
	writer.close();
	
	Analyzer.releaseLock();
    }

}
