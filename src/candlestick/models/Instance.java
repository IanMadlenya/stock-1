package candlestick.models;

import java.util.ArrayList;

public class Instance {
  private ArrayList<SingleCandle> candles = new ArrayList<SingleCandle>();
  private ArrayList<SingleCandle> patternCandles = new ArrayList<SingleCandle>();
  private SingleCandle.TrendType label;
  private String symbol;
  private String sector;
  private String industry;
  private String time;
  private PatternRecognizer pattern;

  public Instance(ArrayList<SingleCandle> candles, String symbol, String time, String sector, String industry) {
    this.candles = candles;
    this.symbol = symbol;
    this.time = time;
    this.sector = sector;
    this.industry = industry;
    extractFeatures();
  }

  public void getPatternCandles() {
    if (candles.size() <= CandleRecognizer.AVG_PERIOD) {
      return;
    }

    ArrayList<SingleCandle> candleList = new ArrayList<SingleCandle>();
    int i = 0;
    while (i <= CandleRecognizer.AVG_PERIOD) {
      candleList.add(candles.get(i));
      i++;
    }

    while (i <= Math.min(candles.size(), CandleRecognizer.AVG_PERIOD * 2 + 1)) {
      CandleRecognizer candle = new CandleRecognizer(candleList);
      if (i == CandleRecognizer.AVG_PERIOD + 1) {
        label = candle.getResult().trend;
      } else {
        patternCandles.add(candle.getResult());
      }

      if (i < Math.min(candles.size(), CandleRecognizer.AVG_PERIOD * 2 + 1)) {
        candleList.remove(0);
        candleList.add(candles.get(i));
      }
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

  public void setLabel(SingleCandle.TrendType setlabel) {
    label = setlabel;
  }

  public String getSymbol() {
    return symbol;
  }

  public String getTime() {
    return time;
  }

  public String getSector() {
    return sector;
  }

  public String getIndustry() {
    return industry;
  }
}
