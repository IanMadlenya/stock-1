import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

public class GenerateWordle {

	public static void main(String args[]) {
		GenerateWordle gw = new GenerateWordle();
		gw.generate();
	}
	public void generate() {
		HashSet<String> stock = new HashSet<String>();
		Connection con = null;

		String url = "jdbc:mysql://localhost:3306/stock";
		String user = "root";
		String password = "";

		try {
			con = DriverManager.getConnection(url, user, password);

			// Get the registered stock list
			PreparedStatement pst = con
					.prepareStatement("SELECT * FROM stock_stock");
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				String symbol = rs.getString(2);
				stock.add(symbol);
			}

			// Get current date
			Calendar now = Calendar.getInstance();
			now.setTime(new Date());

			Iterator<String> itr = stock.iterator();
			while (itr.hasNext()) {
				String symbol = itr.next();
				BufferedWriter bw = new BufferedWriter(new FileWriter(symbol
						+ ".txt"));
				StringBuffer strbuf = new StringBuffer();
				Calendar curr = Calendar.getInstance();
				curr.setTime(new Date());
				curr.add(Calendar.DAY_OF_MONTH, -3);

				for (; !curr.after(now); curr.add(Calendar.DAY_OF_MONTH, 1)) {
					pst = con
							.prepareStatement("SELECT content FROM stock_twitter WHERE symbol=? AND time=?");
					pst.setString(1, symbol);
					pst.setDate(2, new java.sql.Date(curr.getTimeInMillis()));
					rs = pst.executeQuery();
					while (rs.next()) {
						strbuf.append(rs.getString(1) + " ");
					}
				}

				bw.write(strbuf.toString().trim());
				bw.close();
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
