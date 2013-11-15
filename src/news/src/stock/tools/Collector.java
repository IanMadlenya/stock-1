package stock.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Collector {
    
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
    
    public static void genTopics(String listFile) throws Exception {
	Scanner scanner = new Scanner(new File(listFile));
	List<String> symbols = new ArrayList<String>();
	while (scanner.hasNextLine()) {
	    symbols.add(scanner.nextLine());
	}
	scanner.close();
	
	for (int i = 0; i < 1; i++) {
	    int rand = (int) (Math.random() * 500);
	    System.out.println(symbols.get(rand));
	    Thread t = new Thread(new TopicService(symbols.get(rand)));
	    t.start();
	}
    }

    public static void companyCollect(String listFile) throws Exception {
	Scanner scanner = new Scanner(new File(listFile));
	
	while (scanner.hasNextLine()) {
	    while (!requireLock()) {
		;
	    }
	    String symbol = scanner.nextLine();
	    
	    if (symbol != null) {
		
		Thread t = new Thread(new YahooNewsService(symbol));
		t.start();
	    } else {
		releaseLock();
	    }
	}
    }
    
    public static void stat() throws Exception {
	File dir = new File("news-500");
	int tt = 0;
	int cc = 0;
	int g20 = 0;
	int g10 = 0;
	int g5 = 0;
	System.out.println(dir.list().length);
	for (String f : dir.list()) {
	    File subdir = new File("news-500/" + f);
	    if (subdir.isDirectory()) {
		int total = 0;
		for (String ff : subdir.list()) {
		    File subsubdir = new File("news-500/" + f + "/" + ff);
		    if (subsubdir.isDirectory()) {
			total += (subsubdir.list().length);
			tt += (subsubdir.list().length);
		    }
		}
		cc += subdir.list().length - 1;
		if ((total + 0.0) / (subdir.list().length - 1) >= 20) {
		    g20++;
		} else if ((total + 0.0) / (subdir.list().length - 1) >= 10) {
		    g10++;
		} else if ((total + 0.0) / (subdir.list().length - 1) >= 5) {
		    g5++;
		}
		System.out.println(f + " " + ((total + 0.0) / (subdir.list().length - 1)));
	    }
	}
	
	System.out.println("All: " + (tt + 0.0) / cc);
	System.out.println("> 20: " + g20);
	System.out.println("> 10: " + g10);
	System.out.println("> 5: " + g5);
    }
    
    public static void main(String[] argv) throws Exception {
	// companyCollect("500symbol.txt");
	// stat();
	genTopics("500symbol.txt");
    }
    
}
