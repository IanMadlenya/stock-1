import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class GeneratePrediction {
	public static void main(String[] args) {
		String line = null;
		BufferedReader br;
		ArrayList<String> order = new ArrayList<String>();
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("result.csv"));
			bw.write("symbol,date,label\n");

			br = new BufferedReader(new FileReader("group10.csv"));
			br.readLine();
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(",");
				order.add(parts[0] + "," + parts[1]);
			}
			br.close();

			br = new BufferedReader(new FileReader("predict.txt"));
			int i = 0;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(",");
				String label = parts[2].substring(parts[2].indexOf(':') + 1);
				order.set(i, order.get(i) + "," + label);
				i++;
			}
			br.close();

			br = new BufferedReader(new FileReader("10Labels.txt"));
			br.readLine();
			int index = 0;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(",");
				String[] listParts = order.get(index).split(",");
				if (parts[0].equals(listParts[0])
						&& parts[1].equals(listParts[1])) {
					bw.write(order.get(index) + "\n");
					index++;
				} else {
					bw.write(parts[0] + "," + parts[1] + ",null\n");
				}
			}
			br.close();
			bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
