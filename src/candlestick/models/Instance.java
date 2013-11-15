package candlestick.models;

import java.util.ArrayList;

public class Instance {
  private ArrayList<SingleCandle> candles = new ArrayList<SingleCandle>();
  private ArrayList<SingleCandle> patternCandles = new ArrayList<SingleCandle>();
  private SingleCandle.TrendType label;
  private String symbol;
  private String time;
  private PatternRecognizer pattern;

  public Instance(ArrayList<SingleCandle> candles, String symbol, String time) {
    this.candles = candles;
    this.symbol = symbol;
    this.time = time;
    extractFeatures();
  }

  public void getPatternCandles() {
    if (candles.size() <= CandleRecognizer.AVG_PERIOD * 2) {
      return;
    }

    ArrayList<SingleCandle> candleList = new ArrayList<SingleCandle>();
    int i = 0;
    while (i <= CandleRecognizer.AVG_PERIOD) {
      candleList.add(candles.get(i));
      i++;
    }

    while (i <= (CandleRecognizer.AVG_PERIOD * 2)) {
      CandleRecognizer candle = new CandleRecognizer(candleList);
      if (i == CandleRecognizer.AVG_PERIOD + 1) {
        label = candle.getResult().trend;
        //setLabel2(candles);
      } else {
        patternCandles.add(candle.getResult());
      }

      candleList.remove(0);
      candleList.add(candles.get(i));
      i++;
    }
  }

  public void extractFeatures() {
    getPatternCandles();
    pattern = new PatternRecognizer(patternCandles);
  }

  public int[] getFeatures() {
    return pattern.getFeatures();
  }

  public SingleCandle.TrendType getLabel() {
    return label;
  }

  private void setLabel(ArrayList<SingleCandle> candles) {
    double close = candles.get(0).close;
    double avg = 0;
    for (int i = 1; i < 4; i++) {
      avg += candles.get(i).close;
    }
    avg = avg / 3;

    if (avg < close) {
      label = SingleCandle.TrendType.UP;
    } else if (avg > close) {
      label = SingleCandle.TrendType.DOWN;
    } else {
      label = SingleCandle.TrendType.KEEP;
    }
  }

  private void setLabel2(ArrayList<SingleCandle> candles) {
    double close = candles.get(0).close;
    double avg = candles.get(1).close;

    if (avg < close) {
      label = SingleCandle.TrendType.UP;
    } else if (avg > close) {
      label = SingleCandle.TrendType.DOWN;
    } else {
      label = SingleCandle.TrendType.KEEP;
    }
  }

  public String getSymbol() {
    return symbol;
  }

  public String getTime() {
    return time;
  }
}
