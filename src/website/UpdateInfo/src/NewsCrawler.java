import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

public class NewsCrawler {

	private Map<String, String> ptns;

	private Map<String, String> months;

	public NewsCrawler() {
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

		months = new HashMap<String, String>();
		months.put("Jan", "01");
		months.put("Feb", "02");
		months.put("Mar", "03");
		months.put("Apr", "04");
		months.put("May", "05");
		months.put("Jun", "06");
		months.put("Jul", "07");
		months.put("Aug", "08");
		months.put("Sep", "09");
		months.put("Oct", "10");
		months.put("Nov", "11");
		months.put("Dec", "12");
	}

	public void crawlWebpage(String symbol, int stockId, Calendar storedDate,
			PreparedStatement pst, Connection con) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar begin = Calendar.getInstance();
		begin.setTime(new Date());
		begin.add(Calendar.DAY_OF_MONTH, -10);

		Calendar fromDate = null;
		Calendar toDate = null;
		if (storedDate == null || begin.after(storedDate)) {
			fromDate = begin;
		} else {
			storedDate.add(Calendar.DAY_OF_MONTH, 1);
			fromDate = storedDate;
		}

		toDate = Calendar.getInstance();
		toDate.setTime(new Date());

		for (Calendar curr = toDate; !curr.before(fromDate); curr.add(
				Calendar.DAY_OF_MONTH, -1)) {
			String date = dateFormat.format(curr.getTime());
			System.out.println(date);

			String url = "http://finance.yahoo.com/q/h?s="
					+ symbol.substring(1) + "&t=" + date;

			Document doc;
			try {
				doc = Jsoup.connect(url).timeout(0).get();
			} catch (IOException e1) {
				e1.printStackTrace();
				return;
			}

			Elements lis = doc.select(".mod ul li");
			Iterator<Element> iter = lis.iterator();
			while (iter.hasNext()) {
				Element e = iter.next();
				String href = e.select("a").first().attr("href");
				String title = e.select("a").text();
				String datef = e.select("span").text()
						.replaceAll("\\(|\\)", "");
				if (datef.indexOf(",") < 0) {
					datef = date;
				} else {
					String month = datef.split(",")[1].trim().split("\\s+")[0];
					String day = datef.split(",")[1].trim().split("\\s+")[1];
					month = months.get(month);
					if (day.length() == 1) {
						day = "0" + day;
					}
					datef = "2013-" + month + "-" + day;
				}
				if (date.equals(datef)) {
					String[] tokens = href.split("\\*");
					String sourceurl = tokens.length > 1
							? tokens[1]
							: tokens[0];
					int index = sourceurl.indexOf(".com");
					if (index >= 0) {
						sourceurl = sourceurl.substring(0, index + 4);
					}

					try {
						Document doc1 = Jsoup.connect(href).timeout(0).get();
						String content = "";
						if (ptns.containsKey(sourceurl)) {
							Elements article = doc1.select(ptns.get(sourceurl));
							content = article.text().replaceAll("\\s+", " ");
						} else {
							content = doc1.text();
						}
						
						content = content.replaceAll("<[^>]*>", "");
						// Store news article into database
						// title, href, date, content
						pst = con
								.prepareStatement("INSERT INTO stock_news(stock_id, date, content, url, title) VALUES (?,?,?,?,?)");
						pst.setInt(1, stockId);
						pst.setDate(2,
								new java.sql.Date(curr.getTimeInMillis()));
						pst.setString(3, content);
						pst.setString(4, href);
						pst.setString(5, title);
						pst.executeUpdate();
					} catch (IOException ioe) {
						System.err.println("[ERROR] HTTP error fetching URL.");
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
	}
}
