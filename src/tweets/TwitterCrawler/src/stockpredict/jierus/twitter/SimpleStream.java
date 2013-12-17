package stockpredict.jierus.twitter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class SimpleStream {
	public static void main(String[] args) {
		PreparedStatement pst = null;
		ResultSet rs = null;

		ArrayList<String> stock = new ArrayList<String>();

		// Read stock information
		String url = "jdbc:mysql://localhost:3306/stock";
		String user = "shijieru";
		String password = "20021228";

		final int BATCH_SIZE = 5;

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();

			final Connection con = DriverManager.getConnection(url, user,
					password);

			con.setAutoCommit(false);
			con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);

			// Get the registered stock list
			pst = con.prepareStatement("SELECT * FROM stock_stock");
			rs = pst.executeQuery();

			while (rs.next()) {
				String symbol = rs.getString(2);
				String name = rs.getString(3);
				stock.add(symbol);
				stock.add(name);
			}

			final ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true);
			// Conf should not be hardcoded
			cb.setOAuthConsumerKey("gbRLLsXnaCpj9iw4eJyQ");
			cb.setOAuthConsumerSecret("CBFOOexciGYg8bGjwJtrXEuBS8C7SqO1JK9CRkjdi0");
			cb.setOAuthAccessToken("828049309-a5GVNBN5nxew3jybY0EHSDZQsBMJ0vn3y0OFjSLO");
			cb.setOAuthAccessTokenSecret("UKSiJrXZbPiE267Lmk3WPwqZr7ecjP5JlwaFHhTGstg");

			TwitterStream twitterStream = new TwitterStreamFactory(cb.build())
					.getInstance();

			StatusListener listener = new StatusListener() {
				int counter = 0;
				PreparedStatement pst;

				@Override
				public void onException(Exception arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onDeletionNotice(StatusDeletionNotice arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onScrubGeo(long arg0, long arg1) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onStatus(Status status) {
					counter++;
					try {
						pst = con
								.prepareStatement("INSERT INTO temp_twitter VALUES(?,?)");
						pst.setDate(1, new java.sql.Date(status.getCreatedAt()
								.getTime()));
						pst.setString(2, status.getText());
						pst.addBatch();
						if (counter == BATCH_SIZE) {
							pst.executeBatch();
							counter = 0;
							con.commit();
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						System.out.println(e.getMessage());
						e.printStackTrace();
					}
				}

				@Override
				public void onTrackLimitationNotice(int arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onStallWarning(StallWarning arg0) {
					// TODO Auto-generated method stub

				}

			};

			if (stock.size() == 0)
				return;

			String[] stocks = new String[stock.size()];
			for (int i = 0; i < stocks.length; i++) {
				stocks[i] = stock.get(i);
			}

			FilterQuery fq = new FilterQuery();
			fq.language(new String[]{"en"});
			fq.track(stocks);

			twitterStream.addListener(listener);
			twitterStream.filter(fq);
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
}