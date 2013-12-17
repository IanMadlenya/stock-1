package candlestick.models;

import java.util.ArrayList;

public class CandleRecognizer {

  public static int AVG_PERIOD = 10;
  private SingleCandle result;

  public CandleRecognizer(ArrayList<SingleCandle> candles) {
    if (candles.size() != AVG_PERIOD + 1) {
      System.out.println("not enough candles");
    } else {
      result = candles.get(0);

      // set Trend
      double avg = getMA(candles, AVG_PERIOD);
      if (avg < result.close) {
        result.setTrend(SingleCandle.TrendType.UP);
      } else if (avg > result.close) {
        result.setTrend(SingleCandle.TrendType.DOWN);
      } else {
        result.setTrend(SingleCandle.TrendType.KEEP);
      }

      // set bull
      boolean bull = (result.open < result.close) ? true : false;
      result.setBull(bull);

      // set bodysize
      result.setBodysize(Math.abs(result.open - result.close));

      // get size of the shadows
      double shade_low = result.close - result.low;
      double shade_high = result.high - result.open;
      if (result.bull) {
        shade_low = result.open - result.low;
        shade_high = result.high - result.close;
      }
      double HL = result.high - result.low;

      double sum = 0;
      for (int i = 1; i <= AVG_PERIOD; i++) {
        sum += Math.abs(candles.get(i).open - candles.get(i).close);
      }
      sum = sum / AVG_PERIOD;

      // set type
      if (result.bodysize > sum * 1.3) {
        result.setType(SingleCandle.CandleType.TYPE_LONG);
      } else if (result.bodysize < sum * 0.5) {
        result.setType(SingleCandle.CandleType.TYPE_SHORT);
      } else if (result.bodysize < HL * 0.03) {
        result.setType(SingleCandle.CandleType.TYPE_DOJI);
      } else if ((shade_low < result.bodysize * 0.01 || shade_high < result.bodysize * 0.01) && result.bodysize > 0) {
        if (result.type == SingleCandle.CandleType.TYPE_LONG) {
          result.setType(SingleCandle.CandleType.TYPE_MARIBOZU_LONG);
        } else {
          result.setType(SingleCandle.CandleType.TYPE_MARIBOZU);
        }
      } else if (shade_low > result.bodysize * 2 && shade_high < result.bodysize * 0.1) {
        result.setType(SingleCandle.CandleType.TYPE_HAMMER);
      } else if (shade_low < result.bodysize * 0.1 && shade_high > result.bodysize * 2) {
        result.setType(SingleCandle.CandleType.TYPE_INVERT_HAMMER);
      } else if (result.type == SingleCandle.CandleType.TYPE_SHORT && shade_low > result.bodysize && shade_high > result.bodysize) {
        result.setType(SingleCandle.CandleType.TYPE_SPIN_TOP);
      } else {
        result.setType(SingleCandle.CandleType.TYPE_NONE);
      }

    }
  }

  public static double getMA(ArrayList<SingleCandle> candles, int period) {
    double avg = 0;
    for (int i = 0; i < period; i++) {
      avg += candles.get(i).close;
    }

    avg = avg / period;

    return avg;
  }

  public SingleCandle getResult() {
    return result;
  }
}
