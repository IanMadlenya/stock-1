import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

public class ComputeWordPercentage {
	public static void main(String[] args) {
		String line = null;
		BufferedReader br = null;
		BufferedWriter bw = null;
		File file = null;
		String labelFile = args[0];
		String featureFile = args[1];

		// Stop words
		HashSet<String> stopwords = new HashSet<String>();
		// Positive words
		HashSet<String> poswords = new HashSet<String>();
		// Negative words
		HashSet<String> negwords = new HashSet<String>();

		try {
			// Read stop word list
			br = new BufferedReader(new FileReader("stop_words.txt"));
			while ((line = br.readLine()) != null) {
				stopwords.add(line);
			}
			br.close();
			// Read positive word list
			br = new BufferedReader(new FileReader("positive-words.txt"));
			while ((line = br.readLine()) != null) {
				poswords.add(line);
			}
			br.close();
			// Read negative word list
			br = new BufferedReader(new FileReader("negative-words.txt"));
			while ((line = br.readLine()) != null) {
				negwords.add(line);
			}
			br.close();

			// Read label for each day and each symbol
			br = new BufferedReader(new FileReader(labelFile));
			bw = new BufferedWriter(new FileWriter(featureFile));

			bw.write("pos1,neg1,pos2,neg2,pos3,neg3,label\n");
			br.readLine();
			while ((line = br.readLine()) != null) {
				// One instance
				// 0 symbol, 1 date, 2 label, 3 something
				String[] parts = line.split(",");
				// Only want June data
				if (parts[1].contains("2013-06")
						|| parts[1].contains("2013-10")) {
					String[] dates = parts[1].split("-");
					int dayOfMonth = Integer.valueOf(dates[dates.length - 1]);
					if (dayOfMonth == 1)
						continue;
					if (parts[2].equals("null")) {
						continue;
					}

					StringBuffer strBuf = new StringBuffer();
					int countInvalid = 0;

					// Find three days tweets and group them into one line tweet
					BufferedReader brTweet = null;
					int beginDay = -1;
					if ((dayOfMonth - 3) >= 1) {
						beginDay = dayOfMonth - 3;
					} else {
						beginDay = 1;
						for (int i = 0; i < (3 - (dayOfMonth - beginDay)); i++) {
							strBuf.append("0.0,0.0,");
							countInvalid++;
						}
					}

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
							int count = 0;
							double pos = 0;
							double neg = 0;
							while ((tweet = brTweet.readLine()) != null) {
								for (String token : tweet.split(" ")) {
									if (!stopwords.contains(token)) {
										count++;
										if (poswords.contains(token))
											pos++;
										if (negwords.contains(token))
											neg++;
									}
								}
							}
							brTweet.close();
							strBuf.append(pos / count + "," + neg / count + ",");
						} else {
							strBuf.append("0.0,0.0,");
							countInvalid++;
						}
					}
					if (countInvalid == 3)
						continue;

					bw.write(strBuf.toString());
					bw.write(parts[2] + "\n");

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
