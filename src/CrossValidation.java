import java.io.*;

public class CrossValidation {
	public static void main(String[] args) throws IOException{
		String trainFile = "/Users/luli/Desktop/stock/fold/all.arff";
		String trainFold = "/Users/luli/Desktop/stock/fold/trainFold5.arff";
		String testFold = "/Users/luli/Desktop/stock/fold/testFold5.arff";
		BufferedReader br = new BufferedReader(new FileReader(trainFile));
		PrintWriter pwtrain = new PrintWriter(new FileWriter(trainFold));
		PrintWriter pwtest = new PrintWriter(new FileWriter(testFold));
		
		String line;
		String header = br.readLine();
		pwtrain.println(header);
		pwtest.println(header);
		int cw = 0;
		while((line = br.readLine()) != null){
			String[] fields = line.split(",");
			cw++;
			if(cw < 60){
				pwtrain.println(line);
				pwtest.println(line);
			}
			else{
				if(cw > 32169)
					pwtest.println(line);
				else
					pwtrain.println(line);
			}
		}
		br.close();
		pwtest.close();
		pwtrain.close();
	}
}
