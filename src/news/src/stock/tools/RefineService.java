package stock.tools;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class RefineService implements Runnable {
    
    private static final String NEWS_PATH = "news-500";
    
    private String symbol;
    
    private Map<String, String> ptns;
    
    public RefineService(String symbol) {
	this.symbol = symbol;
	ptns = new HashMap<String, String>();
	ptns.put("http://finance.yahoo.com", ".body p");
	ptns.put("http://biz.yahoo.com", "div p");
	ptns.put("http://seekingalpha.com", "#article_body");
	ptns.put("http://www.fool.com", ".entry-content");
	ptns.put("http://www.forbes.com", ".body p");
	ptns.put("http://www.thestreet.com", "#storyBody");
	ptns.put("http://beta.fool.com", "p");
	ptns.put("http://www.bloomberg.com", "#story_display p");
	ptns.put("http://news.investors.com", ".newsStory p");
	ptns.put("http://www.reuters.com", "#articleText");
	ptns.put("http://www.cnbc.com", "#article_body");
	ptns.put("http://bits.blogs.nytimes.com", ".postContent p");
    }
    
    @Override
    public void run() {
	try {
	    refine();
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
    
    public void refine() throws Exception {
	File dir = new File(NEWS_PATH + "/" + symbol);

	for (String fn : dir.list()) {
	    System.out.println(fn);
	    File subdir = new File(NEWS_PATH + "/" + symbol + "/" + fn);
	    if (subdir.isDirectory()) {
		for (String nn : subdir.list()) {
		    if (!nn.startsWith(".")) {
			File nf = new File(NEWS_PATH + "/" + symbol + "/" + fn
				+ "/" + nn);
			
			Scanner scanner = new Scanner(nf);
			String title = scanner.nextLine();
			scanner.nextLine();
			String url = scanner.nextLine();
			scanner.nextLine();
			String date = scanner.nextLine();
			scanner.nextLine();
			String content = scanner.nextLine();
			scanner.close();
			
			String[] tokens = url.split("\\*");
			String sourceurl = tokens.length > 1 ? tokens[1] : tokens[0];
			int index = sourceurl.indexOf(".com");
			if (index >= 0) {
			    sourceurl = sourceurl.substring(0, index + 4);
			}
			
			PrintWriter writer = new PrintWriter(NEWS_PATH + "/" + symbol + "/" + fn
				+ "/" + nn);
			writer.println(title + "\n");
			writer.println(url + "\n");
			writer.println(date + "\n");
			
			if (ptns.containsKey(sourceurl)) {
			    try {
				Document doc = Jsoup.connect(url).get();
				Elements article = doc.select(ptns.get(sourceurl));
				writer.println(article.text());
			    } catch (Exception e) {
				writer.println(content);
			    }
			} else {
			    writer.println(content);
			}
			writer.flush();
			writer.close();
			
		    }
		}
	    }

	}
    }
    
    public static void main(String[] argv) {
	Thread t = new Thread(new RefineService("ABAX"));
	t.run();
    }

}
