package stock.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class YahooNewsService implements Runnable {

    private static final String NEWS_FEED = "http://finance.yahoo.com/rss/headline?s=";

    private String symbol;
    
    public YahooNewsService(String symbol) {
	this.symbol = symbol;
    }
    
    @Override
    public void run() {
	String start = "2013-08-01";
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
	for (int i = 0; i < 61; i++) {
	    try {
		crawlWebpage(symbol, dt.format(c.getTime()));
	    } catch (Exception e) {

	    }
	    System.out.println(dt.format(c.getTime()));
	    c.add(Calendar.DATE, 1);
	}
	
	// Finish
	Collector.releaseLock();
    }
    
    public void retrieve() throws IllegalArgumentException, FeedException, IOException {
	URL feedSource = new URL(NEWS_FEED + "yhoo&n=200");
	SyndFeedInput input = new SyndFeedInput();
	SyndFeed feed = input.build(new XmlReader(feedSource));
	System.out.println(feed.getEntries().size());
    }
    
    public void crawlWebpage(String symbol, String date) throws IOException {
	URL feedSource = new URL("http://finance.yahoo.com/q/h?s=" + symbol + "&t=" + date);
	URLConnection con = feedSource.openConnection();
	BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	StringBuilder sb = new StringBuilder();
	String input;
	while ((input = in.readLine()) != null) {
	    sb.append(input);
	}
	in.close();
	Pattern p = Pattern.compile("</span></h3><ul><li>(.*)</li></ul><table");
	Matcher m = p.matcher(sb.toString());
	Pattern lp = Pattern.compile("(?i)<a href=\"([^\"]+)\">(.+?)</a>");
	if (m.find()) {
	    String text = m.group(1);
	    Matcher lm = lp.matcher(text);
	    
	    File dir = new File("news-500/" + symbol + "/" + date);
	    if (!dir.exists()) {
		dir.mkdirs();
	    }
	    int cnt = 0;
	    while (lm.find()) {
		String link = lm.group(1);
		String title = lm.group(2);
		try {
		    Document doc = Jsoup.connect(link).get();
		    String content = doc.text().replaceAll("\\s+", " ");
		    PrintWriter writer = new PrintWriter("news-500/" + symbol + "/" + date + "/news" + cnt + ".txt");
		    writer.println(title + "\n");
		    writer.println(link + "\n");
		    writer.println(date + "\n");
		    writer.println(content);
		    writer.flush();
		    writer.close();
		    cnt++;
		} catch (Exception e) {
		    // System.out.println("[ERROR]");
		    continue;
		}
	    }
	}
    }


}
