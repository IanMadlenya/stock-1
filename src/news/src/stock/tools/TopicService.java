package stock.tools;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

import jgibblda.Estimator;
import jgibblda.Inferencer;
import jgibblda.LDACmdOption;
import jgibblda.Model;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class TopicService implements Runnable {

    private String NEWS_PATH;

    private String MODEL_PATH;

    private String symbol;

    public TopicService(String symbol) {
	this.symbol = symbol;
	this.NEWS_PATH = "news-500" + "/" + symbol;
	this.MODEL_PATH = "/Users/admin/Desktop/model/" + symbol;
    }

    @Override
    public void run() {
	try {
	    File dir = new File(NEWS_PATH);
	    if (dir.exists()) { 
		gather();
		lda();
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    // Finish
	    Collector.releaseLock();
	}
    }

    public void gather() throws Exception {
	File dir = new File(NEWS_PATH);

	File model = new File(MODEL_PATH);
	if (!model.exists())
	    model.mkdirs();
	
	int count = 0;
	for (String dn : dir.list()) {
	    File subdir = new File(NEWS_PATH + "/" + dn);
	    if (subdir.isDirectory()) {
		count += subdir.list().length - 1;
	    }
	}
	
	PrintWriter writer = new PrintWriter(MODEL_PATH + "/news-" + symbol
		+ ".txt");
	writer.println(count);
	for (String dn : dir.list()) {
	    File subdir = new File(NEWS_PATH + "/" + dn);
	    if (subdir.isDirectory()) {
		for (String nn : subdir.list()) {
		    if (!nn.startsWith(".")) {
			File nf = new File(NEWS_PATH + "/" + dn + "/" + nn);
			Scanner scanner = new Scanner(nf);
			String title = scanner.nextLine();
			scanner.nextLine();
			scanner.nextLine();
			scanner.nextLine();
			scanner.nextLine();
			scanner.nextLine();
			String content = scanner.nextLine();

			String[] tokens = NewsTokenizer.tokenize(title + " "
				+ content);
			scanner.close();
			for (String token : tokens) {
			    writer.print(token + " ");
			}
			writer.println();
		    }
		}
	    }
	}
	writer.flush();
	writer.close();
    }

    public void lda() {
	String[] args = { "-est", "-alpha", "0.5", "-beta", "0.1", "-ntopics",
		"20", "-niters", "100", "-savestep", "10", "-twords", "20",
		"-dfile", "news-" + symbol + ".txt", "-dir", MODEL_PATH + "/" };

	LDACmdOption option = new LDACmdOption();
	CmdLineParser parser = new CmdLineParser(option);

	try {
	    parser.parseArgument(args);

	    if (option.est || option.estc) {
		Estimator estimator = new Estimator();
		estimator.init(option);
		estimator.estimate();
	    } else if (option.inf) {
		Inferencer inferencer = new Inferencer();
		inferencer.init(option);

		Model newModel = inferencer.inference();

		for (int i = 0; i < newModel.phi.length; ++i) {
		    // phi: K * V
		    System.out.println("-----------------------\ntopic" + i
			    + " : ");
		    for (int j = 0; j < 10; ++j) {
			System.out.println(inferencer.globalDict.id2word.get(j)
				+ "\t" + newModel.phi[i][j]);
		    }
		}
	    }
	} catch (CmdLineException cle) {
	    System.out.println("Command line error: " + cle.getMessage());
	    return;
	} catch (Exception e) {
	    System.out.println("Error in main: " + e.getMessage());
	    e.printStackTrace();
	    return;
	}
    }

    public static void main(String[] argv) {
	
	Thread t = new Thread(new TopicService("AAWW"));
	t.run();
    }

}
