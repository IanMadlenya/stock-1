package stock.tools;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class YahooNewsService implements Runnable {

    private String symbol;
    
    private Map<String, String> ptns;
    
    public YahooNewsService(String symbol) {
	this.symbol = symbol;
	ptns = new HashMap<String, String>();
	ptns.put("http://finance.yahoo.com", ".body p");
	ptns.put("http://biz.yahoo.com", "p");
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
	ptns.put("http://wallstcheatsheet.com", "article p");
	File dir = new File("news/" + symbol + "/");
	if (!dir.exists()) {
	    dir.mkdirs();
	}
    }
    
    @Override
    public void run() {
	String start = "2013-11-01";
	SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
	Date startdate = null;
	try {
	    startdate = dt.parse(start);
	} catch (Exception e) {
	    return;
	}

	System.out.println("+++++" + symbol);
	Calendar c = Calendar.getInstance();
	c.setTime(startdate);
	// Two months
	for (int i = 0; i < 31; i++) {
	    crawlWebpage(symbol, dt.format(c.getTime()));
	    System.out.println(dt.format(c.getTime()));
	    c.add(Calendar.DATE, 1);
	}
	
	// Finish
	Collector.releaseLock();
    }
    
    public void crawlWebpage(String symbol, String date) {
	String url = "http://finance.yahoo.com/q/h?s=" + symbol + "&t=" + date;
	Document doc;
	try {
	    doc = Jsoup.connect(url).get();
	} catch (IOException e1) {
	    e1.printStackTrace();
	    return;
	}
	
	Elements lis = doc.select(".mod ul li");
	Iterator<Element> iter = lis.iterator();
	int cnt = 0;
	while (iter.hasNext()) {
	    Element e = iter.next();
	    String href = e.select("a").first().attr("href");
	    String title = e.select("a").text();
	    String datef = e.select("span").text().replaceAll("\\(|\\)", "");
	    String month = datef.split(",")[1].trim().split("\\s+")[0];
	    String day = datef.split(",")[1].trim().split("\\s+")[1];
	    
	    if (month.equals("Sep")) {
		month = "09";
	    } else if (month.equals("Oct")) {
		month = "10";
	    } else if (month.equals("Nov")) {
		month = "11";
	    }
	    
	    if (day.length() == 1) {
		day = "0" + day;
	    }
	    datef = "2013-" + month + "-" + day;
	    if (date.equals(datef)) {
		String[] tokens = href.split("\\*");
		String sourceurl = tokens.length > 1 ? tokens[1] : tokens[0];
		int index = sourceurl.indexOf(".com");
		if (index >= 0) {
		    sourceurl = sourceurl.substring(0, index + 4);
		}
		
		try {
		    Document doc1 = Jsoup.connect(href).get();
		    
		    File dir = new File("news/" + symbol + "/" + date + "/");
		    if (!dir.exists()) {
			dir.mkdirs();
		    }
		    
		    PrintWriter writer = new PrintWriter("news/" + symbol
			    + "/" + date + "/news" + cnt + ".txt");
		    writer.println(title + "\n");
		    writer.println(href + "\n");
		    writer.println(date + "\n");
		    if (ptns.containsKey(sourceurl)) {
			Elements article = doc1.select(ptns.get(sourceurl));
			String content = article.text().replaceAll("\\s+", " ");
			writer.println(content);
		    } else {
			String content = doc1.text();
			writer.println(content);
		    }
		    writer.flush();
		    writer.close();
		    cnt++;
		} catch (IOException ioe) {
		    ioe.printStackTrace();
		}
	    }    
	}
    }

}
