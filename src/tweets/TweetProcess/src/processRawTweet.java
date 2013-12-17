import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class processRawTweet {

	public static void main(String[] args) {
		// args 0: filename 1:date pattern 2:language profiles 3:work folder
		ExecutorService executor = Executors.newFixedThreadPool(15);

		BufferedReader buf;
		try {
			buf = new BufferedReader(new InputStreamReader(System.in));
			String line;
			int count = 0;
			ArrayList<String> rawtweets = new ArrayList<String>();
			while ((line = buf.readLine()) != null) {
				rawtweets.add(line);
				count++;
				if (count == 5000) {
					int beginIndex = args[0].indexOf(args[1]);
					String date = args[0]
							.substring(beginIndex, beginIndex + 16);
					Runnable handler = new ProcessJsonThread(date, args[2],
							args[3], rawtweets);
					executor.execute(handler);
					count = 0;
					rawtweets = new ArrayList<String>();
				}
			}
			buf.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		executor.shutdown();
		while (!executor.isTerminated()) {
		}
	}
}