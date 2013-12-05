import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

		try {
			con = DriverManager.getConnection(url, user, password);

			pst = con.prepareStatement("SELECT * FROM stock_stock");
			rs = pst.executeQuery();

			while (rs.next()) {
				int id = rs.getInt(1);
				String symbol = rs.getString(2);
				String name = rs.getString(3);
				// Get the latest time for this stock
				pst = con
						.prepareStatement("SELECT date FROM stock_news WHERE stock_id=? ORDER BY date DESC LIMIT 1");
				pst.setInt(1, id);
				ResultSet rsTime = pst.executeQuery();
				Date d = null;
				while (rsTime.next()) {
					d = rsTime.getDate(1);
				}
				if (d == null)
					dp.Download(symbol,id, null,pst,con);
				else {
					Calendar c = Calendar.getInstance();
					c.setTime(rsTime.getDate(1));
					dp.Download(symbol,id, c,pst,con);
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
