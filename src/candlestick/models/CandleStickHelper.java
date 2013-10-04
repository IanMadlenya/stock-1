package candlestick.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CandleStickHelper {

  public static final int predictLength = 3;

  public static float getAverageClose(Set<SingleCandleStick> set) {
    float sum = 0;
    for (SingleCandleStick item : set) {
      sum += item.getClose();
    }

    return sum / set.size();
  }

  public static int numWhite(ArrayList<SingleCandleStick> candleSticks) {
    int count = 0;
    for (int i = predictLength; i < candleSticks.size(); i++) {
      if (candleSticks.get(i).isWhite()) {
        count++;
      }
    }
    return count;
  }

  public static int numBlack(ArrayList<SingleCandleStick> candleSticks) {
    int count = 0;
    for (int i = predictLength; i < candleSticks.size(); i++) {
      if (candleSticks.get(i).isBlack()) {
        count++;
      }
    }
    return count;
  }

  public static Label getLabel(ArrayList<SingleCandleStick> candleSticks) {
    HashSet<SingleCandleStick> setForPrediction = new HashSet<SingleCandleStick>();
    for (int i = 0; i < predictLength; i++) {
      setForPrediction.add(candleSticks.get(i));
    }

    float averageClose = getAverageClose(setForPrediction);
    if (averageClose > candleSticks.get(predictLength).getClose()) {
      return Label.UP;
    } else if (averageClose < candleSticks.get(predictLength).getClose()) {
      return Label.DOWN;
    } else {
      return Label.KEEP;
    }
  }

  public static enum Label {
    DOWN(0), KEEP(1), UP(2);
    private int value;

    private Label(int value) {
      this.value = value;
    }
  }

}
