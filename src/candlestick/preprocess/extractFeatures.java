package candlestick.preprocess;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import candlestick.models.Instance;
import candlestick.models.PatternRecognizer;
import candlestick.models.SingleCandle;

public class extractFeatures {
  public static Connection conn = null;
  public static Statement stmt = null;
  public static ResultSet rs = null;

  public static void init() throws Exception {
    Class.forName("com.mysql.jdbc.Driver").newInstance();
    String connectionUrl = "jdbc:mysql://localhost:3306/capstone";
    String connectionUser = "root";
    String connectionPassword = "gongteng";
    conn = DriverManager.getConnection(connectionUrl, connectionUser, connectionPassword);
    stmt = conn.createStatement();
  }

  public static void main(String[] args) throws Exception {
    init();
    ArrayList<String> symbolList = getSymbolList();
    ArrayList<String> yearList = getYearList();
    String filename = "/Users/none/stock/outputfull2.csv";
    BufferedWriter bw = new BufferedWriter(new FileWriter(filename));

    StringBuilder title = new StringBuilder();
    title.append("label");
    for (int i = 1; i <= PatternRecognizer.FEATURE_NUM; i++) {
      title.append(",f" + i);
    }
    bw.write(title.toString());
    bw.newLine();

    for (String symbol : symbolList) {
      for (String year : yearList) {
        for (int i = 1; i <= 7; i++) {
          ArrayList<SingleCandle> candles = new ArrayList<SingleCandle>();
          String query = "SELECT * FROM stockdata WHERE symbol = \"" + symbol + "\" AND MONTH(date) BETWEEN " + i +
              " AND " + (i + 5) + " AND YEAR(date) = " + year + " order by date desc";
          rs = stmt.executeQuery(query);
          while (rs.next()) {
            double open = Double.parseDouble(rs.getString("open"));
            double high = Double.parseDouble(rs.getString("high"));
            double low = Double.parseDouble(rs.getString("low"));
            double close = Double.parseDouble(rs.getString("close"));
            int volumn = Integer.parseInt(rs.getString("volumn"));
            SingleCandle candle = new SingleCandle(open, high, low, close, volumn);
            candles.add(candle);
          }
          String toMonth = (i + 5) > 9 ? year + (i + 5) : year + "0" + (i + 5);
          String time = year + "0" + i + "-" + toMonth;
          Instance instance = new Instance(candles, symbol, time);
          StringBuffer result = new StringBuffer();
          if (instance.getLabel() != null) {
            result.append(instance.getLabel());
            int[] features = instance.getFeatures();
            for (int k = 0; k < PatternRecognizer.FEATURE_NUM; k++) {
              result.append("," + features[k]);
            }
            //            System.out.println(result);
            bw.write(result.toString());
            bw.newLine();
          }
        }
      }
    }
    bw.close();
  }

  public static ArrayList<String> getSymbolList() throws Exception {
    ArrayList<String> symbolList = new ArrayList<String>();
    rs = stmt.executeQuery("SELECT distinct symbol FROM stockdata");
    while (rs.next()) {
      String symbol = rs.getString("symbol");
      symbolList.add(symbol);
    }

    return symbolList;
  }

  public static ArrayList<String> getYearList() throws Exception {
    ArrayList<String> yearList = new ArrayList<String>();
    rs = stmt.executeQuery("SELECT distinct year(date) as year FROM stockdata");
    while (rs.next()) {
      String year = rs.getString("year");
      yearList.add(year);
    }

    return yearList;
  }
}
