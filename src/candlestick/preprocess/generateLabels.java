package candlestick.preprocess;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import candlestick.models.CandleRecognizer;
import candlestick.models.SingleCandle;

public class generateLabels {
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

  public static ArrayList<String> getDateList() throws Exception {
    ArrayList<String> dateList = new ArrayList<String>();
    rs = stmt.executeQuery("SELECT distinct date FROM stockdata WHERE year(date) = 2013 and month(date) between 7 and 8 order by date asc");
    while (rs.next()) {
      String date = rs.getString("date");
      dateList.add(date);
    }

    return dateList;
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

  public static void main(String[] args) throws Exception {
    init();
    String filename = "/Users/none/stock/78Labels.txt";
    BufferedWriter bw = new BufferedWriter(new FileWriter(filename));

    ArrayList<String> symbolList = getSymbolList();
    ArrayList<String> dateList = getDateList();

    StringBuilder title = new StringBuilder();
    title.append("symbol,date,label");
    bw.write(title.toString());
    bw.newLine();

    for (String symbol : symbolList) {
      while (!dateList.isEmpty()) {
        String date = dateList.get(0);
        dateList.remove(0);
        ArrayList<SingleCandle> candles = new ArrayList<SingleCandle>();
        String query = "SELECT * FROM stockdata WHERE symbol = \"" + symbol + "\" AND date <= date(\"" +
            date + "\") order by date desc limit 11";
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

        SingleCandle.TrendType label = null;
        String note = "";
        if (candles.size() < CandleRecognizer.AVG_PERIOD + 1 && candles.size() > 1) {
          double sum = 0;
          for (int i = 1; i < candles.size(); i++) {
            sum += candles.get(i).close;
          }
          sum = sum / (candles.size() - 1);
          note = ",Less than 10 previous candles";
          if (candles.get(0).close > sum) {
            label = SingleCandle.TrendType.UP;
          } else if (candles.get(0).close < sum) {
            label = SingleCandle.TrendType.DOWN;
          } else {
            label = SingleCandle.TrendType.KEEP;
          }
        } else if (candles.size() == 1) {
          note = ",First price";
          label = SingleCandle.TrendType.KEEP;
        } else if (candles.size() == 0) {
          note = ",No price";
        } else {
          CandleRecognizer cur = new CandleRecognizer(candles);
          label = cur.getResult().trend;
        }

        if (label != null) {

          StringBuffer result = new StringBuffer();
          result.append(symbol + ",");
          result.append(date + ",");
          result.append(label);
          result.append(note);

          //            System.out.println(result);
          bw.write(result.toString());
          bw.newLine();
        }

      }
      dateList = getDateList();
    }

    bw.close();
  }
}
