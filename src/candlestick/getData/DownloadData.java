package candlestick.getData;

import java.io.*;
import java.net.*;

public class DownloadData {
	@SuppressWarnings("finally")
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(
				new FileReader(
						"/Users/luli/Documents/java_ee_workspace/StockCapstone/500symbol.txt"));

		// daily, weekly or monthly
		String interval = "daily";

		int startYear = 2013; // start year
		int endYear = 2013; // end year

		String line;

		while ((line = br.readLine()) != null) {

			String symbol = line;

			for (int i = startYear; i <= endYear; i++) {

				String fromDate = "11/1/" + i; // start date
				String toDate = "11/30/" + i; // end date

				int[] fd = processDate(fromDate);
				int[] td = processDate(toDate);

				String path = "http://ichart.yahoo.com/table.csv?s=" + symbol
						+ "&a=" + fd[0] + "&b=" + fd[1] + "&c=" + fd[2] + "&d="
						+ td[0] + "&e=" + td[1] + "&f=" + td[2] + "&g="
						+ interval.charAt(0) + "&ignore=.csv";

				URL url = new URL(path);
				HttpURLConnection httpCon;
				try {
					httpCon = (HttpURLConnection) url.openConnection();
					InputStream in = httpCon.getInputStream();
					String filePath = "/Users/luli/Documents/java_ee_workspace/StockCapstone/201311/"
							+ symbol + fd[2] + interval + ".csv";
					OutputStream out = new FileOutputStream(filePath);
					out = new BufferedOutputStream(out);
					byte[] buf = new byte[8192];
					int len = 0;
					while ((len = in.read(buf)) != -1) {
						out.write(buf, 0, len);
					}
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					continue;
				}
			}
		}
		br.close();
	}

	public static int[] processDate(String date) {
		if (date == null || date.isEmpty())
			return null;
		int[] result = new int[3];
		String[] fields = date.split("/");
		// fields 0: date, 1: month, 2: year
		for (int i = 0; i != fields.length; i++)
			result[i] = Integer.parseInt(fields[i]);
		result[0] = result[0] - 1;
		return result;
	}
}
