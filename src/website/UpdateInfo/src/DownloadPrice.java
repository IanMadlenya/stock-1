import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Download the stock price for one company
 * 
 * @author shijieru
 * 
 */
public class DownloadPrice {
	// daily, weekly or monthly
	String interval = "daily";

	public void Download(String symbol, int stockId, Calendar storedDate,
			PreparedStatement pst, Connection con) throws IOException {
		Calendar begin = Calendar.getInstance();
		begin.setTime(new Date());
		begin.add(Calendar.DAY_OF_MONTH, -45);

		String fromDate = null;
		String toDate = null;
		if (storedDate == null || begin.after(storedDate)) {
			fromDate = (begin.get(Calendar.MONTH) + 1) + "/"
					+ begin.get(Calendar.DAY_OF_MONTH) + "/"
					+ begin.get(Calendar.YEAR);
		} else {
			storedDate.add(Calendar.DAY_OF_MONTH, 1);
			fromDate = (storedDate.get(Calendar.MONTH) + 1) + "/"
					+ storedDate.get(Calendar.DAY_OF_MONTH) + "/"
					+ storedDate.get(Calendar.YEAR);
		}
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		toDate = (now.get(Calendar.MONTH) + 1) + "/"
				+ now.get(Calendar.DAY_OF_MONTH) + "/" + now.get(Calendar.YEAR);

		int[] fd = processDate(fromDate);
		int[] td = processDate(toDate);

		String path = "http://ichart.yahoo.com/table.csv?s="
				+ symbol.substring(1) + "&a=" + fd[0] + "&b=" + fd[1] + "&c="
				+ fd[2] + "&d=" + td[0] + "&e=" + td[1] + "&f=" + td[2] + "&g="
				+ interval.charAt(0) + "&ignore=.csv";

		URL url = new URL(path);
		HttpURLConnection httpCon;
		try {
			httpCon = (HttpURLConnection) url.openConnection();
			InputStream in = httpCon.getInputStream();
			CSVReader reader = new CSVReader(new InputStreamReader(in));
			String[] nextLine;
			reader.readNext();
			while ((nextLine = reader.readNext()) != null) {
				String[] parts = nextLine[0].split("-");
				Calendar c = Calendar.getInstance();
				c.set(Integer.valueOf(parts[0]), Integer.valueOf(parts[1]) - 1,
						Integer.valueOf(parts[2]));
				// nextLine[] is an array of values from the line
				pst = con
						.prepareStatement("INSERT INTO stock_price(stock_id, date, open, close, high, low, volume) VALUES (?,?,?,?,?,?,?)");
				pst.setInt(1, stockId);
				pst.setDate(2, new java.sql.Date(c.getTimeInMillis()));
				pst.setFloat(3, Float.valueOf(nextLine[1]));
				pst.setFloat(4, Float.valueOf(nextLine[4]));
				pst.setFloat(5, Float.valueOf(nextLine[2]));
				pst.setFloat(6, Float.valueOf(nextLine[3]));
				pst.setFloat(7, Float.valueOf(nextLine[5]));
				pst.executeUpdate();
			}
			in.close();
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
