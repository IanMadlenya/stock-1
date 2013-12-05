import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cmu.arktweetnlp.Tagger;
import cmu.arktweetnlp.Tagger.TaggedToken;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

public class ProcessJsonThread implements Runnable {
	public static ArrayList<String> symbol = new ArrayList<String>();
	public static ArrayList<String> name = new ArrayList<String>();
	public static Trie trie = null;

	private String date = null;
	private ArrayList<String> tweetJsons = null;
	private Tagger tagger = null;
	private String folder = null;

	/**
	 * Read in stock symbol and name into memory, build a trie and a ArrayList
	 * to store information
	 */
	public ProcessJsonThread(String date, String profiles, String folder,
			ArrayList<String> tweetJsons) {

		InputStream is = null;
		BufferedReader buf = null;
		String line = null;

		this.tweetJsons = tweetJsons;
		this.date = date;
		this.folder = folder;

		try {
			// Initialize ark tagger
			tagger = new Tagger();
			tagger.loadModel("/cmu/arktweetnlp/model.20120919");

			// Initialize language detector
			try {
				DetectorFactory.loadProfile(profiles);
			} catch (LangDetectException e) {
			}

			// Read the stock name file and construct a trie
			if ((trie == null) && (name.size() == 0)) {
				trie = new Trie();
				is = this.getClass().getClassLoader()
						.getResourceAsStream("stock/name.txt");
				buf = new BufferedReader(new InputStreamReader(is));
				int index = 0;
				while ((line = buf.readLine()) != null) {
					trie.AddWord(line.toLowerCase(), index);
					name.add(line.toLowerCase());
					index++;
				}
				is.close();
				buf.close();
			}

			if (symbol.size() == 0) {
				// Store stock symbol into ArrayList
				is = this.getClass().getClassLoader()
						.getResourceAsStream("stock/symbol.txt");
				buf = new BufferedReader(new InputStreamReader(is));
				int index = 0;
				while ((line = buf.readLine()) != null) {
					trie.AddWord(line.toLowerCase(), index);
					symbol.add(line);
					index++;
				}
				is.close();
				buf.close();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		BufferedWriter bwTweet = null;
		BufferedWriter bwWord = null;
		try {
			bwTweet = new BufferedWriter(new FileWriter(folder + "/result/"
					+ date + "_" + Thread.currentThread().getId(), true));

			bwWord = new BufferedWriter(new FileWriter(folder + "/word/" + date
					+ "_" + Thread.currentThread().getId(), true));

			HashMap<String, Integer> map = new HashMap<String, Integer>();

			Iterator<String> tweetItr = tweetJsons.iterator();
			while (tweetItr.hasNext()) {
				String tweetJson = tweetItr.next();

				// Read one tweet from Json file
				String content = getContentFromJsonString(tweetJson);
				if (content == null)
					// non-messaging event or JSONException happens
					continue;
				// Language detection
				try {
					Detector detector = DetectorFactory.create();
					detector.append(content);
					if (!detector.detect().equals("en"))
						// Tweet is not in English, throw it away
						continue;
				} catch (LangDetectException e) {
					// If language detector can not find any language feature,
					// throw it away
					continue;
				}

				// Set up a Hashset to store matched stock indexes
				Set<Integer> indexes = new HashSet<Integer>();

				// Check stock name, tricky part
				// Tokenize and Tag the tweet using ARK library, and then split
				// the tag and token into separate list
				List<TaggedToken> taggedTokens = tagger.tokenizeAndTag(content);
				List<String> tokens = new ArrayList<String>();
				List<String> tags = new ArrayList<String>();
				for (int i = 0; i < taggedTokens.size(); i++) {
					if (taggedTokens.get(i).token.startsWith("$")) {
						// Lower case the token, eliminate the repeated letters
						tokens.add(taggedTokens.get(i).token.toLowerCase());
						tags.add(taggedTokens.get(i).tag);
					}
					// Delete dollar sign, hashtag, punctuation and emoticon
					else if (taggedTokens.get(i).token.startsWith("#")
							|| taggedTokens.get(i).tag.equals("#")
							|| taggedTokens.get(i).tag.equals("$")
							|| taggedTokens.get(i).tag.equals(",")
							|| taggedTokens.get(i).tag.equals("E")
							|| taggedTokens.get(i).tag.equals("@")
							|| taggedTokens.get(i).token.startsWith("@")
							|| taggedTokens.get(i).tag.equals("U")) {
						continue;
					} else {
						// Lower case the token, eliminate the repeated letters
						tokens.add(eliminateRepeatedLetters(taggedTokens.get(i).token
								.toLowerCase()));
						tags.add(taggedTokens.get(i).tag);
					}
				}

				taggedTokens.clear();
				content = convertFromList(tokens);

				indexes.addAll(trie.findWords(tokens, tags));

				Iterator<Integer> itr = indexes.iterator();
				while (itr.hasNext()) {
					content = content.replace(name.get(itr.next()), "");
				}

				// Delete stop word or length=1 or length=0 word
				String modifiedTweet = normalize(content.split(" "), map);

				// Store information into database
				itr = indexes.iterator();
				while (itr.hasNext()) {
					bwTweet.write(symbol.get(itr.next()) + "\t" + modifiedTweet
							+ "\n");

				}
			}

			for (Entry<String, Integer> e : map.entrySet()) {
				bwWord.write(e.getKey() + "\t" + e.getValue() + "\n");
			}

			bwTweet.close();
			bwWord.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
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

	String normalize(String[] tokens, HashMap<String, Integer> map) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < tokens.length; i++) {
			String token = tokens[i];
			if (token.length() == 0 || token.length() == 1)
				continue;
			else if (!isValidWord(token))
				continue;
			else {
				sb.append(token);
				if (map.containsKey(token)) {
					map.put(token, map.get(token) + 1);
				} else
					map.put(token, 1);
				if (i != tokens.length - 1)
					sb.append(" ");
			}
		}
		return sb.toString();
	}
	String convertFromList(List<String> tokens) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < tokens.size(); i++) {
			sb.append(tokens.get(i));
			if (i != tokens.size() - 1)
				sb.append(" ");
		}
		return sb.toString();
	}

	java.util.Date getDateFromJsonString(String json) throws ParseException {
		JSONObject jsonObj;
		String time = null;
		try {
			jsonObj = new JSONObject(json);
			time = jsonObj.getString("created_at");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZ yyyy")
				.parse(time);
	}

	String getContentFromJsonString(String json) {
		JSONObject jsonObj = null;
		String content = null;
		JSONArray users = null;
		JSONArray urls = null;
		try {
			jsonObj = new JSONObject(json);
			// Check if it is the retweet
			if (jsonObj.has("retweeted_status")) {
				JSONObject retweetObj = jsonObj
						.getJSONObject("retweeted_status");
				content = retweetObj.getString("text");
				JSONObject entity = retweetObj.getJSONObject("entities");
				users = entity.getJSONArray("user_mentions");
				urls = entity.getJSONArray("urls");
			} else {
				content = jsonObj.getString("text");
				JSONObject entity = jsonObj.getJSONObject("entities");
				users = entity.getJSONArray("user_mentions");
				urls = entity.getJSONArray("urls");
			}

			if (content == null)
				return null;
			if (users != null) {
				for (int i = 0; i < users.length(); i++) {
					content = content.replace("@"
							+ users.getJSONObject(i).getString("screen_name"),
							"");
				}
			}
			if (urls != null) {
				for (int i = 0; i < urls.length(); i++) {
					content = content.replace(
							urls.getJSONObject(i).getString("url"), "");
				}
			}
		} catch (JSONException e) {
			// we do not care if we have not find that String just use a null to
			// represent a not find
			// problem.
			return null;
		}
		return content;
	}

}
