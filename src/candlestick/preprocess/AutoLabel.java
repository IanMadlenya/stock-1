package candlestick.preprocess;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import candlestick.models.CandleStickHelper;
import candlestick.models.SingleCandleStick;

public class AutoLabel {

  public static final String[] symbols = {"AAPL", "BAC", "BRK.B", "CVX", "GE", "IBM", "JNJ", "JPM", "MSFT", "PFE", "PG", "T", "WFC", "XOM", "GOOG"};
  public static final String[] years = {"2003", "2004", "2005", "2006", "2007", "2008"};
  public static final int dailyAnalysisPeriod = 10;
  public static final List<String> months = Arrays.asList("06", "07", "08", "09", "10", "11", "12");

  public static void main(String[] args) throws Exception {
    String fileHead = "/Users/none/stock/stockdata/";
    String fileTail = "daily.csv";

    StringBuilder result = new StringBuilder();
    result.append("Label,Symbol,MonthRange,NumWhite,NumBlack\n");

    for (String curSymbol : symbols) {
      for (String curYear : years) {

        String filename = fileHead + curSymbol + curYear + fileTail;
        try {
          BufferedReader br = new BufferedReader(new FileReader(filename));

          String line;
          int count = 0;

          ArrayList<SingleCandleStick> previousSet = new ArrayList<SingleCandleStick>();

          String prevMonth = "13";
          while ((line = br.readLine()) != null) {
            if (!line.contains(curYear)) {
              continue;
            }

            String[] stats = line.split(",");
            String curMonth = stats[0].substring(5, 7);
            boolean ignore = !months.contains(curMonth);

            if (ignore) {
              break;
            }

            if (!curMonth.equals(prevMonth)) {
              if (!previousSet.isEmpty()) {
                addRecord(result, prevMonth, curYear, curSymbol, previousSet);
              }
              count = 0;
              previousSet.clear();
              prevMonth = curMonth;
            }

            if (count < dailyAnalysisPeriod) {
              SingleCandleStick candleStick = new SingleCandleStick(getFloat(stats[1]), getFloat(stats[2]), getFloat(stats[3]), getFloat(stats[4]));
              previousSet.add(candleStick);
            }
            count++;

          }

          addRecord(result, prevMonth, curYear, curSymbol, previousSet);

          br.close();
        } catch (FileNotFoundException e) {
          continue;
        }
      }

    }

    System.out.println(result);

  }

  public static void addRecord(StringBuilder result, String prevMonth, String curYear, String curSymbol, ArrayList<SingleCandleStick> candleSticks) {
    int startMonth = Integer.valueOf(prevMonth) - 5;
    String monthRange = curYear + "0" + startMonth + "-" + curYear + prevMonth;

    CandleStickHelper.Label label = CandleStickHelper.getLabel(candleSticks);
    int numWhite = CandleStickHelper.numWhite(candleSticks);
    int numBlack = CandleStickHelper.numBlack(candleSticks);

    result.append(label + "," + curSymbol + "," + monthRange + "," + numWhite + "," + numBlack + "\n");
  }

  public static float getFloat(String s) {
    return Float.valueOf(s);
  }

}
