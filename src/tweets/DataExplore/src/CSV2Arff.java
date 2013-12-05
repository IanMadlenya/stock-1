import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

import java.io.File;

public class CSV2Arff {
	/**
	 * takes 2 arguments: - CSV input file - ARFF output file
	 */
	public static void main(String[] args) throws Exception {
		String csvFile = "ft_test.csv";
		String arffFile = "ft_test.arff";

		// load CSV
		CSVLoader loader = new CSVLoader();
		loader.setSource(new File(csvFile));
		Instances data = loader.getDataSet();

		// save ARFF
		ArffSaver saver = new ArffSaver();
		saver.setInstances(data);
		saver.setFile(new File(arffFile));
		saver.setDestination(new File(arffFile));
		saver.writeBatch();
	}
}