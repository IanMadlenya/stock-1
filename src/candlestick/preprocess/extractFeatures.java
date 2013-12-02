package candlestick.preprocess;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import candlestick.models.CandleRecognizer;
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
    String filename = "/Users/none/stock/outputfinalfull.csv";
    BufferedWriter bw = new BufferedWriter(new FileWriter(filename));

    StringBuilder title = new StringBuilder();
    title.append("label,symbol,time");
    for (int i = 1; i <= PatternRecognizer.FEATURE_NUM + 2; i++) {
      title.append(",f" + i);
    }
    bw.write(title.toString());
    bw.newLine();

    for (String symbol : symbolList) {
      for (String year : yearList) {
        for (int i = 1; i <= 10; i++) {
          ArrayList<SingleCandle> candles = new ArrayList<SingleCandle>();
          String query = "SELECT * FROM stockdata WHERE symbol = \"" + symbol + "\" AND MONTH(date) BETWEEN " + i +
              " AND " + (i + 2) + " AND YEAR(date) = " + year + " order by date desc limit " + (CandleRecognizer.AVG_PERIOD * 2 + 1);
          rs = stmt.executeQuery(query);

          String sector = "";
          String industry = "";
          String date = "";

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

            if (date.isEmpty()) {
              date = rs.getString("date");
            }

            double open = Double.parseDouble(rs.getString("open"));
            double high = Double.parseDouble(rs.getString("high"));
            double low = Double.parseDouble(rs.getString("low"));
            double close = Double.parseDouble(rs.getString("close"));
            int volumn = Integer.parseInt(rs.getString("volumn"));
            SingleCandle candle = new SingleCandle(open, high, low, close, volumn);
            candles.add(candle);
          }

          if (date.isEmpty()) {
            continue;
          }

          String fromMonth = i > 9 ? year + i : year + "0" + i;
          String toMonth = (i + 2) > 9 ? year + (i + 2) : year + "0" + (i + 2);
          String time = fromMonth + "-" + toMonth;

          if (!date.substring(5, 7).equals(toMonth.substring(4, 6))) {
            continue;
          }

          Instance instance = new Instance(candles, symbol, time, sector, industry);

          SingleCandle.TrendType label = null;
          int note = -1;
          if (candles.size() < CandleRecognizer.AVG_PERIOD + 1 && candles.size() > 1) {
            double sum = 0;
            for (int j = 1; j < candles.size(); j++) {
              sum += candles.get(j).close;
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
