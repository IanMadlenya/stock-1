package candlestick.models;


public class SingleCandle {
  public double open, high, low, close;
  public CandleType type;
  public boolean bull;
  public double bodysize;
  public TrendType trend;

  public SingleCandle(double open, double high, double low, double close) {
    this.open = open;
    this.high = high;
    this.low = low;
    this.close = close;
  }

  public void setTrend(TrendType trend) {
    this.trend = trend;
  }

  public void setType(CandleType type) {
    this.type = type;
  }

  public void setBull(boolean bull) {
    this.bull = bull;
  }

  public void setBodysize(double bodysize) {
    this.bodysize = bodysize;
  }


  public static enum CandleType {
    TYPE_NONE(0),
    TYPE_MARIBOZU(1),
    TYPE_MARIBOZU_LONG(2),
    TYPE_DOJI(3),
    TYPE_SPIN_TOP(4),
    TYPE_HAMMER(5),
    TYPE_INVERT_HAMMER(6),
    TYPE_LONG(7),
    TYPE_SHORT(8),
    TYPE_STAR(9);
    private int value;

    private CandleType(int value) {
      this.value = value;
    }
  }

  public static enum TrendType {
    UP(0), DOWN(1), KEEP(2),;
    private int value;

    private TrendType(int value) {
      this.value = value;
    }
  }
}