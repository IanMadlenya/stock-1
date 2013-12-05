import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class GroupText {
	public static void main(String[] args) {
		String line = null;
		BufferedReader br = null;
		BufferedWriter bw = null;
		File file = null;
		String labelFile = args[0];
		String groupFile = args[1];
		String datePattern = args[2];

		// Existing stock symbol
		ArrayList<String> stockSet = new ArrayList<String>();
		// Stop words
		HashSet<String> stopwords = new HashSet<String>();

		try {

			// Read existing company symbol
			br = new BufferedReader(new FileReader("company"));
			while ((line = br.readLine()) != null) {
				stockSet.add(line.substring(1));
			}
			br.close();
			// Read stop word list
			br = new BufferedReader(new FileReader("stop_words.txt"));
			while ((line = br.readLine()) != null) {
				stopwords.add(line);
			}
			br.close();

			// Read label for each day and each symbol
			br = new BufferedReader(new FileReader(labelFile));
			bw = new BufferedWriter(new FileWriter(groupFile));

			bw.write("symbol,date,tweet,label\n");
			br.readLine();
			while ((line = br.readLine()) != null) {
				// One instance
				// 0 symbol, 1 date, 2 label, 3 something
				String[] parts = line.split(",");
				// Only want June data
				if (parts[1].contains(datePattern)
						&& stockSet.contains(parts[0])) {
					String[] dates = parts[1].split("-");
					int dayOfMonth = Integer.valueOf(dates[dates.length - 1]);
					if (dayOfMonth == 1)
						continue;
					if (parts[2].equals("null")) {
						continue;
					}

					// Find three days tweets and group them into one line tweet
					BufferedReader brTweet = null;
					int beginDay = (dayOfMonth - 3) >= 1 ? (dayOfMonth - 3) : 1;
					StringBuffer totalTweet = new StringBuffer();
					for (int i = beginDay; i < dayOfMonth; i++) {
						String day = null;
						if (i < 10)
							day = "0" + i;
						else
							day = "" + i;
						file = new File("$" + parts[0] + "_" + dates[0] + "-"
								+ dates[1] + "-" + day + ".txt");
						if (file.exists()) {
							brTweet = new BufferedReader(new FileReader(file));
							String tweet = null;
							while ((tweet = brTweet.readLine()) != null) {
								for (String token : tweet.split(" ")) {
									if (!stopwords.contains(token)) {
										totalTweet.append(token + " ");
									}
								}
							}
							brTweet.close();
						}
					}
					if (totalTweet.toString().trim().equals("")) {
						continue;
					}
					bw.write(parts[0] + "," + parts[1] + ","
							+ totalTweet.toString().trim() + "," + parts[2]
							+ "\n");
				}
			}
			br.close();
			bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
