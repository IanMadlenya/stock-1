import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

public class UpdateDatabase {
	public static void main(String[] args) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		String url = "jdbc:mysql://localhost:3306/stock";
		String user = "root";
		String password = "";

		DownloadPrice dp = new DownloadPrice();
		NewsCrawler nc = new NewsCrawler();

		try {
			con = DriverManager.getConnection(url, user, password);
			// Get the registered stock list
			pst = con.prepareStatement("SELECT * FROM stock_stock");
			rs = pst.executeQuery();

			while (rs.next()) {
				int id = rs.getInt(1);
				String symbol = rs.getString(2);

				// Get the latest price update date for this stock
				pst = con
						.prepareStatement("SELECT date FROM stock_price WHERE stock_id=? ORDER BY date DESC LIMIT 1");
				System.out.println(id);
				pst.setInt(1, id);
				ResultSet rsTime = pst.executeQuery();
				Date d = null;
				while (rsTime.next()) {
					d = rsTime.getDate(1);
				}
				if (d == null)
					dp.Download(symbol, id, null, pst, con);
				else {
					Calendar c = Calendar.getInstance();
					c.setTime(d);
					dp.Download(symbol, id, c, pst, con);
				}

				// Get the latest news update date for this stock
				pst = con
						.prepareStatement("SELECT date FROM stock_news WHERE stock_id=? ORDER BY date DESC LIMIT 1");
				pst.setInt(1, id);
				rsTime = pst.executeQuery();
				d = null;
				while (rsTime.next()) {
					d = rsTime.getDate(1);
				}

				if (d == null)
					nc.crawlWebpage(symbol, id, null, pst, con);
				else {
					Calendar c = Calendar.getInstance();
					c.setTime(d);
					nc.crawlWebpage(symbol, id, c, pst, con);
				}

			}
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
