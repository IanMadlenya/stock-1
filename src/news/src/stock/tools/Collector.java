package stock.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

public class Collector {
    
    public static int count = 0;
    
    public static synchronized boolean requireLock() {
	if (count >= 10) {
	    return false;
	} else {
	    count++;
	    return true;
	}
    }
    
    public static synchronized void releaseLock() {
	count--;
    }
    
    public static void genTopics(String listFile) throws Exception {
	Scanner scanner = new Scanner(new File(listFile));
	List<String> symbols = new ArrayList<String>();
	while (scanner.hasNextLine()) {
	    symbols.add(scanner.nextLine());
	}
	scanner.close();
	
	for (int i = 0; i < symbols.size(); i++) {
	    while (!requireLock()) {
		;
	    }
	    
	    Thread t = new Thread(new TopicService(symbols.get(i)));
	    t.start();
	}
    }

    public static void companyCollect(String listFile) throws Exception {
	Scanner scanner = new Scanner(new File(listFile));
	
	File dir = new File("news");
	if (!dir.exists()) {
	    dir.mkdirs();
	}
	
	while (scanner.hasNextLine()) {
	    while (!requireLock()) {
		;
	    }
	    String symbol = scanner.nextLine();
	    
	    if (symbol != null) {
		
		Thread t = new Thread(new YahooNewsService(symbol));
		t.start();
	    } else {
		releaseLock();
	    }
	}
    }
    
    public static void stat() throws Exception {
	File dir = new File("news-500");
	int tt = 0;
	int cc = 0;
	int g20 = 0;
	int g10 = 0;
	int g5 = 0;
	System.out.println(dir.list().length);
	for (String f : dir.list()) {
	    File subdir = new File("news-500/" + f);
	    if (subdir.isDirectory()) {
		int total = 0;
		for (String ff : subdir.list()) {
		    File subsubdir = new File("news-500/" + f + "/" + ff);
		    if (subsubdir.isDirectory()) {
			total += (subsubdir.list().length);
			tt += (subsubdir.list().length);
		    }
		}
		cc += subdir.list().length - 1;
		if ((total + 0.0) / (subdir.list().length - 1) >= 20) {
		    g20++;
		} else if ((total + 0.0) / (subdir.list().length - 1) >= 10) {
		    g10++;
		} else if ((total + 0.0) / (subdir.list().length - 1) >= 5) {
		    g5++;
		}
		System.out.println(f + " " + ((total + 0.0) / (subdir.list().length - 1)));
	    }
	}
	
	System.out.println("All: " + (tt + 0.0) / cc);
	System.out.println("> 20: " + g20);
	System.out.println("> 10: " + g10);
	System.out.println("> 5: " + g5);
    }
    
    public static void extractTopics(String fn) throws FileNotFoundException {
	Scanner scanner = new Scanner(new File(fn));
	PrintWriter writer = new PrintWriter("features.txt");
	Set<String> fs = new HashSet<String>();
	while (scanner.hasNextLine()) {
	    scanner.nextLine();
	    int k = 0;
	    for (int i = 0; i < 50; i++) {
		String line = scanner.nextLine();
		String[] tokens = line.trim().split("\\s+");
		if (k < 20 && !fs.contains(tokens[0])) {
		    writer.println(tokens[0]);
		    fs.add(tokens[0]);
		    k++;
		}
	    }
	}
	scanner.close();
	writer.flush();
	writer.close();
    }
    
    public static void buildTraining() throws FileNotFoundException {
	Scanner scanner = new Scanner(new File("features.txt"));
	List<String> features = new ArrayList<String>();
	Set<String> fset = new HashSet<String>();
	while (scanner.hasNextLine()) {
	    String word = scanner.nextLine();
	    features.add(word);
	    fset.add(word);
	}
	scanner.close();
	
	PrintWriter writer = new PrintWriter("train.csv");
	PrintWriter writer1 = new PrintWriter("tags.csv");
	writer.print("class,symbol");
	int k = 1;
	for (String f : features) {
	    writer.print("," + k);
	    // System.out.println(k + " : " + f);
	    k++;
	}
	writer.println();
	
	scanner = new Scanner(new File("newslabel.txt"));
	scanner.nextLine();
	Map<String, Map<String, String>> labels = new HashMap<String, Map<String, String>>();
	while (scanner.hasNextLine()) {
	    String line = scanner.nextLine();
	    String[] tokens = line.split(",");
	    if (labels.containsKey(tokens[0])) {
		labels.get(tokens[0]).put(tokens[1], tokens[2]);
	    } else {
		Map<String, String> map = new HashMap<String, String>();
		map.put(tokens[1], tokens[2]);
		labels.put(tokens[0], map);
	    }
	}
	scanner.close();
	
	int total = 0;
	
	File dir = new File("news");
	for (String symbol : dir.list()) {
	    if (symbol.charAt(0) == '.' || !labels.containsKey(symbol))
		continue;
	    System.out.print(symbol);
	    File subdir = new File("news/" + symbol);
	    Map<String, Map<String, Integer>> map = new HashMap<String, Map<String, Integer>>();
	    for (String dt : subdir.list()) {
		if (dt.charAt(0) == '.' || (!dt.startsWith("2013-09") && !dt.startsWith("2013-10")))
		    continue;
		File subsubdir = new File("news/" + symbol + "/" + dt);
		Map<String, Integer> tmp = new HashMap<String, Integer>();
		for (String fn : subsubdir.list()) {
		    if (fn.charAt(0) == '.')
			continue;
		    scanner = new Scanner(new File("news/" + symbol + "/" + dt + "/" + fn));
		    if (scanner.hasNextLine()) {
			String title = scanner.nextLine();
			scanner.nextLine();
			scanner.nextLine();
			scanner.nextLine();
			scanner.nextLine();
			scanner.nextLine();
			String content = scanner.nextLine();

			String[] tokens = NewsTokenizer.tokenize(title + " "
				+ content);
			for (String token : tokens) {
			    if (fset.contains(token)) {
				if (tmp.containsKey(token)) {
				    tmp.put(token, tmp.get(token) + 1);
				} else {
				    tmp.put(token, 1);
				}
			    }
			}
		    }
		    scanner.close();
		}
		map.put(dt, tmp);
	    }
	    
	    int newscnt = 0;
	    double newsavg = 0.0;
	    String start = "2013-09-04";
	    // String start = "2013-10-18";
	    SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
	    Date startdate = null;
	    try {
		startdate = dt.parse(start);
	    } catch (Exception e) {
		return;
	    }

	    Calendar c = Calendar.getInstance();
	    c.setTime(startdate);
//	    for (int i = 0; i < 58; i++) {
	    for (int i = 0; i < 88; i++) {
		if (i >= 27 && i < 58) {
		    c.add(Calendar.DATE, 1);
		    continue;
		}
		
		String date = dt.format(c.getTime());
		// System.out.println(date);
		if (c.getTime().getDay() == 6 || c.getTime().getDay() == 0) {
		    c.add(Calendar.DATE, 1);
		    continue;
		}
		
		if (labels.get(symbol).containsKey(date)) {
		    String label = labels.get(symbol).get(date);
		    if (label.equals("null")) {
			c.add(Calendar.DATE, 1);
			// writer1.println("0," + symbol + "," + date);
			continue;
		    }
		    c.add(Calendar.DATE, -1);
		    String d1 = dt.format(c.getTime());
		    c.add(Calendar.DATE, -1);
		    String d2 = dt.format(c.getTime());
		    c.add(Calendar.DATE, -1);
		    String d3 = dt.format(c.getTime());
		    Map<String, Integer> freq = calcFreq(map, d1, d2, d3);
		    
		    if (freq.keySet().size() == 0) {
			c.add(Calendar.DATE, 4);
			writer1.println("0," + symbol + "," + date);
			continue;
		    }
		    newsavg += freq.keySet().size();
		    
		    writer.print(label + "," + symbol);
		    for (String f : features) {
			if (freq.containsKey(f)) {
			    writer.print("," + freq.get(f));
			} else {
			    writer.print(",0");
			}
		    }
		    writer.println();
		    writer1.println("1," + symbol + "," + date);
		    newscnt++;
		    total++;
		    c.add(Calendar.DATE, 4);
		} else {
		    c.add(Calendar.DATE, 1);
		}
	    }
	    System.out.println("\t" + newscnt + "\t" + (newsavg == 0 ? 0 : newsavg / newscnt));
	}
	writer.flush();
	writer.close();
	writer1.flush();
	writer1.close();
	System.out.println(total);
    }
    
    private static Map<String, Integer> calcFreq(
	    Map<String, Map<String, Integer>> map, String d1, String d2,
	    String d3) {
	
	if (!map.containsKey(d1) && !map.containsKey(d2) && !map.containsKey(d3)) {
	    return new HashMap<String, Integer>();
	} else {
	    Map<String, Integer> ret = new HashMap<String, Integer>();
	    if (map.containsKey(d1)) {
		for (String key : map.get(d1).keySet()) {
		    if (ret.containsKey(key)) {
			ret.put(key, ret.get(key) + map.get(d1).get(key));
		    } else {
			ret.put(key, map.get(d1).get(key));
		    }
		}
	    }
	    if (map.containsKey(d2)) {
		for (String key : map.get(d2).keySet()) {
		    if (ret.containsKey(key)) {
			ret.put(key, ret.get(key) + map.get(d2).get(key));
		    } else {
			ret.put(key, map.get(d2).get(key));
		    }
		}
	    }
	    if (map.containsKey(d3)) {
		for (String key : map.get(d3).keySet()) {
		    if (ret.containsKey(key)) {
			ret.put(key, ret.get(key) + map.get(d3).get(key));
		    } else {
			ret.put(key, map.get(d3).get(key));
		    }
		}
	    }
	    return ret;
	}
    }
    
    public static void changeFormat() {
	Scanner scanner = null;
	Scanner scanner1 = null;
	PrintWriter writer = null;
	try {
	    scanner = new Scanner(new File("tags.csv"));
	    scanner1 = new Scanner(new File("export.csv"));
	    writer = new PrintWriter("news-results.csv");
	} catch (FileNotFoundException e1) {
	    e1.printStackTrace();
	    return;
	}
	
	scanner1.nextLine();
	while (scanner.hasNextLine()) {
	    String line = scanner.nextLine();
	    String[] tokens = line.split(",");
	    if (tokens[0].equals("1")) {
		String line1 = scanner1.nextLine();
	    	String p = line1.split(",")[1];
	    	writer.println(p + "," + tokens[1] + "," + tokens[2]);
	    } else {
		writer.println("null," + tokens[1] + "," + tokens[2]);
	    }
	}
	writer.flush();
	writer.close();
	scanner.close();
	scanner1.close();
    }
    
    public static void topWords() {
	try {
	    Scanner scanner = new Scanner(new File("news-all.txt"));
	    Map<String, Integer> freq = new HashMap<String, Integer>();
	    while (scanner.hasNextLine()) {
		String line = scanner.nextLine();
		String[] tokens = line.split("\\s+");
		for (String token : tokens) {
		    if (freq.containsKey(token)) {
			freq.put(token, freq.get(token) + 1);
		    } else {
			freq.put(token, 1);
		    }
		}
	    }
	    scanner.close();
	    
	    List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(freq.entrySet());
	    Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
		@Override
		public int compare(Entry<String, Integer> e0,
			Entry<String, Integer> e1) {
		    return e1.getValue().compareTo(e0.getValue());
		}
	    });
	    
	    for (int i = 0; i < 50; i++) {
		System.out.println(list.get(i).getKey());
	    }
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
    }
    
    public static void dist() {
	try {
	    Map<Integer, Integer> freq = new HashMap<Integer, Integer>();
	    Map<Integer, List<String>> syms = new HashMap<Integer, List<String>>();
	    List<Double> avgs = new ArrayList<Double>();
	    
	    int linecnt = 0;
	    Scanner scanner = new Scanner(new File("dist"));
	    while (scanner.hasNextLine()) {
		String line = scanner.nextLine();
		String symbol = line.split("\\s+")[0];
		Integer cnt = Integer.parseInt(line.split("\\s+")[1]);
		Double avg = Double.parseDouble(line.split("\\s+")[2]);
		avgs.add(avg);
		// System.out.println(cnt);
		linecnt += cnt;
		if (freq.containsKey(cnt)) {
		    freq.put(cnt, freq.get(cnt) + 1);
		    syms.get(cnt).add(symbol);
		} else {
		    freq.put(cnt, 1);
		    List<String> list = new ArrayList<String>();
		    list.add(symbol);
		    syms.put(cnt, list);
		}
	    }
	    scanner.close();
	    System.out.println(linecnt);
	    
	    List<Map.Entry<Integer, Integer>> res = new ArrayList<Map.Entry<Integer, Integer>>(freq.entrySet());
	    Collections.sort(res, new Comparator<Map.Entry<Integer, Integer>>() {
		@Override
		public int compare(Entry<Integer, Integer> arg0,
			Entry<Integer, Integer> arg1) {
		    return arg0.getKey().compareTo(arg1.getKey());
		}
	    });
	    
	    for (Map.Entry<Integer, Integer> e : res) {
		System.out.println(e.getKey() + "\t" + e.getValue());
		System.out.println(syms.get(e.getKey()));
	    }
	    
	    Collections.sort(avgs);
	    Collections.reverse(avgs);
	    for (Double d : avgs) {
		System.out.println(d);
	    }
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
    }
    
    public static void acc() {
	try {
	    Map<String, Integer> correct = new HashMap<String, Integer>();
	    Map<String, Integer> total = new HashMap<String, Integer>();
	    
	    Scanner scanner = new Scanner(new File("votingResult.csv"));
	    scanner.nextLine();
	    while (scanner.hasNextLine()) {
		String line = scanner.nextLine();
		String[] tokens = line.split(",");
		String symbol = tokens[1];
		String actual = tokens[0];
		String pred = tokens[6];
		
		if (tokens[3].equals("null") || tokens[4].equals("null") || tokens[5].equals("null"))
		    continue;
		
		if (total.containsKey(symbol)) {
		    total.put(symbol, total.get(symbol) + 1);
		} else {
		    total.put(symbol, 1);
		}
		
		if (actual.equals(pred)) {
		    if (correct.containsKey(symbol)) {
			correct.put(symbol, correct.get(symbol) + 1);
		    } else {
			correct.put(symbol, 1);
		    }
		}
	    }
	    scanner.close();
	    
	    List<Double> accs = new ArrayList<Double>();
	    for (String symbol : total.keySet()) {
		double acc = correct.containsKey(symbol) ? (correct.get(symbol) + 0.0) / total.get(symbol) : 0;
		accs.add(acc);
		if (acc == 0)
		    System.out.print(symbol + ", ");
	    }
	    Collections.sort(accs);
	    Collections.reverse(accs);
	    
	    for (Double d : accs) {
		System.out.println(d);
	    }
	    
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
    }
    
    public static void sector(String s) {
	try {
	    Map<String, String> secs = new HashMap<String, String>();
	    Scanner scanner = new Scanner(new File("companylist500.csv"));
	    scanner.nextLine();
	    while (scanner.hasNextLine()) {
		String line = scanner.nextLine();
		String[] tokens = line.split(",");
		String symbol = tokens[0];
		String sec = tokens[7];
		secs.put(symbol, sec);
	    }
	    scanner.close();
	    
	    Map<String, Integer> stat = new HashMap<String, Integer>();
	    String[] ss = s.split(",");
	    for (String symbol : ss) {
		String sec = secs.get(symbol.trim());
		if (stat.containsKey(sec)) {
		    stat.put(sec, stat.get(sec) + 1);
		} else {
		    stat.put(sec, 1);
		}
	    }
	    
	    
	    for (String key : stat.keySet()) {
		System.out.println(key + "\t" + stat.get(key));
	    }
	    
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
    }
    
    public static void features(String news) {
	Set<String> features = new HashSet<String>();
	try {
	    Scanner scanner = new Scanner(new File("features.txt"));
	    while (scanner.hasNextLine()) {
		features.add(scanner.nextLine());
	    }
	    scanner.close();
	    
	    Map<String, Integer> tmp = new HashMap<String, Integer>();
	    String[] tokens = NewsTokenizer.tokenize(news);
	    for (String token : tokens) {
		if (features.contains(token)) {
		    if (tmp.containsKey(token)) {
			tmp.put(token, tmp.get(token) + 1);
		    } else {
			tmp.put(token, 1);
		    }
		}
	    }
	    System.out.println(tmp);
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
    }

    public static void main(String[] argv) throws Exception {
	// companyCollect("500symbol.txt");
	// stat();
	// genTopics("500symbol.txt");
	// extractTopics("model-final.twords");
	buildTraining();
	// changeFormat();
	
	// topWords();
	// dist();
	// acc();
	// sector("TOWN, SYBT, USEG, CUI, ASIA, FMNB, UCFC, MTSN, DMLP, OCFC, TSEM, ORRF, LKFN, APFC, WEBK, VVTV, SASR, LBTYB, RLOG, PTNR, GOODO, TLF, VLTC, AMAP, FREE, AMBC, CSWC, PEBK, CNBKA, THRM, HWCC, LPTH, RIVR, BSET, BOTA, MCOX, TITN, WSBF");
	// features("Today we're going to look at Noodles & Co. (NDLS), which is a restaurant chain that's been whetting investors' appetites. They were featured in the Sept. 23 New America article. Their restaurants cook noodle dishes to order, serving everything from Tuscan linguini to pad thai rice to mac 'n cheese. They also have soups, salads and sandwiches. The average price of an entree is $8. And as of July, they had 295 restaurants and 53 franchises in 26 states and the District of Columbia. ?View?Enlarged?Image Analysts say that fast-casual is the fastest growing segment of the restaurant industry, with sales up 10% last year, which is better than the 4% growth logged by the industry has a whole. Experts say Noodles can open shops in places others cannot. That's because even with lower volume they get the same profitability. Each of their restaurants averages about $1 million in sales and roughly 20% profitability. They are also helped by the fact that pasta carries a high profit margin. Most of their restaurants are developed in shopping centers in leased locations, which reduces the amount of cash required to open new stores, compared to heavier cash outlays of owning the building Key Fundamentals ? Sales growth ranged from 16% to 21% the past five quarters. ? Earnings growth slowed in the first quarter, but rose 30% last quarter. ? The firm is slated to release third-quarter earnings Oct. 29, analyst are expecting 11 cents a share, which would be a 266% increase over the same quarter last year. ? For the full year, analysts see earnings rising 82% in 2013 and 38% in 2014. Stock Checkup Stock Checkup shows the stock's 53 Composite Rating is ranked No. 34 among the 54 stocks in its Retail-Restaurants industry group. And the group is ranked No. 60 among IBD's 197 groups. Chart Analysis The stock came public in late June at 18 a share and quickly jumped to nearly 52 a share. But it's not a good idea to buy a stock in the early days immediately following its IPO. A better strategy is to wait for it to settle down and form its first base pattern. Noodles went on to form a consolidation and has been hitting resistance around 49 a share. So it may be worth keeping an eye on to see if it can break out of that pattern and launch a new climb.");
	// features("BROOMFIELD, Colo., Oct. 22, 2013 (GLOBE NEWSWIRE) -- Noodles & Company (NDLS) today announced that it will host a conference call to discuss its third quarter 2013 financial results on Wednesday, November 6, 2013 at 4:30 PM Eastern Time. Hosting the call will be Kevin Reddy, Chairman and Chief Executive Officer, Keith Kinsey, President and Chief Operating Officer and Dave Boennighausen, Chief Financial Officer. A press release with third quarter 2013 financial results will be issued that same day, shortly after the market close. The conference call can be accessed live over the phone by dialing (877) 303-1298 or for international callers by dialing (253) 237-1032. A replay will be available after the call and can be accessed by dialing (855) 859-2056 or for international callers by dialing (404) 537-3406; the passcode is 86968470. The replay will be available until Wednesday, November 20, 2013. The conference call will also be webcast live from the Company's corporate website at investor.noodles.com under the \"Events & Presentations\" page. An archive of the webcast will be available at the same location on the corporate website shortly after the call has concluded. About Noodles & Company Founded in 1995, Noodles & Company is a fast-casual restaurant chain that serves classic noodle and pasta dishes from around the world. Known as Your World Kitchen, Noodles & Company's globally inspired menu consists of more than 25 fresh, customizable noodle bowls, salads, soups and sandwiches that are prepared quickly using quality ingredients. From healthy to indulgent, spicy to comforting, the menu provides favorites for everyone from kids to adults. Popular dishes include the sweet and spicy Japanese Pan Noodles, zesty Pesto Cavatappi and creamy Wisconsin Mac & Cheese. By Mark Felsenthal and Susan Cornwell WASHINGTON (Reuters) - In the most significant legislative rebuke to President Barack Obama's healthcare overhaul, 39 members of his Democratic Party voted for a Republican bill in the House of Representatives on Friday aimed at undermining his signature? For people with excellent credit. Compare exclusive offers side-by-side and apply online for the card that is right for you. Fast-casual may be all the dining rage, but these two pizza chains have done it right without having to be something they're not. A top Apple Inc executive testified on Friday that Samsung Electronics Co Ltd undermined his company's marketing efforts, reputation and business by selling devices that copied the iPhone and iPad. Apple marketing chief Phil Schiller appeared as a witness during a damages retrial between the two? By Aubrey Belford TACLOBAN, Philippines (Reuters) - Survivors began rebuilding homes destroyed by one of the world's most powerful typhoons and emergency supplies flowed into ravaged Philippine islands, as the United Nations more than doubled its estimate of people made homeless to nearly two? Arizona utility regulators approve monthly fee for solar customers of state's largest utility, impacting shares of SolarCity Corp. and SunPower Corp. Hint: It has nothing to do with tech glitches at HealthCare.gov If you haven't ever asked your elderly relatives for advice about finances, you may want to think twice. Those that grew up in earlier decades certainly had a very different experience managing money than we do today. Their advice can prove very insightful rather than out-dated because frugality? Credit card. Debit card. Gift card. Loyalty card. Membership card.Second credit card. Your wallet has way too many cards in it. Coinhas a plan to get people to stop fumbling with all that plastic. OnThursday, the seven-person startup in San Francisco introduced anall-in-one card, also called Coin,? Microsoft Corp. (NASDAQ: MSFT) is about to potentially be a far different company. Steve Ballmer?s resignation, retirement, or force-out, was sooner than some investors were expecting. Now we have a Bloomberg ... The government wants a piece of your retirement savings -- atleast, it wants the taxes on your annual minimum requireddistribution (MRD). You didn't think that they would let you getaway with simply passing on all your IRA money to your designatedbeneficiary, did you? Every year, starting with the? Business at Costco is thriving. The retailer... NASHVILLE, Tenn. (AP) ? Top Volkswagen officials are trying to quell fears among Tennessee politicians about efforts to work with a union to create a German-style works council at the automaker's lone U.S. plant in Chattanooga. It priced 11.5 million shares at $22 for its initial public offering, above the expected $18 to $20, which itself was upped from a previous estimate of $16 to $18. Goldman Sachs (GS), Bank of America (BAC), and Citigroup (C) were lead underwriters. Local auto dealers are cutting their prices for November. If you're looking for a new car this month, buy now and save as much as you can. Are the ongoing Dreamliner stories an indication of a technological mess or rather a public relations fail that has exaggerated problems? McDonald's has announced it will add a new window to its drive-thru in hopes of speeding up the process and improving its customer service. McDonald's spokeswoman Lisa McComb says the Fast Forward Drive-Thru will be featured in new and renovated restaurants starting next year. With the current bull market pushing five years, Hugh Johnson offers three stocks that he thinks will help extend it even further. CNBC's Courtney Reagan reveals her holiday retail naughty and nice list explaining how payroll taxes and higher health cost may factor into consumer spending. This summer, thousands of fast-food workers in the United States went on strike in cities across the country, demanding their wages be increased to $15 an hour and the ability to unionize. To no one?s ... Get ready for a big week. Cramer is looking at 13 earnings that could confirm or deny strength in the market. Farmers in Colorado made history this month when they harvested ahemp crop -- the first in the United States since the late 1950s.Led by Springfield, Colo. farmer Ryan Loflin who planted the55-acre hemp crop back in May, Loflin and hemp advocates across thenation came to his farm in October to?");
    }
    
}
