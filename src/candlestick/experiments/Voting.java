package candlestick.experiments;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Voting {

  public static ArrayList<String> actuals, candles, news, twitter, symbols, dates;

  public static void init() throws Exception {
    actuals = new ArrayList<String>();
    symbols = new ArrayList<String>();
    dates = new ArrayList<String>();
    candles = new ArrayList<String>();
    news = new ArrayList<String>();
    twitter = new ArrayList<String>();

    String predFile = "/Users/none/stock/src/candlestick/extractedFeatures/predAllOct.csv";
    BufferedReader br = new BufferedReader(new FileReader(predFile));

    String line = br.readLine();

    while ((line = br.readLine()) != null) {
      String[] tokens = line.split(",");
      actuals.add(tokens[0]);
      symbols.add(tokens[1]);
      dates.add(tokens[2]);
      candles.add(tokens[3]);
      news.add(tokens[4]);
      twitter.add(tokens[5]);
    }
    br.close();
  }

  public static void main(String[] args) throws Exception {
    init();
    simpleVoting();
  }

  public static void simpleVoting() {
    System.out.println("actual,symbol,date,candlePred,newsPred,twitterPred,VotePred");

    int total = actuals.size();
    int correct = 0;
    String pred = "";

    for (int i = 0; i < total; i++) {
      String actual = actuals.get(i);
      String symbol = symbols.get(i);
      String date = dates.get(i);

      int up = 0;
      int down = 0;
      int keep = 0;

      if (candles.get(i).equals("UP")) {
        up++;
      } else if (candles.get(i).equals("DOWN")) {
        down++;
      } else if (candles.get(i).equals("KEEP")) {
        keep++;
      }

      if (twitter.get(i).equals("null") || news.get(i).equals("null")) {
        pred = candles.get(i);
      } else {
        if (news.get(i).equals("UP")) {
          up++;
        } else if (news.get(i).equals("DOWN")) {
          down++;
        } else if (news.get(i).equals("KEEP")) {
          keep++;
        }

        if (twitter.get(i).equals("UP")) {
          up++;
        } else if (twitter.get(i).equals("DOWN")) {
          down++;
        } else if (twitter.get(i).equals("KEEP")) {
          keep++;
        }

        if (down >= 2) {
          pred = "DOWN";
        } else if (up >= 2) {
          pred = "UP";
        } else if (keep >= 2) {
          pred = "KEEP";
        }
      }

      System.out.println(actual + "," + symbol + "," + date + "," + candles.get(i) + "," + news.get(i) + "," + twitter.get(i) + "," + pred);

      if (pred.equals(actual)) {
        correct++;
      }
    }

    double prec = ((double)correct / total) * 100;

    System.out.println(correct + " / " + total + " = " + prec + "%");

  }
}
