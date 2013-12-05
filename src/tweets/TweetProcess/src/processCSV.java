import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVReader;

public class processCSV {
	public static void main(String[] args) {
		CSVReader reader;
		BufferedWriter buwS;
		BufferedWriter buwN;
		try {
			reader = new CSVReader(new FileReader("companylist500.csv"));
			buwS = new BufferedWriter(new FileWriter("symbol.txt"));
			buwN = new BufferedWriter(new FileWriter("name1.txt"));
			String[] nextLine;
			reader.readNext();
			while ((nextLine = reader.readNext()) != null) {
				// nextLine[] is an array of values from the line
				buwS.write("$" + nextLine[0] + "\n");
				buwN.write(nextLine[1] + "\n");
			}
			buwS.close();
			buwN.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
