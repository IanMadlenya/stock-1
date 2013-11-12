package candlestick.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class PatternRecognizer {
  public static int FEATURE_NUM = 30;
  private int[] features = new int[FEATURE_NUM];

  public PatternRecognizer(ArrayList<SingleCandle> candles) {
    Arrays.fill(features, 0);
    // time seq
    Collections.reverse(candles);

    for (int i = 0; i < candles.size(); i++) {
      oneCandlePattern(candles.get(i));

      if (i < candles.size() - 1) {
        twoCandlePattern(candles.get(i), candles.get(i + 1));
      }

      if (i < candles.size() - 2) {
        threeCandlePattern(candles.get(i), candles.get(i + 1), candles.get(i + 2));
      }

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


  }

  public int[] getFeatures() {
    return features;
  }
}
