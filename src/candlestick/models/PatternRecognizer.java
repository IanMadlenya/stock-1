package candlestick.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class PatternRecognizer {
  public static int FEATURE_NUM = 55;
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
        threeCandlePattern(candles.get(i), candles.get(i + 1), candles.get(i + 2));
      }

      if (i < candles.size() - 3) {
        fourCandlePattern(candles.get(i), candles.get(i + 1), candles.get(i + 2), candles.get(i + 3));
      }

      if (i < candles.size() - 4) {
        fiveCandlePattern(candles.get(i), candles.get(i + 1), candles.get(i + 2), candles.get(i + 3), candles.get(i + 4));
      }
    }
  }

  public void twoCandleVolumn(SingleCandle candle1, SingleCandle candle2) {
    if (candle2.volumn > candle1.volumn * 2) {
      features[54] += 1;
    }
  }

  public void oneCandlePattern(SingleCandle candle) {
    // inverted hammer the bull
    if (candle.trend == SingleCandle.TrendType.DOWN && candle.type == SingleCandle.CandleType.TYPE_INVERT_HAMMER) {
      features[0] += 1;
    }

    // hanging man the bear
    if (candle.trend == SingleCandle.TrendType.UP && candle.type == SingleCandle.CandleType.TYPE_HAMMER) {
      features[1] += 1;
    }

    // hammer the bull
    if (candle.trend == SingleCandle.TrendType.DOWN && candle.type == SingleCandle.CandleType.TYPE_HAMMER) {
      features[2] += 1;
    }
  }

  public void twoCandlePattern(SingleCandle candle1, SingleCandle candle2) {
    // shooting star the bear
    if (candle1.trend == SingleCandle.TrendType.UP && candle2.trend == SingleCandle.TrendType.UP && candle2.type == SingleCandle.CandleType.TYPE_INVERT_HAMMER) {
      features[3] += 1;
    }

    // belt hold the bull
    if (candle2.trend == SingleCandle.TrendType.DOWN && candle2.bull && !candle1.bull &&
        candle2.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG && candle1.bodysize < candle2.bodysize && candle2.close < candle1.close) {
      features[4] += 1;
    }

    // belt hold the bear
    if (candle2.trend == SingleCandle.TrendType.UP && !candle2.bull && candle1.bull &&
        candle2.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG && candle1.bodysize < candle2.bodysize && candle2.close > candle1.close) {
      features[5] += 1;
    }

    // engulfing the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN && !candle1.bull && candle2.trend == SingleCandle.TrendType.DOWN
        && candle2.bull && candle1.bodysize < candle2.bodysize) {
      features[6] += 1;
    }

    // engulfing the bear
    if (candle1.trend == SingleCandle.TrendType.UP && candle1.bull && candle2.trend == SingleCandle.TrendType.UP &&
        !candle2.bull && candle1.bodysize < candle2.bodysize) {
      features[7] += 1;
    }

    // harami cross the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN && !candle1.bull &&
        (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle2.type == SingleCandle.CandleType.TYPE_DOJI) {
      features[8] += 1;
    }

    // harami cross the bear
    if (candle1.trend == SingleCandle.TrendType.UP && candle1.bull &&
        (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle2.type == SingleCandle.CandleType.TYPE_DOJI) {
      features[9] += 1;
    }

    // harami the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN && !candle1.bull && candle2.bull &&
        (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle2.type != SingleCandle.CandleType.TYPE_DOJI && candle1.bodysize > candle2.bodysize) {
      features[10] += 1;
    }

    // harami the bear
    if (candle1.trend == SingleCandle.TrendType.UP && candle1.bull && !candle2.bull &&
        (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle2.type != SingleCandle.CandleType.TYPE_DOJI && candle1.bodysize > candle2.bodysize) {
      features[11] += 1;
    }

    // doji star the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN && !candle1.bull &&
        (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle2.type == SingleCandle.CandleType.TYPE_DOJI) {
      features[12] += 1;
    }

    // doji star the bear
    if (candle1.trend == SingleCandle.TrendType.UP && candle1.bull &&
        (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle2.type == SingleCandle.CandleType.TYPE_DOJI) {
      features[13] += 1;
    }

    // piercing line the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN && !candle1.bull && candle2.trend == SingleCandle.TrendType.DOWN &&
        candle2.bull && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle2.type == SingleCandle.CandleType.TYPE_LONG || candle2.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle2.close < (candle1.close + candle1.open) / 2) {
      features[14] += 1;
    }

    // dark cloud cover the bear
    if (candle1.trend == SingleCandle.TrendType.UP && candle1.bull && candle2.trend == SingleCandle.TrendType.UP &&
        !candle2.bull && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle2.type == SingleCandle.CandleType.TYPE_LONG || candle2.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle2.close > (candle1.close + candle1.open) / 2) {
      features[15] += 1;
    }

    // meeting lines the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN && !candle1.bull && candle2.trend == SingleCandle.TrendType.DOWN &&
        candle2.bull && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle2.type == SingleCandle.CandleType.TYPE_LONG || candle2.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle1.close == candle2.close && candle1.bodysize < candle2.bodysize && candle1.low > candle2.open) {
      features[16] += 1;
    }

    // meeting lines the bear
    if (candle1.trend == SingleCandle.TrendType.UP && candle1.bull && candle2.trend == SingleCandle.TrendType.UP &&
        !candle2.bull && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle1.close == candle2.close && candle1.bodysize < candle2.bodysize && candle1.high < candle2.open) {
      features[17] += 1;
    }

    // matching low the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN && !candle1.bull && candle2.trend == SingleCandle.TrendType.DOWN &&
        !candle2.bull
        && candle1.close == candle2.close && candle1.bodysize > candle2.bodysize) {
      features[18] += 1;
    }

    // homing pigeon the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN && !candle1.bull && candle2.trend == SingleCandle.TrendType.DOWN &&
        !candle2.bull && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle1.close < candle2.close && candle1.open > candle2.open)) {
      features[19] += 1;
    }

    // continuation patterns: kicking the bull
    if (!candle1.bull && candle2.bull &&
        candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG && candle2.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG &&
        candle1.open < candle2.open) {
      features[43] += 1;
    }

    // continuation patterns: kicking the bear
    if (candle1.bull && !candle2.bull &&
        candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG && candle2.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG &&
        candle1.open > candle2.open) {
      features[44] += 1;
    }

    if (candle1.trend == SingleCandle.TrendType.DOWN && !candle1.bull && candle2.bull &&
        (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)) {
      // continuation patterns: on neck line the bear
      if (candle2.open < candle1.low && candle2.close == candle1.low) {
        features[45] += 1;
      } else {

        // continuation patterns: in neck line the bear
        if (candle1.trend == SingleCandle.TrendType.DOWN && !candle1.bull && candle2.bull &&
            (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
            && candle1.bodysize > candle2.bodysize && candle2.open < candle1.low && candle2.close >= candle1.close &&
            candle2.close < (candle1.close + candle1.bodysize * 0.01)) {
          features[46] += 1;
        } else {

          // continuation patterns: thrusting line the bear
          if (candle1.trend == SingleCandle.TrendType.DOWN && !candle1.bull && candle2.bull &&
              (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
              && candle2.open < candle1.low && candle2.close > candle1.close &&
              candle2.close < (candle1.open + candle1.close) / 2) {
            features[47] += 1;
          }
        }
      }
    }
  }

  public void threeCandlePattern(SingleCandle candle1, SingleCandle candle2, SingleCandle candle3) {
    // abandoned baby the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN && !candle1.bull && candle3.trend == SingleCandle.TrendType.DOWN &&
        candle3.bull && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle3.type == SingleCandle.CandleType.TYPE_LONG || candle3.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle2.type == SingleCandle.CandleType.TYPE_DOJI &&
        candle3.close < candle1.open && candle3.close > candle1.close) {
      features[20] += 1;
    }

    // abandoned baby the bear
    if (candle1.trend == SingleCandle.TrendType.UP && candle1.bull && candle3.trend == SingleCandle.TrendType.UP &&
        !candle3.bull && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle3.type == SingleCandle.CandleType.TYPE_LONG || candle3.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle2.type == SingleCandle.CandleType.TYPE_DOJI &&
        candle3.close > candle1.open && candle3.close < candle1.close) {
      features[21] += 1;
    }

    // morning star the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN && !candle1.bull && candle3.trend == SingleCandle.TrendType.DOWN &&
        candle3.bull && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle3.type == SingleCandle.CandleType.TYPE_LONG || candle3.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle2.type == SingleCandle.CandleType.TYPE_SHORT &&
        candle3.close > candle1.close && candle3.close < candle1.open) {
      features[22] += 1;
    }

    // evening star the bear
    if (candle1.trend == SingleCandle.TrendType.UP && candle1.bull && candle3.trend == SingleCandle.TrendType.UP &&
        !candle3.bull && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle3.type == SingleCandle.CandleType.TYPE_LONG || candle3.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle2.type == SingleCandle.CandleType.TYPE_SHORT &&
        candle3.close < candle1.close && candle3.close > candle1.open) {
      features[23] += 1;
    }

    // morning doji star the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN && !candle1.bull && candle3.trend == SingleCandle.TrendType.DOWN &&
        candle3.bull && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle3.type == SingleCandle.CandleType.TYPE_LONG || candle3.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle2.type == SingleCandle.CandleType.TYPE_DOJI &&
        candle3.close > candle1.close && candle3.close < candle1.open) {
      features[24] += 1;
    }

    // evening doji star the bear
    if (candle1.trend == SingleCandle.TrendType.UP && candle1.bull && candle3.trend == SingleCandle.TrendType.UP &&
        !candle3.bull && (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle3.type == SingleCandle.CandleType.TYPE_LONG || candle3.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle2.type == SingleCandle.CandleType.TYPE_DOJI &&
        candle3.close < candle1.close && candle3.close > candle1.open) {
      features[25] += 1;
    }

    // upside gap two crows the bear
    if (candle1.trend == SingleCandle.TrendType.UP && candle1.bull && candle2.trend == SingleCandle.TrendType.UP &&
        !candle2.bull && candle3.trend == SingleCandle.TrendType.UP && !candle3.bull &&
        (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle1.close < candle2.close && candle1.close < candle3.close &&
        candle2.open < candle3.open && candle2.close > candle3.close) {
      features[26] += 1;
    }

    // two crows the bear
    if (candle1.trend == SingleCandle.TrendType.UP && candle1.bull && candle2.trend == SingleCandle.TrendType.UP &&
        !candle2.bull && candle3.trend == SingleCandle.TrendType.UP && !candle3.bull &&
        (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle3.type == SingleCandle.CandleType.TYPE_LONG || candle3.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle1.close < candle2.close && candle3.open > candle2.close && candle3.close < candle1.close) {
      features[27] += 1;
    }

    // three star in the south the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN && !candle1.bull && !candle2.bull && !candle3.bull &&
        (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle3.type == SingleCandle.CandleType.TYPE_MARIBOZU || candle3.type == SingleCandle.CandleType.TYPE_SHORT)
        && candle1.bodysize > candle2.bodysize && candle1.low < candle2.low &&
        candle3.low > candle2.low && candle3.high < candle2.high) {
      features[28] += 1;
    }

    // deliberation the bear
    if (candle1.trend == SingleCandle.TrendType.UP && candle1.bull && candle2.bull && candle3.bull &&
        (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle2.type == SingleCandle.CandleType.TYPE_LONG || candle2.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle3.type == SingleCandle.CandleType.TYPE_SPIN_TOP || candle3.type == SingleCandle.CandleType.TYPE_SHORT)) {
      features[29] += 1;
    }

    // three white soldiers the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN && candle1.bull && candle2.bull && candle3.bull &&
        (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle2.type == SingleCandle.CandleType.TYPE_LONG || candle2.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle3.type == SingleCandle.CandleType.TYPE_LONG || candle3.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)) {
      features[30] += 1;
    }

    // three black crows the bear
    if (candle1.trend == SingleCandle.TrendType.UP && !candle1.bull && !candle2.bull && !candle3.bull &&
        (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle2.type == SingleCandle.CandleType.TYPE_LONG || candle2.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && (candle3.type == SingleCandle.CandleType.TYPE_LONG || candle3.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle1.close < candle2.open && candle2.close < candle3.open) {
      features[31] += 1;
    }

    // three outside up the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN && !candle1.bull && candle2.trend == SingleCandle.TrendType.DOWN && candle2.bull
        && candle3.bull && candle2.bodysize > candle1.bodysize && candle3.close > candle2.close) {
      features[32] += 1;
    }

    // three outside down the bear
    if (candle1.trend == SingleCandle.TrendType.UP && candle1.bull && candle2.trend == SingleCandle.TrendType.UP && !candle2.bull
        && !candle3.bull && candle2.bodysize > candle1.bodysize && candle3.close < candle2.close) {
      features[33] += 1;
    }

    // three inside up the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN && !candle1.bull && candle2.bull && candle3.bull &&
        (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle1.bodysize > candle2.bodysize && candle3.close > candle2.close) {
      features[34] += 1;
    }

    // three inside down the bear
    if (candle1.trend == SingleCandle.TrendType.UP && candle1.bull && !candle2.bull && !candle3.bull &&
        (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)
        && candle1.bodysize > candle2.bodysize && candle3.close < candle2.close) {
      features[35] += 1;
    }

    // three stars the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN && candle1.type == SingleCandle.CandleType.TYPE_DOJI
        && candle2.type == SingleCandle.CandleType.TYPE_DOJI && candle3.type == SingleCandle.CandleType.TYPE_DOJI) {
      features[36] += 1;
    }

    // three stars the bear
    if (candle1.trend == SingleCandle.TrendType.UP && candle1.type == SingleCandle.CandleType.TYPE_DOJI
        && candle2.type == SingleCandle.CandleType.TYPE_DOJI && candle3.type == SingleCandle.CandleType.TYPE_DOJI) {
      features[37] += 1;
    }

    // identical three crows the bear
    if (candle1.trend == SingleCandle.TrendType.UP && !candle1.bull && !candle2.bull && !candle3.bull &&
        (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG) &&
        (candle2.type == SingleCandle.CandleType.TYPE_LONG || candle2.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG) &&
        (candle3.type == SingleCandle.CandleType.TYPE_LONG || candle3.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG)) {
      features[38] += 1;
    }

    // unique three river bottom the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN && !candle1.bull && !candle2.bull && candle3.bull &&
        (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG) &&
        (candle3.type == SingleCandle.CandleType.TYPE_SHORT)
        && candle2.open < candle1.open && candle2.close > candle1.close && candle2.low < candle1.low &&
        candle3.close < candle2.close) {
      features[39] += 1;
    }

    // continuation patterns: upside gap three methods the bull
    if (candle1.trend == SingleCandle.TrendType.UP && candle1.bull && candle2.bull && !candle3.bull &&
        (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG) &&
        (candle2.type == SingleCandle.CandleType.TYPE_LONG || candle2.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG) &&
        candle2.open > candle1.close && candle3.open > candle2.open && candle3.open < candle2.close &&
        candle3.close < candle1.close) {
      features[48] += 1;
    }

    // continuation patterns: downside gap three methods the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN && !candle1.bull && !candle2.bull && candle3.bull &&
        (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG) &&
        (candle2.type == SingleCandle.CandleType.TYPE_LONG || candle2.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG) &&
        candle2.open < candle1.close && candle3.open < candle2.open && candle3.open > candle2.close &&
        candle3.close > candle1.close) {
      features[49] += 1;
    }

    // continuation patterns: upside tasuki gap the bull
    if (candle1.trend == SingleCandle.TrendType.UP && candle1.bull && candle2.bull && !candle3.bull &&
        candle1.type != SingleCandle.CandleType.TYPE_DOJI && candle2.type != SingleCandle.CandleType.TYPE_DOJI &&
        candle2.open > candle1.close && candle3.open > candle2.open && candle3.open < candle2.close &&
        candle3.close < candle2.open && candle3.close > candle1.close) {
      features[50] += 1;
    }

    // continuation patterns: downside tasuki gap the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN && !candle1.bull && !candle2.bull && candle3.bull &&
        candle1.type != SingleCandle.CandleType.TYPE_DOJI && candle2.type != SingleCandle.CandleType.TYPE_DOJI &&
        candle2.open < candle1.close && candle3.open < candle2.open && candle3.open > candle2.close &&
        candle3.close > candle2.open && candle3.close < candle1.close) {
      features[51] += 1;
    }
  }

  public void fourCandlePattern(SingleCandle candle1, SingleCandle candle2, SingleCandle candle3, SingleCandle candle4) {
    // concealing baby swallow the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN && !candle1.bull && !candle2.bull && !candle3.bull && !candle4.bull &&
        candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG && candle2.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG &&
        candle3.type == SingleCandle.CandleType.TYPE_SHORT &&
        candle3.open < candle2.close && candle3.high > candle2.close && candle4.open > candle3.high && candle4.close < candle3.low) {
      features[40] += 1;
    }

    // continuation patterns: three line strike the bull
    if (candle1.trend == SingleCandle.TrendType.UP && candle1.bull && candle2.bull && candle3.bull && !candle4.bull &&
        (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG) &&
        (candle2.type == SingleCandle.CandleType.TYPE_LONG || candle2.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG) &&
        (candle3.type == SingleCandle.CandleType.TYPE_LONG || candle3.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG) &&
        candle2.close > candle1.close && candle3.close > candle2.close && candle4.close < candle1.open) {
      features[52] += 1;
    }

    // continuation patterns: three line strike the bear
    if (candle1.trend == SingleCandle.TrendType.DOWN && !candle1.bull && !candle2.bull && !candle3.bull && candle4.bull &&
        (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG) &&
        (candle2.type == SingleCandle.CandleType.TYPE_LONG || candle2.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG) &&
        (candle3.type == SingleCandle.CandleType.TYPE_LONG || candle3.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG) &&
        candle2.close < candle1.close && candle3.close < candle2.close && candle4.close > candle1.open) {
      features[53] += 1;
    }
  }

  public void fiveCandlePattern(SingleCandle candle1, SingleCandle candle2, SingleCandle candle3, SingleCandle candle4, SingleCandle candle5) {
    // breakaway the bull
    if (candle1.trend == SingleCandle.TrendType.DOWN && !candle1.bull && !candle2.bull && !candle4.bull && candle5.bull &&
        (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG) &&
        candle2.type == SingleCandle.CandleType.TYPE_SHORT && candle2.open < candle1.close &&
        candle3.type == SingleCandle.CandleType.TYPE_SHORT && candle4.type == SingleCandle.CandleType.TYPE_SHORT &&
        (candle5.type == SingleCandle.CandleType.TYPE_LONG || candle5.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG) &&
        candle5.close < candle1.close && candle5.close > candle2.open) {
      features[41] += 1;
    }

    // breakaway the bear
    if (candle1.trend == SingleCandle.TrendType.UP && candle1.bull && candle2.bull && candle4.bull && !candle5.bull &&
        (candle1.type == SingleCandle.CandleType.TYPE_LONG || candle1.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG) &&
        candle2.type == SingleCandle.CandleType.TYPE_SHORT && candle2.open > candle1.close &&
        candle3.type == SingleCandle.CandleType.TYPE_SHORT && candle4.type == SingleCandle.CandleType.TYPE_SHORT &&
        (candle5.type == SingleCandle.CandleType.TYPE_LONG || candle5.type == SingleCandle.CandleType.TYPE_MARIBOZU_LONG) &&
        candle5.close > candle1.close && candle5.close < candle2.open) {
      features[42] += 1;
    }
  }

  public int[] getFeatures() {
    return features;
  }
}
