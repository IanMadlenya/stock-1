import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class dataExplore {
	static HashMap<String, Integer> day = new HashMap<String, Integer>();
	static HashMap<String, Integer> tweet = new HashMap<String, Integer>();

	static int numberOfLines(File file) {
		int count = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null) {
				count++;
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}

	public static void main(String[] args) {
		File folder = new File(args[0]);
		if (folder.isDirectory()) {
			for (File fileEntry : folder.listFiles()) {
				if (fileEntry.getName().endsWith(".txt")
						&& fileEntry.getName().startsWith("$")) {
					String symbol = fileEntry.getName()
							.substring(0, fileEntry.getName().indexOf("."))
							.split("_")[0];

					int count = numberOfLines(fileEntry);
					if (day.containsKey(symbol)) {
						day.put(symbol, day.get(symbol) + 1);
					} else
						day.put(symbol, 1);
					if (tweet.containsKey(symbol)) {
						tweet.put(symbol, day.get(symbol) + count);
					} else
						tweet.put(symbol, 1);
				}
			}
		}
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("day.csv"));
			bw.write("number,count\n");

			HashMap<Integer, Integer> graph = new HashMap<Integer, Integer>();
			for (String key : day.keySet()) {
				int count = day.get(key);
				if (graph.containsKey(count))
					graph.put(count, graph.get(count) + 1);
				else
					graph.put(count, 1);
			}

			for (Integer key : graph.keySet()) {
				bw.write(key + "," + graph.get(key) + "\n");
			}

			bw.close();

			bw = new BufferedWriter(new FileWriter("tweet.csv"));
			bw.write("number,count\n");
			graph = new HashMap<Integer, Integer>();
			for (String key : tweet.keySet()) {
				int count = tweet.get(key);
				if (graph.containsKey(count))
					graph.put(count, graph.get(count) + 1);
				else
					graph.put(count, 1);
			}

			for (Integer key : graph.keySet()) {
				bw.write(key + "," + graph.get(key) + "\n");
			}

			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
