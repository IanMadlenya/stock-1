package candlestick.getData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;

public class AddField {
	public static void main(String[] args) throws IOException {
		Connection con = null;
		Statement st = null;

		String url = "jdbc:mysql://localhost/capstone";
		String user = "";
		String password = "";

		String path = "/Users/luli/Documents/java_ee_workspace/StockCapstone/companylist.txt";
		String line;

		try {
			con = DriverManager.getConnection(url, user, password);
			st = con.createStatement();
			BufferedReader br = new BufferedReader(new FileReader(path));
			br.readLine(); // ignore first line
			// 6: sector 7: industry 0: symbol

			while ((line = br.readLine()) != null) {
				String[] fields = line.split("\",\"");
				String symbol = fields[0];
				symbol = symbol.substring(1);
				String sector = fields[6];
				String industry = fields[7];
				System.out.println(symbol + ";" + sector + ";" + industry);
				st.executeUpdate("update stockdata set sector='" + sector
						+ "' where symbol in ('" + symbol + "');");
				st.executeUpdate("update stockdata set industry='" + industry
						+ "' where symbol in ('" + symbol + "');");
			}
			br.close();
		} catch (SQLException ex) {
			// TODO Auto-generated catch block
			Logger lgr = Logger.getLogger(StoreIntoDB.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		}

	}
}
