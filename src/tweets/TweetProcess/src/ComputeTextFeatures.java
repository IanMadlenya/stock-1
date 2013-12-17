import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;

public class ComputeTextFeatures {
	public static void main(String[] args) {
		String line = null;
		BufferedReader br = null;
		BufferedWriter bw = null;
		String featureFile = args[0];
		String groupFile = args[1];
		String featureTable = args[2];

		// Dictionary
		TreeMap<String, Double> dict = new TreeMap<String, Double>();

		try {
			// Read vocabulary
			br = new BufferedReader(new FileReader(featureFile));
			br.readLine();
			while ((line = br.readLine()) != null) {
				dict.put(line, 0.0);
			}
			br.close();

			// Read label for each day and each symbol
			br = new BufferedReader(new FileReader(groupFile));
			bw = new BufferedWriter(new FileWriter(featureTable));

			for (String key : dict.keySet()) {
				bw.write(key + ",");
			}
			bw.write("Instance_label\n");

			br.readLine();
			while ((line = br.readLine()) != null) {
				// 0 symbol,1 date, 2 tweet,3 label
				String[] parts = line.split(",");

				// Clear the dict
				for (String key : dict.keySet()) {
					dict.put(key, 0.0);
				}

				for (String token : parts[2].split(" ")) {
					if (dict.containsKey(token))
						dict.put(token, dict.get(token) + 1);
				}

				for (String key : dict.keySet()) {
					if (dict.get(key) >= 1)
						bw.write(1.0 + ",");
					else
						bw.write(0.0 + ",");
				}
				bw.write(parts[3] + "\n");
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
