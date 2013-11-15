package stock.tools;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Analyzer {
    
    private static final String NEWS_PATH = "news-500";
    
    public static void countSources() throws Exception {
	File dir = new File(NEWS_PATH);
	
	Map<String, Integer> sourceCnts = new HashMap<String, Integer>();
	
	for (String fn : dir.list()) {
	    System.out.println(fn);
	    File subdir = new File(NEWS_PATH + "/" + fn);
	    if (subdir.isDirectory()) {
		for (String dn : subdir.list()) {
		    File subsubdir = new File(NEWS_PATH + "/" + fn + "/" + dn);
		    if (subsubdir.isDirectory()) {
			for (String nn : subsubdir.list()) {
			    if (!nn.startsWith(".")) {
				String source = extractSource(NEWS_PATH + "/" + fn + "/" + dn + "/" + nn);
				System.out.println(source);
				if (sourceCnts.containsKey(source)) {
				    sourceCnts.put(source, sourceCnts.get(source) + 1);
				} else {
				    sourceCnts.put(source, 1);
				}
			    }
			}
		    }
		}
	    }
	}
	
	System.out.println(sourceCnts);
    }
    
    public static String extractSource(String fn) throws Exception {
	Scanner scanner = new Scanner(new File(fn));
	scanner.nextLine();
	scanner.nextLine();
	String url = scanner.nextLine();
	scanner.close();
	
	String[] tokens = url.split("\\*");
	String sourceurl = tokens.length > 1 ? tokens[1] : tokens[0];
	int index = sourceurl.indexOf(".com");
	if (index >= 0) {
	    return sourceurl.substring(0, index + 4);
	} else {
	    return sourceurl;
	}
    }
    
    public static int count = 0;
    
    public static synchronized boolean requireLock() {
	if (count >= 10) {
	    return false;
	} else {
	    count++;
	    return true;
	}
    }
    
    public static synchronized void releaseLock() {
	count--;
    }
    
    public static void generateDict() throws Exception {
	File dir = new File(NEWS_PATH);
	for (String fn : dir.list()) {
	    File subdir = new File(NEWS_PATH + "/" + fn);
	    if (subdir.isDirectory()) {
		while (!requireLock()) {
		    ;
		}
		System.out.println(fn);
		Thread t = new Thread(new WordService(fn));
		t.start();
	    }
	}
    }
    

    
    public static void generateFeatures() throws Exception {
	File dir = new File(NEWS_PATH);
	
	Map<String, Integer> freq = new HashMap<String, Integer>();
	
	for (String fn : dir.list()) {
	    System.out.println(fn);
	    File subdir = new File(NEWS_PATH + "/" + fn);
	    if (subdir.isDirectory()) {
		for (String dn : subdir.list()) {
		    File subsubdir = new File(NEWS_PATH + "/" + fn + "/" + dn);
		    if (subsubdir.isDirectory()) {
			int count = 0;
			for (String nn : subsubdir.list()) {
			    count++;
			    if (count == 2) {
				break;
			    }
			    if (!nn.startsWith(".")) {
				File nf = new File(NEWS_PATH + "/" + fn + "/" + dn + "/" + nn);
				Scanner scanner = new Scanner(nf);
				String title = scanner.nextLine();
				scanner.nextLine();
				scanner.nextLine();
				scanner.nextLine();
				scanner.nextLine();
				scanner.nextLine();
				String content = scanner.nextLine();
				String[] tokens = NewsTokenizer.tokenize(title + " " + content);
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
	}
	
	List<Map.Entry<String, Integer>> entries = new ArrayList<Map.Entry<String, Integer>>(freq.entrySet());
	Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
	    @Override
	    public int compare(Entry<String, Integer> e0,
		    Entry<String, Integer> e1) {
		return e1.getValue().compareTo(e0.getValue());
	    }	    
	});
	
	PrintWriter writer = new PrintWriter("dictionary.txt");
	for (Map.Entry<String, Integer> entry : entries) {
	    writer.println(entry.getKey() + " " + entry.getValue());
	}
	writer.flush();
	writer.close();
    }
    
    public static void gather() throws Exception {
	File dir = new File(NEWS_PATH);
	PrintWriter writer = new PrintWriter("news-all.txt");
	int count = 0;
	int newscount = 0;
	for (String fn : dir.list()) {
	    count++;
	    if (count == 3)
		break;
	    System.out.println(fn);
	    File subdir = new File(NEWS_PATH + "/" + fn);
	    if (subdir.isDirectory()) {
		for (String dn : subdir.list()) {
		    File subsubdir = new File(NEWS_PATH + "/" + fn + "/" + dn);
		    if (subsubdir.isDirectory()) {
			for (String nn : subsubdir.list()) {
			    if (!nn.startsWith(".")) {
				File nf = new File(NEWS_PATH + "/" + fn + "/" + dn + "/" + nn);
				Scanner scanner = new Scanner(nf);
				String title = scanner.nextLine();
				scanner.nextLine();
				scanner.nextLine();
				scanner.nextLine();
				scanner.nextLine();
				scanner.nextLine();
				String content = scanner.nextLine();
				String[] tokens = NewsTokenizer.tokenize(title + " " + content);
				scanner.close();
				for (String token : tokens) {
				    writer.print(token + " ");
				}
				writer.println();
				newscount++;
			    }
			}
		    }
		}
	    }
	}
	
	writer.flush();
	writer.close();
	System.out.println(newscount);
    }
    
    public static void main(String[] argv) throws Exception {
	// countSources();
	// generateDict();
	gather();
	// refinePage("http://finance.yahoo.com/news/atlas-financial-holdings-announces-expiration-225100238.html");
    }    

}
