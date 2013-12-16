import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cmu.arktweetnlp.Tagger;
import cmu.arktweetnlp.Tagger.TaggedToken;

public class TweetProcessor {
	Map<String, Integer> map = new HashMap<String, Integer>();
	Map<Integer, String> stock = new HashMap<Integer, String>();
	HashSet<String> stopwords = new HashSet<String>();

	Connection conTemp = null;
	Connection conStock = null;

	public TweetProcessor() {
		String urlTemp = "jdbc:mysql://localhost:3306/twitter";
		String urlStock = "jdbc:mysql://localhost:3306/stock";
		String user = "root";
		String password = "";

		BufferedReader buf;
		try {
			conTemp = DriverManager.getConnection(urlTemp, user, password);
			conStock = DriverManager.getConnection(urlStock, user, password);

			// Get the registered stock list
			PreparedStatement pst = conStock
					.prepareStatement("SELECT * FROM stock_stock");
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				int id = rs.getInt(1);
				String symbol = rs.getString(2);
				String name = rs.getString(3);
				map.put(symbol.toLowerCase(), id);
				map.put(name.toLowerCase(), id);
				stock.put(id, symbol);
			}

			InputStream is = this.getClass().getClassLoader()
					.getResourceAsStream("stop_words.txt");
			buf = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while ((line = buf.readLine()) != null) {
				stopwords.add(line.toLowerCase());
			}
			is.close();
			buf.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void process() {
		PreparedStatement pst = null;

		ResultSet rs = null;
		try {
			// Initialize ark tagger
			Tagger tagger = new Tagger();
			tagger.loadModel("/cmu/arktweetnlp/model.20120919");

			// Get current date
			Calendar now = Calendar.getInstance();
			now.setTime(new Date());

			Calendar curr = Calendar.getInstance();
			curr.setTime(new Date());
			curr.add(Calendar.DAY_OF_MONTH, -3);

			for (; !curr.after(now); curr.add(Calendar.DAY_OF_MONTH, 1)) {
				// Process past three days raw tweets
				pst = conTemp
						.prepareStatement("SELECT * FROM temp_twitter WHERE date=?");
				pst.setDate(1, new java.sql.Date(curr.getTimeInMillis()));
				rs = pst.executeQuery();

				while (rs.next()) {
					HashSet<Integer> ids = new HashSet<Integer>();
					java.sql.Date date = rs.getDate(1);
					String content = rs.getString(2).toLowerCase();

					for (String key : map.keySet()) {
						if (content.contains(key)) {
							ids.add(map.get(key));
							content = content.replace(key, "");
						}
					}

					if (ids.size() > 0 && content.trim().length() > 0) {
						List<TaggedToken> taggedTokens = tagger
								.tokenizeAndTag(content);
						StringBuffer sbuf = new StringBuffer();
						for (int i = 0; i < taggedTokens.size(); i++) {
							// Delete dollar sign, hashtag, punctuation and
							// emoticon
							if (taggedTokens.get(i).token.startsWith("#")
									|| taggedTokens.get(i).tag.equals("#")
									|| taggedTokens.get(i).token
											.startsWith("$")
									|| taggedTokens.get(i).tag.equals("$")
									|| taggedTokens.get(i).tag.equals(",")
									|| taggedTokens.get(i).tag.equals("E")
									|| taggedTokens.get(i).tag.equals("@")
									|| taggedTokens.get(i).token
											.startsWith("@")
									|| taggedTokens.get(i).tag.equals("U")
									|| taggedTokens.get(i).token
											.startsWith("http://")) {
								continue;
							} else {
								String token = normalize(eliminateRepeatedLetters(taggedTokens
										.get(i).token.toLowerCase()));
								if (token != null)
									sbuf.append(token + " ");
							}
						}
						String processed_str = sbuf.toString().trim();

						Iterator<Integer> itr = ids.iterator();
						while (itr.hasNext()) {
							Integer id = itr.next();
							pst = conStock
									.prepareStatement("INSERT INTO stock_twitter(stock_id, symbol,time, content) VALUES (?,?,?,?)");
							pst.setInt(1, id);
							pst.setString(2, stock.get(id));
							pst.setDate(3, date);
							pst.setString(4, processed_str);
							pst.executeUpdate();
						}
					}
				}
			}
			conTemp.close();
			conStock.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		TweetProcessor tp = new TweetProcessor();
		tp.process();
	}

	String eliminateRepeatedLetters(String token) {
		StringBuffer sb = new StringBuffer();
		char c = ' ';
		int i = 0;
		for (; i < token.length() - 2; i++) {
			if (token.charAt(i) == c)
				continue;
			if ((token.charAt(i + 2) == token.charAt(i + 1))
					&& (token.charAt(i + 1) == token.charAt(i))) {
				sb.append(token.charAt(i));
				sb.append(token.charAt(i));
				c = token.charAt(i);
				i = i + 2;
				continue;
			}
			sb.append(token.charAt(i));
		}
		while (i != (token.length())) {
			if (token.charAt(i) == c) {
				i++;
				continue;
			}
			sb.append(token.charAt(i));
			i++;
		}
		return sb.toString();
	}
	boolean isValidWord(String token) {
		for (int i = 0; i < token.length(); i++) {
			char c = token.charAt(i);
			if ((c <= 'z' && c >= 'a') || (c == '\'')) {
				continue;
			} else
				return false;
		}
		return true;
	}

	String normalize(String token) {
		if (token.length() == 0 || token.length() == 1)
			return null;
		else if (!isValidWord(token))
			return null;
		else if (stopwords.contains(token))
			return null;
		else
			return token;
	}
}
