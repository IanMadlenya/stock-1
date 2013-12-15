package candlestick.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class PatternRecognizer {
  public static int FEATURE_NUM = 63;
  private int[] features = new int[FEATURE_NUM];

  public PatternRecognizer(ArrayList<SingleCandle> candles) {
    Arrays.fill(features, 0);
    // time seq
    Collections.reverse(candles);

    for (int i = 0; i < candles.size(); i++) {
      oneCandlePattern(candles.get(i));

      if (i < candles.size() - 1) {
        twoCandlePattern(candles.get(i), candles.get(i + 1));
        twoCandleVolumn(candles.get(i), candles.get(i + 1));
      }

      if (i < candles.size() - 2) {
        threeCandlePattern(candles.get(i), candles.get(i + 1),
            candles.get(i + 2));
      }

      if (i < candles.size() - 3) {
        fourCandlePattern(candles.get(i), candles.get(i + 1),
            candles.get(i + 2), candles.get(i + 3));
      }

      if (i < candles.size() - 4) {
        fiveCandlePattern(candles.get(i), candles.get(i + 1),
            candles.get(i + 2), candles.get(i + 3),
            candles.get(i + 4));
      }
    }

    calculateRSV(candles);
    calculateRSI(candles);

    // feature[62]: how many previous days
    features[62] = candles.size();
  }

  public void calculateRSV(ArrayList<SingleCandle> candles) {
    int size = candles.size();
    if (size == 0) {
      return;
    }
    double min = Double.MAX_VALUE;
    double max = Double.MIN_VALUE;
    for (int i = 0; i != size; i++) {
      SingleCandle sc = candles.get(i);
      if (sc.low < min) {
        min = sc.low;
      }
      if (sc.high > max) {
        max = sc.high;
      }
    }
    double currentClose = candles.get(size - 1).close;
    double rsv = (currentClose - min) / (max - min) * 100;
    if (rsv >= 80) {
      // features[56]: rsv >= 80
      features[56]++;
      // feature[59]: bear count
      features[59]++;
    }
    if (rsv <= 20) {
      // features[56]: rsv <= 20
      features[57]++;
      // feature[58]: bull count
      features[58]++;
    }
  }

  public void calculateRSI(ArrayList<SingleCandle> candles) {
    int size = candles.size();
    if (size == 0) {
      return;
    }
    double increaseSum = 0;
    double decreaseSum = 0;
    for (int i = 1; i < size; i++) {
      SingleCandle candle1 = candles.get(i - 1);
      SingleCandle candle2 = candles.get(i);
      double diff = candle2.close - candle1.close;

      if (diff >= 0) {
        increaseSum += diff;
      } else {
        decreaseSum += Math.abs(diff);
      }
    }

    double rsi = 100 * increaseSum / (increaseSum + decreaseSum);

    if (rsi >= 70) {
      // features[60]: rsi >= 70
      features[60]++;
      // feature[59]: bear count
      features[59]++;
    }
    if (rsi <= 30) {
      // features[61]: rsi <= 30
      features[61]++;
      // feature[58]: bull count
      features[58]++;
    }
  }

  public void twoCandleVolumn(SingleCandle candle1, SingleCandle candle2) {
    // feature[54]: sudden volumn increase
    if (candle2.volumn > candle1.volumn * 2) {
      features[54]++;
    }

    // feature[55]: sudden volumn decrease
    if (candle2.volumn * 2 < candle1.volumn) {
      features[55]++;
    }
  }

  public void oneCandlePattern(SingleCandle candle) {
    // inverted hammer the bull
    if (candle.trend == SingleCandle.TrendType.DOWN
        && candle.type == SingleCandle.CandleType.TYPE_INVERT_HAMMER) {
      features[0]++;
      features[58]++;
    }

    // hanging man the bear
    if (candle.trend == SingleCandle.TrendType.UP
        && candle.type == SingleCandle.CandleType.TYPE_HAMMER) {
      features[1]++;
      features[59]++;
    }

    // hammer the bull
    if (candle.trend == SingleCandle.TrendType.DOWN
        && candle.type == SingleCandle.CandleType.TYPE_HAMMER) {
      features[2]++;
      features[58]++;
    }
  }

  public void twoCandlePattern(SingleCandle candle1, SingleCandle candle2) {
    // shooting star the bear
    if (candle1.trend == SingleCandle.TrendType.UP
        && candle2.trend == SingleCandle.TrendType.UP
        && candle2.type == SingleCandle.CandleType.TYPE_INVERT_HAMMER) {
      features[3]++;
      features[59]++;
    }

    // belt hold the bull
    if (candle2.trend == SingleCandle.TrendType.DOWN && candle2.bull
        && !candle1.bull
        && candle2.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG
        && candle1.bodysize < candle2.bodysize
        && candle2.close < candle1.close) {
      features[4]++;
      features[58]++;
    }

    // belt hold the bear
    if (candle2.trend == SingleCandle.TrendType.UP && !candle2.bull
        && candle1.bull
        && candle2.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG
        && candle1.bodysize < candle2.bodysize
        && candle2.close > candle1.close) {
      features[5]++;
      features[59]++;
    }

    // engulfing the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN && !candle1.bull
        && candle2.trend == SingleCandle.TrendType.DOWN && candle2.bull
        && candle1.bodysize < candle2.bodysize) {
      features[6]++;
      features[58]++;
    }

    // engulfing the bear
    if (candle1.trend == SingleCandle.TrendType.UP && candle1.bull
        && candle2.trend == SingleCandle.TrendType.UP && !candle2.bull
        && candle1.bodysize < candle2.bodysize) {
      features[7]++;
      features[59]++;
    }

    // harami cross the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN
        && !candle1.bull
        && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle2.type == SingleCandle.CandleType.TYPE_DOJI) {
      features[8]++;
      features[58]++;
    }

    // harami cross the bear
    if (candle1.trend == SingleCandle.TrendType.UP
        && candle1.bull
        && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle2.type == SingleCandle.CandleType.TYPE_DOJI) {
      features[9]++;
      features[59]++;
    }

    // harami the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN
        && !candle1.bull
        && candle2.bull
        && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle2.type != SingleCandle.CandleType.TYPE_DOJI
        && candle1.bodysize > candle2.bodysize) {
      features[10]++;
      features[58]++;
    }

    // harami the bear
    if (candle1.trend == SingleCandle.TrendType.UP
        && candle1.bull
        && !candle2.bull
        && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle2.type != SingleCandle.CandleType.TYPE_DOJI
        && candle1.bodysize > candle2.bodysize) {
      features[11]++;
      features[59]++;
    }

    // doji star the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN
        && !candle1.bull
        && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle2.type == SingleCandle.CandleType.TYPE_DOJI) {
      features[12]++;
      features[58]++;
    }

    // doji star the bear
    if (candle1.trend == SingleCandle.TrendType.UP
        && candle1.bull
        && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle2.type == SingleCandle.CandleType.TYPE_DOJI) {
      features[13]++;
      features[59]++;
    }

    // piercing line the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN
        && !candle1.bull
        && candle2.trend == SingleCandle.TrendType.DOWN
        && candle2.bull
        && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle2.type == SingleCandle.CandleType.TYPE_LONG || candle2.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle2.close < (candle1.close + candle1.open) / 2) {
      features[14]++;
      features[58]++;
    }

    // dark cloud cover the bear
    if (candle1.trend == SingleCandle.TrendType.UP
        && candle1.bull
        && candle2.trend == SingleCandle.TrendType.UP
        && !candle2.bull
        && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle2.type == SingleCandle.CandleType.TYPE_LONG || candle2.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle2.close > (candle1.close + candle1.open) / 2) {
      features[15]++;
      features[59]++;
    }

    // meeting lines the bull
    // loosen condition
    if (candle1.trend == SingleCandle.TrendType.DOWN
        && !candle1.bull
        && candle2.trend == SingleCandle.TrendType.DOWN
        && candle2.bull
        && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle2.type == SingleCandle.CandleType.TYPE_LONG || candle2.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        //        && Math.abs(candle1.close - candle2.close) / candle1.close < 0.03
        && candle1.close == candle2.close
        && candle1.bodysize < candle2.bodysize
        && candle1.low > candle2.open) {
      features[16]++;
      features[58]++;
    }

    // meeting lines the bear
    // loosen condition
    if (candle1.trend == SingleCandle.TrendType.UP
        && candle1.bull
        && candle2.trend == SingleCandle.TrendType.UP
        && !candle2.bull
        && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        //        && Math.abs(candle1.close - candle2.close) / candle1.close < 0.03
        && candle1.close == candle2.close
        && candle1.bodysize < candle2.bodysize
        && candle1.high < candle2.open) {
      features[17]++;
      features[59]++;
    }

    // matching low the bull
    // loosen condition
    if (candle1.trend == SingleCandle.TrendType.DOWN && !candle1.bull
        && candle2.trend == SingleCandle.TrendType.DOWN
        //        && !candle2.bull && Math.abs(candle1.close - candle2.close) / candle1.close < 0.03
        && candle1.close == candle2.close
        && candle1.bodysize > candle2.bodysize) {
      features[18]++;
      features[58]++;
    }

    // homing pigeon the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN
        && !candle1.bull
        && candle2.trend == SingleCandle.TrendType.DOWN
        && !candle2.bull
        && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle1.close < candle2.close && candle1.open > candle2.open)) {
      features[19]++;
      features[58]++;
    }

    // continuation patterns: kicking the bull
    if (!candle1.bull && candle2.bull
        && candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG
        && candle2.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG
        && candle1.open < candle2.open) {
      features[43]++;
      features[58]++;
    }

    // continuation patterns: kicking the bear
    if (candle1.bull && !candle2.bull
        && candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG
        && candle2.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG
        && candle1.open > candle2.open) {
      features[44]++;
      features[59]++;
    }

    if (candle1.trend == SingleCandle.TrendType.DOWN
        && !candle1.bull
        && candle2.bull
        && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)) {
      // continuation patterns: on neck line the bear
      // loosen condition
      if (candle2.open < candle1.low && candle1.open == candle2.close) {
        //      if (candle2.open < candle1.low && Math.abs(candle1.open - candle2.close) / candle1.close < 0.03) {
        features[45]++;
        features[59]++;
      } else {

        // continuation patterns: in neck line the bear
        if (candle1.trend == SingleCandle.TrendType.DOWN
            && !candle1.bull
            && candle2.bull
            && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
            && candle1.bodysize > candle2.bodysize
            && candle2.open < candle1.low
            && candle2.close >= candle1.close
            && candle2.close < (candle1.close + candle1.bodysize * 0.01)) {
          features[46]++;
          features[59]++;
        } else {

          // continuation patterns: thrusting line the bear
          if (candle1.trend == SingleCandle.TrendType.DOWN
              && !candle1.bull
              && candle2.bull
              && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
              && candle2.open < candle1.low
              && candle2.close > candle1.close
              && candle2.close < (candle1.open + candle1.close) / 2) {
            features[47]++;
            features[59]++;
          }
        }
      }
    }

  }

  public void threeCandlePattern(SingleCandle candle1, SingleCandle candle2,
                                 SingleCandle candle3) {
    // abandoned baby the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN
        && !candle1.bull
        && candle3.trend == SingleCandle.TrendType.DOWN
        && candle3.bull
        && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle3.type == SingleCandle.CandleType.TYPE_LONG || candle3.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle2.type == SingleCandle.CandleType.TYPE_DOJI
        && candle3.close < candle1.open
        && candle3.close > candle1.close) {
      features[20]++;
      features[58]++;
    }

    // abandoned baby the bear
    if (candle1.trend == SingleCandle.TrendType.UP
        && candle1.bull
        && candle3.trend == SingleCandle.TrendType.UP
        && !candle3.bull
        && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle3.type == SingleCandle.CandleType.TYPE_LONG || candle3.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle2.type == SingleCandle.CandleType.TYPE_DOJI
        && candle3.close > candle1.open
        && candle3.close < candle1.close) {
      features[21]++;
      features[59]++;
    }

    // morning star the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN
        && !candle1.bull
        && candle3.trend == SingleCandle.TrendType.DOWN
        && candle3.bull
        && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle3.type == SingleCandle.CandleType.TYPE_LONG || candle3.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle2.type == SingleCandle.CandleType.TYPE_SHORT
        && candle3.close > candle1.close
        && candle3.close < candle1.open) {
      features[22]++;
      features[58]++;
    }

    // evening star the bear
    if (candle1.trend == SingleCandle.TrendType.UP
        && candle1.bull
        && candle3.trend == SingleCandle.TrendType.UP
        && !candle3.bull
        && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle3.type == SingleCandle.CandleType.TYPE_LONG || candle3.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle2.type == SingleCandle.CandleType.TYPE_SHORT
        && candle3.close < candle1.close
        && candle3.close > candle1.open) {
      features[23]++;
      features[59]++;
    }

    // morning doji star the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN
        && !candle1.bull
        && candle3.trend == SingleCandle.TrendType.DOWN
        && candle3.bull
        && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle3.type == SingleCandle.CandleType.TYPE_LONG || candle3.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle2.type == SingleCandle.CandleType.TYPE_DOJI
        && candle3.close > candle1.close
        && candle3.close < candle1.open) {
      features[24]++;
      features[58]++;
    }

    // evening doji star the bear
    if (candle1.trend == SingleCandle.TrendType.UP
        && candle1.bull
        && candle3.trend == SingleCandle.TrendType.UP
        && !candle3.bull
        && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle3.type == SingleCandle.CandleType.TYPE_LONG || candle3.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle2.type == SingleCandle.CandleType.TYPE_DOJI
        && candle3.close < candle1.close
        && candle3.close > candle1.open) {
      features[25]++;
      features[59]++;
    }

    // upside gap two crows the bear
    if (candle1.trend == SingleCandle.TrendType.UP
        && candle1.bull
        && candle2.trend == SingleCandle.TrendType.UP
        && !candle2.bull
        && candle3.trend == SingleCandle.TrendType.UP
        && !candle3.bull
        && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle1.close < candle2.close
        && candle1.close < candle3.close && candle2.open < candle3.open
        && candle2.close > candle3.close) {
      features[26]++;
      features[59]++;
    }

    // two crows the bear
    if (candle1.trend == SingleCandle.TrendType.UP
        && candle1.bull
        && candle2.trend == SingleCandle.TrendType.UP
        && !candle2.bull
        && candle3.trend == SingleCandle.TrendType.UP
        && !candle3.bull
        && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle3.type == SingleCandle.CandleType.TYPE_LONG || candle3.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle1.close < candle2.close
        && candle3.open > candle2.close
        && candle3.close < candle1.close) {
      features[27]++;
      features[59]++;
    }

    // three star in the south the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN
        && !candle1.bull
        && !candle2.bull
        && !candle3.bull
        && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle3.type == SingleCandle.CandleType.TYPE_MARIBOZU || candle3.type == SingleCandle.CandleType.TYPE_SHORT)
        && candle1.bodysize > candle2.bodysize
        && candle1.low < candle2.low && candle3.low > candle2.low
        && candle3.high < candle2.high) {
      features[28]++;
      features[58]++;
    }

    // deliberation the bear
    if (candle1.trend == SingleCandle.TrendType.UP
        && candle1.bull
        && candle2.bull
        && candle3.bull
        && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle2.type == SingleCandle.CandleType.TYPE_LONG || candle2.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle3.type == SingleCandle.CandleType.TYPE_SPIN_TOP || candle3.type == SingleCandle.CandleType.TYPE_SHORT)) {
      features[29]++;
      features[59]++;
    }

    // three white soldiers the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN
        && candle1.bull
        && candle2.bull
        && candle3.bull
        && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle2.type == SingleCandle.CandleType.TYPE_LONG || candle2.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle3.type == SingleCandle.CandleType.TYPE_LONG || candle3.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)) {
      features[30]++;
      features[58]++;
    }

    // three black crows the bear
    if (candle1.trend == SingleCandle.TrendType.UP
        && !candle1.bull
        && !candle2.bull
        && !candle3.bull
        && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle2.type == SingleCandle.CandleType.TYPE_LONG || candle2.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle3.type == SingleCandle.CandleType.TYPE_LONG || candle3.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle1.close < candle2.open && candle2.close < candle3.open) {
      features[31]++;
      features[59]++;
    }

    // three outside up the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN && !candle1.bull
        && candle2.trend == SingleCandle.TrendType.DOWN && candle2.bull
        && candle3.bull && candle2.bodysize > candle1.bodysize
        && candle3.close > candle2.close) {
      features[32]++;
      features[58]++;
    }

    // three outside down the bear
    if (candle1.trend == SingleCandle.TrendType.UP && candle1.bull
        && candle2.trend == SingleCandle.TrendType.UP && !candle2.bull
        && !candle3.bull && candle2.bodysize > candle1.bodysize
        && candle3.close < candle2.close) {
      features[33]++;
      features[59]++;
    }

    // three inside up the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN
        && !candle1.bull
        && candle2.bull
        && candle3.bull
        && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle1.bodysize > candle2.bodysize
        && candle3.close > candle2.close) {
      features[34]++;
      features[58]++;
    }

    // three inside down the bear
    if (candle1.trend == SingleCandle.TrendType.UP
        && candle1.bull
        && !candle2.bull
        && !candle3.bull
        && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle1.bodysize > candle2.bodysize
        && candle3.close < candle2.close) {
      features[35]++;
      features[59]++;
    }

    // three stars the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN
        && candle1.type == SingleCandle.CandleType.TYPE_DOJI
        && candle2.type == SingleCandle.CandleType.TYPE_DOJI
        && candle3.type == SingleCandle.CandleType.TYPE_DOJI) {
      features[36]++;
      features[58]++;
    }

    // three stars the bear
    if (candle1.trend == SingleCandle.TrendType.UP
        && candle1.type == SingleCandle.CandleType.TYPE_DOJI
        && candle2.type == SingleCandle.CandleType.TYPE_DOJI
        && candle3.type == SingleCandle.CandleType.TYPE_DOJI) {
      features[37]++;
      features[59]++;
    }

    // identical three crows the bear
    if (candle1.trend == SingleCandle.TrendType.UP
        && !candle1.bull
        && !candle2.bull
        && !candle3.bull
        && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle2.type == SingleCandle.CandleType.TYPE_LONG || candle2.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle3.type == SingleCandle.CandleType.TYPE_LONG || candle3.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)) {
      features[38]++;
      features[59]++;
    }

    // unique three river bottom the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN
        && !candle1.bull
        && !candle2.bull
        && candle3.bull
        && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle3.type == SingleCandle.CandleType.TYPE_SHORT)
        && candle2.open < candle1.open && candle2.close > candle1.close
        && candle2.low < candle1.low && candle3.close < candle2.close) {
      features[39]++;
      features[58]++;
    }

    // continuation patterns: upside gap three methods the bull
    if (candle1.trend == SingleCandle.TrendType.UP
        && candle1.bull
        && candle2.bull
        && !candle3.bull
        && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle2.type == SingleCandle.CandleType.TYPE_LONG || candle2.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle2.open > candle1.close && candle3.open > candle2.open
        && candle3.open < candle2.close
        && candle3.close < candle1.close) {
      features[48]++;
      features[58]++;
    }

    // continuation patterns: downside gap three methods the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN
        && !candle1.bull
        && !candle2.bull
        && candle3.bull
        && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle2.type == SingleCandle.CandleType.TYPE_LONG || candle2.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle2.open < candle1.close && candle3.open < candle2.open
        && candle3.open > candle2.close
        && candle3.close > candle1.close) {
      features[49]++;
      features[58]++;
    }

    // continuation patterns: upside tasuki gap the bull
    if (candle1.trend == SingleCandle.TrendType.UP && candle1.bull
        && candle2.bull && !candle3.bull
        && candle1.type != SingleCandle.CandleType.TYPE_DOJI
        && candle2.type != SingleCandle.CandleType.TYPE_DOJI
        && candle2.open > candle1.close && candle3.open > candle2.open
        && candle3.open < candle2.close && candle3.close < candle2.open
        && candle3.close > candle1.close) {
      features[50]++;
      features[58]++;
    }

    // continuation patterns: downside tasuki gap the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN && !candle1.bull
        && !candle2.bull && candle3.bull
        && candle1.type != SingleCandle.CandleType.TYPE_DOJI
        && candle2.type != SingleCandle.CandleType.TYPE_DOJI
        && candle2.open < candle1.close && candle3.open < candle2.open
        && candle3.open > candle2.close && candle3.close > candle2.open
        && candle3.close < candle1.close) {
      features[51]++;
      features[58]++;
    }
  }

  public void fourCandlePattern(SingleCandle candle1, SingleCandle candle2,
                                SingleCandle candle3, SingleCandle candle4) {
    // concealing baby swallow the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN && !candle1.bull
        && !candle2.bull && !candle3.bull && !candle4.bull
        && candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG
        && candle2.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG
        && candle3.type == SingleCandle.CandleType.TYPE_SHORT
        && candle3.open < candle2.close && candle3.high > candle2.close
        && candle4.open > candle3.high && candle4.close < candle3.low) {
      features[40]++;
      features[58]++;
    }

    // continuation patterns: three line strike the bull
    if (candle1.trend == SingleCandle.TrendType.UP
        && candle1.bull
        && candle2.bull
        && candle3.bull
        && !candle4.bull
        && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle2.type == SingleCandle.CandleType.TYPE_LONG || candle2.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle3.type == SingleCandle.CandleType.TYPE_LONG || candle3.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle2.close > candle1.close
        && candle3.close > candle2.close
        && candle4.close < candle1.open) {
      features[52]++;
      features[58]++;
    }

    // continuation patterns: three line strike the bear
    if (candle1.trend == SingleCandle.TrendType.DOWN
        && !candle1.bull
        && !candle2.bull
        && !candle3.bull
        && candle4.bull
        && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle2.type == SingleCandle.CandleType.TYPE_LONG || candle2.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle3.type == SingleCandle.CandleType.TYPE_LONG || candle3.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle2.close < candle1.close
        && candle3.close < candle2.close
        && candle4.close > candle1.open) {
      features[53]++;
      features[59]++;
    }
  }

  public void fiveCandlePattern(SingleCandle candle1, SingleCandle candle2,
                                SingleCandle candle3, SingleCandle candle4, SingleCandle candle5) {
    // breakaway the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN
        && !candle1.bull
        && !candle2.bull
        && !candle4.bull
        && candle5.bull
        && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle2.type == SingleCandle.CandleType.TYPE_SHORT
        && candle2.open < candle1.close
        && candle3.type == SingleCandle.CandleType.TYPE_SHORT
        && candle4.type == SingleCandle.CandleType.TYPE_SHORT
        && (candle5.type == SingleCandle.CandleType.TYPE_LONG || candle5.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle5.close < candle1.close
        && candle5.close > candle2.open) {
      features[41]++;
      features[58]++;
    }

    // breakaway the bear
    if (candle1.trend == SingleCandle.TrendType.UP
        && candle1.bull
        && candle2.bull
        && candle4.bull
        && !candle5.bull
        && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle2.type == SingleCandle.CandleType.TYPE_SHORT
        && candle2.open > candle1.close
        && candle3.type == SingleCandle.CandleType.TYPE_SHORT
        && candle4.type == SingleCandle.CandleType.TYPE_SHORT
        && (candle5.type == SingleCandle.CandleType.TYPE_LONG || candle5.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle5.close > candle1.close
        && candle5.close < candle2.open) {
      features[42]++;
      features[59]++;
    }
  }

  public int[] getFeatures() {
    return features;
  }
}