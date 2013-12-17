import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;

public class StoreIntoDB {

	private static ArrayList<String> filelist = new ArrayList<String>();

	@SuppressWarnings("finally")
	public static void main(String[] args) {

		Connection con = null;
		Statement st = null;
		int rs;

		String url = "jdbc:mysql://localhost/capstone";
		String user = "";
		String password = "";

		refreshFileList("/Users/luli/Documents/java_ee_workspace/StockCapstone/stockdatatwo");

		try {
			con = DriverManager.getConnection(url, user, password);
			st = con.createStatement();

			for (int i = 0; i != filelist.size(); i++) {
				String currentFile = filelist.get(i);
				int symbolIndex = currentFile.indexOf("20");
				int lastSlash = currentFile.lastIndexOf("/");
				// System.out.println(currentFile);
				// System.out.println(symbolIndex + " --- " + lastSlash);
				try {
					String symbol = currentFile.substring(lastSlash + 1,
							symbolIndex);

					BufferedReader br;
					br = new BufferedReader(new FileReader(currentFile));
					br.readLine(); // remove the first line
					String line;
					while ((line = br.readLine()) != null) {
						String[] fields = line.split(",");
						String date = fields[0];
						float open = Float.parseFloat(fields[1]);
						float high = Float.parseFloat(fields[2]);
						float low = Float.parseFloat(fields[3]);
						float close = Float.parseFloat(fields[4]);
						int volumn = Integer.parseInt(fields[5]);
						float adjClose = Float.parseFloat(fields[6]);

						rs = st.executeUpdate("INSERT INTO stockdata VALUES ('"
								+ symbol + "',null,null,'" + date + "','" + open + "','"
								+ high + "','" + low + "','" + close + "','"
								+ volumn + "','" + adjClose + "')");
					}
					br.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					continue;
				}
			}

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(StoreIntoDB.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			try {
				if (st != null) {
					st.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(StoreIntoDB.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}

	public static void refreshFileList(String strPath) {
		File dir = new File(strPath);
		File[] files = dir.listFiles();

		if (files == null)
			return;
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				refreshFileList(files[i].getAbsolutePath());
			} else {
				String strFileName = files[i].getAbsolutePath().toLowerCase();
				System.out.println(strFileName);
				filelist.add(files[i].getAbsolutePath());
			}
		}
	}
}