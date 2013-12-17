import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

public class EliminateStopWord {
	static HashSet<String> stopwordSet = new HashSet<String>();

	static void eliminateStopWord() {
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new FileReader("stop_words.txt"));
			String line = null;
			while ((line = br.readLine()) != null) {
				stopwordSet.add(line.trim());
			}
			br.close();
			br = new BufferedReader(new FileReader("word"));
			bw = new BufferedWriter(new FileWriter("new_word.txt"));
			while ((line = br.readLine()) != null) {
				String[] parts = line.split("\t");
				if (stopwordSet.contains(parts[0].trim())) {
					continue;
				} else {
					bw.write(line + "\n");
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

	public static void main(String[] args) {
		eliminateStopWord();
	}
}
