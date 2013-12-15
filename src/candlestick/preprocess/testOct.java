package candlestick.preprocess;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

import candlestick.models.CandleRecognizer;
import candlestick.models.Instance;
import candlestick.models.PatternRecognizer;
import candlestick.models.SingleCandle;

public class testOct {
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
    ArrayList<String> dateList = getDateList();
    String filename = "/Users/none/stock/src/candlestick/extractedFeatures/test.csv";
    BufferedWriter bw = new BufferedWriter(new FileWriter(filename));

    StringBuilder title = new StringBuilder();
    title.append("label,symbol,time");
    for (int i = 1; i <= PatternRecognizer.FEATURE_NUM + 2; i++) {
      title.append(",f" + i);
    }
    bw.write(title.toString());
    bw.newLine();

    for (String symbol : symbolList) {
      while (!dateList.isEmpty()) {
        String date = dateList.get(0);
        dateList.remove(0);
        ArrayList<SingleCandle> candles = new ArrayList<SingleCandle>();
        String query = "SELECT * FROM stockdata WHERE symbol = \"" + symbol + "\" AND date <= date(\"" +
            date + "\") order by date desc limit " + (CandleRecognizer.AVG_PERIOD * 2 + 1);
        rs = stmt.executeQuery(query);

        String sector = "";
        String industry = "";

        while (rs.next()) {
          if (sector.isEmpty()) {
            sector = rs.getString("sector");
          }

          if (industry.isEmpty()) {
            industry = rs.getString("industry");
            if (industry.contains(",")) {
              industry = industry.replaceAll(",", " ");
            }
          }

          double open = Double.parseDouble(rs.getString("open"));
          double high = Double.parseDouble(rs.getString("high"));
          double low = Double.parseDouble(rs.getString("low"));
          double close = Double.parseDouble(rs.getString("close"));
          int volumn = Integer.parseInt(rs.getString("volumn"));
          SingleCandle candle = new SingleCandle(open, high, low, close, volumn);
          candles.add(candle);
        }

        Instance instance = new Instance(candles, symbol, date, sector, industry);

        SingleCandle.TrendType label = null;
        int note = -1;
        if (candles.size() < CandleRecognizer.AVG_PERIOD + 1 && candles.size() > 1) {
          double sum = 0;
          for (int i = 1; i < candles.size(); i++) {
            sum += candles.get(i).close;
          }
          sum = sum / (candles.size() - 1);
          // note = ",Less than 10 previous candles";
          note = 0;
          if (candles.get(0).close > sum) {
            label = SingleCandle.TrendType.UP;
          } else if (candles.get(0).close < sum) {
            label = SingleCandle.TrendType.DOWN;
          } else {
            label = SingleCandle.TrendType.KEEP;
          }
        } else if (candles.size() == 1) {
          // note = ",First price";
          note = 1;
          label = SingleCandle.TrendType.KEEP;
        } else if (candles.size() == 0) {
          // note = ",No price";
          note = 2;
        } else {
          label = instance.getLabel();
        }

        instance.setLabel(label);
        StringBuffer result = new StringBuffer();

        if (instance.getLabel() != null) {
          result.append(instance.getLabel());
          result.append("," + instance.getSymbol());
          result.append("," + instance.getTime());
          int[] features = instance.getFeatures();
          for (int k = 0; k < PatternRecognizer.FEATURE_NUM; k++) {
            result.append("," + features[k]);
          }
          result.append("," + sector);
          result.append("," + industry);

          //            System.out.println(result);
          bw.write(result.toString());
          bw.newLine();
        }
      }
      dateList = getDateList();
    }

    bw.close();
  }

  public static ArrayList<String> getDateList() throws Exception {
    ArrayList<String> dateList = new ArrayList<String>();
    rs = stmt.executeQuery("SELECT distinct date FROM stockdata WHERE year(date) = 2013 and month(date) = 10 order by date desc limit 10");
    while (rs.next()) {
      String date = rs.getString("date");
      dateList.add(date);
    }

    Collections.reverse(dateList);

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
}
