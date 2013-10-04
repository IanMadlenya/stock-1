package candlestick.models;

public class SingleCandleStick {
  private float open, high, low, close, body, head, tail;

  public SingleCandleStick(float open, float high, float low, float close) {
    this.open = open;
    this.high = high;
    this.low = low;
    this.close = close;
    this.body = Math.abs(open - close);
    this.head = high - Math.max(open, close);
    this.tail = Math.min(open, close) - low;
  }

  public float getOpen() {
    return open;
  }

  public float getHigh() {
    return high;
  }

  public float getLow() {
    return low;
  }

  public float getClose() {
    return close;
  }

  public boolean isWhite() {
    return close > open ? true : false;
  }

  public boolean isBlack() {
    return close < open ? true : false;
  }

}
