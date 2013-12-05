import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
/**
 * Throw away invalid and duplicate tweet
 * 
 * @author shijieru
 * 
 */
public class FilterTweet {
	static HashMap<String, Integer> map = new HashMap<String, Integer>();
	static HashSet<String> symbolSet = new HashSet<String>();

	static void filter(File file) {
		String name = file.getName();
		String old = null;
		String line = null;
		String symbol = null;
		BufferedWriter bw = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
				// 0 SYMBOL, 1 TWEET
				String[] parts = line.split("\t");
				if (parts.length == 1 || parts[1].trim().equals("")) {
					continue;
				}
				if (old != null && parts[1].equals(old)) {
					continue;
				}
				old = parts[1];
				if (symbol == null || !symbol.equals(parts[0])) {
					// Create a new BufferedWriter
					if (bw != null)
						bw.close();
					symbol = parts[0];
					symbolSet.add(symbol);
					bw = new BufferedWriter(new FileWriter("filtered/" + symbol
							+ "_" + name));
				}
				StringBuffer strbuf = new StringBuffer();
				for (String token : parts[1].split(" ")) {
					if (token.contains("'")) {
						token = token.substring(0, token.indexOf('\''));
					}
					if (token.trim().equals("")) {
						continue;
					}
					strbuf.append(token + " ");
					if (map.containsKey(token)) {
						map.put(token, map.get(token) + 1);
					} else
						map.put(token, 1);
				}
				bw.write(strbuf.toString().trim() + "\n");
			}
			br.close();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		File folder = new File(args[0]);
		if (folder.isDirectory()) {
			for (File fileEntry : folder.listFiles()) {
				if (fileEntry.getName().endsWith(".txt"))
					filter(fileEntry);
			}
		} else {
			filter(folder);
		}
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("word"));
			for (Entry<String, Integer> entry : map.entrySet()) {
				bw.write(entry.getKey() + "\t" + entry.getValue() + "\n");
			}
			bw.close();
			bw = new BufferedWriter(new FileWriter("company"));
			Iterator<String> itr = symbolSet.iterator();
			while (itr.hasNext()) {
				bw.write(itr.next() + "\n");
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
