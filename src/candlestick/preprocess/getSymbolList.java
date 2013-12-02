package candlestick.preprocess;

import java.io.BufferedReader;
import java.io.FileReader;

public class getSymbolList {
  public static void main(String[] args) throws Exception {
    String filename = "/Users/none/stock/companylist500.csv";

    BufferedReader br = new BufferedReader(new FileReader(filename));

    String line;

    while ((line = br.readLine()) != null) {
      String[] tokens = line.split(",");
      System.out.println(tokens[0]);
    }

    br.close();

  }
}
