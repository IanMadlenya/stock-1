import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateLabel {
	public static void main(String[] args) {
		String line = null;
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader("validation.csv"));

			BufferedWriter bw = new BufferedWriter(new FileWriter(
					"validation_label.csv"));
			
			while ((line = br.readLine()) != null) {
				//label,symbol,time -> symbol, date, label
				String[] parts = line.split(",");
				bw.write(parts[1] + "," + parts[2] + "," + parts[0] + "\n");
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
