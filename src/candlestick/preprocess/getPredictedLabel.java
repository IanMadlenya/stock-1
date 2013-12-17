package candlestick.preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class getPredictedLabel {
  public static void main(String[] args) throws Exception {
    String candleFile = "/Users/none/stock/src/candlestick/extractedFeatures/labelsCandle.txt";
    String newsFile = "/Users/none/stock/src/candlestick/extractedFeatures/labelsNews.csv";
    String twitterFile = "/Users/none/stock/src/candlestick/extractedFeatures/labelsTwitter.csv";

    String candleNews = "/Users/none/stock/src/candlestick/extractedFeatures/predAllOct.csv";


    BufferedReader candlebr = new BufferedReader(new FileReader(candleFile));
    BufferedReader newsbr = new BufferedReader(new FileReader(newsFile));
    BufferedReader twitterbr = new BufferedReader(new FileReader(twitterFile));

    BufferedWriter candlenewsbw = new BufferedWriter(new FileWriter(candleNews));


    String candlel = "";
    String newsl = "";
    String twitterl = "";
    String actual = "";
    String symbol = "";
    String date = "";
    String candlep = "";
    String newsp = "";
    String twitterp = "";

    //    System.out.println("actual,symbol,date,candlePred,newsPred");
    candlenewsbw.write("actual,symbol,date,candlePred,newsPred,twitterPred");
    candlenewsbw.newLine();


    while ((candlel = candlebr.readLine()) != null) {
      newsl = newsbr.readLine();
      twitterl = twitterbr.readLine();

      String[] candlet = candlel.split(":");
      if (candlet[1].startsWith("U")) {
        actual = "UP";
      } else if (candlet[1].startsWith("D")) {
        actual = "DOWN";
      } else if (candlet[1].startsWith("K")) {
        actual = "KEEP";
      }

      if (candlet[2].startsWith("U")) {
        candlep = "UP";
      } else if (candlet[2].startsWith("D")) {
        candlep = "DOWN";
      } else if (candlet[2].startsWith("K")) {
        candlep = "KEEP";
      }

      String[] newst = newsl.split(",");
      if (newst[0].equals("UP")) {
        newsp = "UP";
      } else if (newst[0].equals("DOWN")) {
        newsp = "DOWN";
      } else if (newst[0].equals("KEEP")) {
        newsp = "KEEP";
      } else {
        newsp = "null";
      }

      symbol = newst[1];
      date = newst[2];

      String[] twittert = twitterl.split(",");
      if (twittert[2].equals("UP")) {
        twitterp = "UP";
      } else if (twittert[2].equals("DOWN")) {
        twitterp = "DOWN";
      } else if (twittert[2].equals("KEEP")) {
        twitterp = "KEEP";
      } else {
        twitterp = "null";
      }


      StringBuffer result = new StringBuffer();
      if (!newsp.isEmpty() && !twitterp.isEmpty()) {
        result.append(actual);
        result.append("," + symbol);
        result.append("," + date);
        result.append("," + candlep);
        result.append("," + newsp);
        result.append("," + twitterp);
        //        System.out.println(result.toString());
        candlenewsbw.write(result.toString());
        candlenewsbw.newLine();
      }

    }

    candlenewsbw.close();
    newsbr.close();
    candlebr.close();
    twitterbr.close();

  }
}
