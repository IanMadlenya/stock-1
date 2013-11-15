package stock.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewsTokenizer {

    /** A regular expression for letters and numbers. */
    private static final String regexLetterNumber = "[a-zA-Z0-9]";
 
    /** A regular expression for non-letters and non-numbers. */
    private static final String regexNotLetterNumber = "[^a-zA-Z0-9]";
 
    /** A regular expression for separators. */
    private static final String regexSeparator = "[!]";
    
    private static final String regexUselessSeparator = "[\\?!()\";/\\|`,><]";

    /** A regular expression for separators. */
    private static final String regexClitics =
        "'|:|-|'S|'D|'M|'LL|'RE|'VE|N'T|'s|'d|'m|'ll|'re|'ve|n't";
 
    /** Abbreviations. */
    private static final List<String> abbrList =
        Arrays.asList("Co.", "Corp.", "vs.", "e.g.", "etc.", "ex.", "cf.",
            "eg.", "Jan.", "Feb.", "Mar.", "Apr.", "Jun.", "Jul.", "Aug.",
            "Sept.", "Oct.", "Nov.", "Dec.", "jan.", "feb.", "mar.",
            "apr.", "jun.", "jul.", "aug.", "sept.", "oct.", "nov.",
            "dec.", "ed.", "eds.", "repr.", "trans.", "vol.", "vols.",
            "rev.", "est.", "b.", "m.", "bur.", "d.", "r.", "M.", "Dept.",
            "MM.", "U.", "Mr.", "Jr.", "Ms.", "Mme.", "Mrs.", "Dr.",
            "Ph.D.");
    
    private static Set<String> stopwords;
     
    public static synchronized void readStopWords() {
	if (stopwords == null) {
	    try {
		Scanner scanner = new Scanner(new File("common-english-words.txt"));
		String line = scanner.nextLine();
		String[] tokens = line.split(",");
		stopwords = new HashSet<String>();
		for (String token : tokens) {
		    stopwords.add(token);
		}
		scanner.close();
	    } catch (FileNotFoundException e) {
		e.printStackTrace();
	    }
	}
    }
    
    public static String[] tokenize(String str) {
	
	if (stopwords == null) {
	    readStopWords();
	}
	
	List<String> tokenList = new ArrayList<String>();
	
	str = str.toLowerCase();
	
        // Changes tabs into spaces.
        str = str.replaceAll("\\t", " ");
 
        // Puts blanks around unambiguous separators.
        str = str.replaceAll("(" + regexUselessSeparator + ")", "");
        str = str.replaceAll("(" + regexSeparator + ")", " $1 ");
 
        // Puts blanks around commas that are not inside numbers.
        str = str.replaceAll("([^0-9]),", "$1 , ");
        str = str.replaceAll(",([^0-9])", " , $1");
 
        // Distinguishes single quotes from apstrophes by segmenting off
        // single quotes not preceded by letters.
        str = str.replaceAll("^(')", "$1 ");
        str = str.replaceAll("(" + regexNotLetterNumber + ")'", "$1 '");
 
        // Segments off unambiguous word-final clitics and punctuations.
        str = str.replaceAll("(" + regexClitics + ")$", " $1");
        str = str.replaceAll(
                "(" + regexClitics + ")(" + regexNotLetterNumber + ")",
                " $1 $2");
 
        // Deals with periods.
        String[] words = str.trim().split("\\s+");
        Pattern p1 = Pattern.compile(".*" + regexLetterNumber + "\\.");
        Pattern p2 = Pattern.compile(
            "^([A-Za-z]\\.([A-Za-z]\\.)+|[A-Z][bcdfghj-nptvxz]+\\.)$");
        for (String word : words) {
            Matcher m1 = p1.matcher(word);
            Matcher m2 = p2.matcher(word);
            if (m1.matches() && !abbrList.contains(word) && !m2.matches()) {
                // Segments off the period.
        	if (!stopwords.contains(word.substring(0, word.length() - 1))) {
        	    tokenList.add(word.substring(0, word.length() - 1));
        	    //tokenList.add(word.substring(word.length() - 1));
        	}
            } else {
        	if (!stopwords.contains(word)) {
        	    tokenList.add(word);
        	}
            }
        }
        return tokenList.toArray(new String[0]);
    }
    
    public static void main(String[] argv) {
	tokenize("S&P ekes out gain, Nasdaq sets fresh 12-year high; FB closes below $38 GO Loading... >> View All Results for \"\" Enter multiple symbols separated by commas London quotes now available GO HOME EDITION NEWS U.S. Asia Europe Economy EARNINGS Energy Inside Wealth Politics Technology Blogs Slideshows Special Reports Corrections MARKETS Pre-Markets U.S. Europe Asia Stocks Commodities Currencies Bonds Funds ETFs INVESTING Stock Blog Personal Finance CNBC Explains Portfolio Watchlist Stock Screener Fund Screener Financial Advisors Small Business Franchising Financing Management Video VIDEO Latest Video Top Video U.S. Video Europe Video Asia Video CEO Interviews Analyst Interviews Digital Workshop Full Episodes SHOWS Watch Live CNBC U.S. CNBC Asia-Pacific CNBC Europe CNBC World Full Episodes Watch Live PRO Register | Sign In Member Center Member Center Sign Out X US Markets Market Movers Dow 30 NASDAQ 100 Sectors S&P ekes out gain, Nasdaq sets fresh 12-year high; FB closes below $38 ???Text Size???Published: Tuesday, 30 Jul 2013 | 5:13 PM ET By: JeeYeon Park | CNBC.com Stock Market Writer The Dow and S&P 500 closed near the flatline Tuesday as investors remained cautious ahead of the Federal Reserve's policy statement, but techs climbed to lift the Nasdaq to a fresh 12-year high. (Read more: After-hours buzz:Amgen, Aflac, Symantec & More) ? Name Price ? Change %Change DJIA --- S&P 500 --- NASDAQ --- The Dow Jones Industrial Average slipped 1.38 points to finish at 15,520.59, dragged by Verizon and AT&T. The S&P 500 eked out a gain of 0.63 points to close at 1,685.96. The Nasdaq rose 17.33 points to end at 3,616.47, logging its highest close in nearly 12 years. The CBOE Volatility Index (VIX), widely considered the best gauge of fear in the market, closed unchanged above 13. Among key S&P sectors, telecoms dragged, while techs finished higher. Time to buy big-cap stocks? Can a little botox help boost your portfolio? Kim Forrest, Pitt Capital Total Return Fund, provides ways to help a sagging portfolio. Among techs, Facebook advanced more than 6 percent after the social-networking giant announced the launch of Facebook Mobile Games Publishing, a \"pilot program to help small and medium-sized developers take their mobile games global.\" The stock has surged more than 45 percent so far this month and is trading close to its IPO price of $38 a share. (Read more: Facebook nearing its IPO price. Here's why) And Apple rose above $450 a share for the first time in over a month. The stock has soared nearly 14 percent so far this month and is having its best month since last February. On the earnings front, Pfizer gained after the U.S.'s largest drugmaker posted numbers that were slightly ahead of expectations. Rival pharmaceutical company Merck topped profit expectations but showed weakness on revenue. And BP posted earnings that missed analysts' expectations, but warned that its $20 billion oil-spill compensation fund has almost run out. Following the earnings report, CEO Bob Dudley told CNBC that class action lawsuits like the ones BP is facing in the U.S. are a \"business model\" that serve only to benefit attorneys. Shares declined. Aetna posted better-than-expected quarterly results, following similar earnings beats by larger rivals UnitedHealth and WellPoint. With quarterly results in from nearly 60 percent of the S&P 500 companies, 67 percent have beaten earnings expectations?in line with the average beat over the last four quarters. Meanwhile, about 55 percent of the companies have beaten revenue expectations, more than the 48 percent of revenue beats in the past four earnings seasons, according to the latest data from Thomson Reuters. If all remaining companies report earnings in line with estimates, earnings will be up 4 percent from last year's second quarter. (Read more: What earnings seasonis saying about road ahead) Are stocks overvalued? Paul Hickey, Bespoke Investment Group, joins \"Street Signs\" to co-host. Bespoke says a \"typical bull market P/E ratio peak is 18.2 in the S&P 500,\" Hickey explains. Amgen, Aflac, Symantec and Take Two Interactive are among notable companies scheduled to report after the closing bell. But trading volume is likely to be muted again as investors look ahead to the statement from Federal Reserve's policy-setting meeting. In addition, the first reading of second-quarter gross domestic product will be due Wednesday. And the closely-watched July employment report will be reported Friday. The Fed is expected to maintain its accommodative monetary policy, but investors will be looking for hints on when the central bank might start scaling back on its monthly bond-buying program. In addition, markets will also be eager for hints for who may replace Ben Bernanke next year as Fed Chairman. (Read more: Fed expectations: will they stay or will they slow?) \"It's a pivotal time for Fed policy, and it would be advantageous to have someone at the helm who had already gone through the analytical process of getting comfortable with the last five years of policy, rather than coming in with a completely clean slate and trying to potentially reinvent it,\" said Ian Lyngen, senior Treasury strategist at CRT Capital. (Read more: Fed intrigue, not policy, has market attention) On the economic front, single-family home prices rose 1 percent in May on a seasonally adjusted basis, according to the S&P/Case-Shiller composite index of 20 metropolitan areas. Economists polled by Reuters expected a gain of 1.5 percent. Meanwhile, consumer confidence slipped slightly to a reading of 80.3 in July from an upwardly revised 82.1 in June, according to the Conference Board. Economists surveyed by Reuters expected a reading of 81.1. Mosaic plunged nearly 20 percent after Russia's Uralkali pulled out of a venture with its partner in Belarus, a move it expects will cause global prices to plunge by 25 percent. Uralkali and Belarus potash maker Belaruskali were partners for eight years in BPC, which accounts for 43 percent of the global potash export market. Shares of Potash and Agrium also tumbled sharply. Community Health Systems announced it would acquire smaller Health Management Associates in a $3.9 billion deal. Shares of both company finished in the red following the news. JPMorgan closed lower after the banking giant said it would pay a total of $410 million to settle allegations of energy market manipulation in California and the Midwest. The Japanese yen weakened against the dollar to above the 98-level in early trade, leading the export-heavy Nikkei index to rally 1.5 percent. Meanwhile, South Korea's Kospi added 0.9 percent and the Shanghai Composite traded within sight of the 2,000 mark. European shares rose after an upbeat German consumer confidence report. The Gfk research institute said its forward-looking consumer index hit its highest level since September 2007, thanks to an improving labor market and expectations for more robust economic growth. ?By CNBC's JeeYeon Park (Follow JeeYeon on Twitter: @JeeYeonParkCNBC) On Tap This Week: WEDNESDAY: MBA mortgage applications, ADP employment report, GDP, employment cost index, Chicago PMI, oil inventories, FOMC mtg announcement, farm prices, Booz Allen Hamilton investor call, Electronic Arts shareholder mtg; Earnings from Comcast, Honda, MasterCard, Hess, Humana, Sodastream, Allstate, CBS, Marriott, MetLife, Whole Foods, Dreamworks Animation, Yelp THURSDAY: Challenger job-cut report, jobless claims, PMI manufacturing index, ISM mfg index, construction spending, natural gas inventories, Fed balance sheet/money supply, auto sales, JCPenney vs. Martha Stewart closing arguments, Michael Kors shareholder mtg, Motorola Moto X launch; Earnings from AstraZeneca, ConocoPhillips, ExxonMobil, P&G, Royal Dutch Shell, Barrick Gold, Cigna, Clorox, Time Warner Cable, AIG, Kraft Foods, LinkedIn, Leap Wireless FRIDAY: Nonfarm payrolls, personal icome & outlays, factory orders, Congress breaks for summer, Dell shareholders mtg, Detroit bankruptcy hearing; Earnings from Chevron, Toyota, Viacom What's Trending on CNBC.com: Why China risks falling into Japan-style economic coma Wal-Mart's Washington showdown over wage law Markets focused on Fed policy? Nope, it's on this ?Print ?Email The Dow and S&P 500 closed near the flatline Tuesday, while techs climbed to boost the Nasdaq to a fresh 12-year high, as investors remained cautious ahead of the Federal Reserve's policy statement. Related Follow @JeeYeonParkCNBC on Twitter. What should we expect from the Fed? Fed intrigue, not policy, has market attention China risks falling into economic coma Wall St.: Fall taper mostly priced in Home prices surge most since 2006 Wal-Mart, Washington in wage showdown After-hours buzz: AMGN, AFL, SYMC & More Stocks end lower ahead of Fed meeting Why is Facebook pushing its IPO price? Consumer confidence retreats in July What earnings say about the road ahead Fertilizer makers sink on Russia shock Europe shares close higher despite mixed data Investors look for 'shift' in sentiment: Expert???? 'Night of the living Fed' tapering fears???? Top stock plays in a nervous market: Expert???? Are stocks overvalued????? Time to buy big-cap stocks????? Markets Dow Jones Industrial Average S&P 500 Index Nasdaq Composite Index Pre-Markets Merck & Co Inc Amgen Inc Aflac Inc Symantec Corp Take-Two Interactive Software Inc Facebook Apple Inc Aetna Inc UnitedHealth Group Inc WellPoint Inc Mosaic Co Potash Corporation of Saskatchewan Inc Agrium Inc Community Health Systems Inc Health Management Associates Inc JPMorgan Chase and Co Stocks Economy ? Price ? Change %Change DJIA --- S&P 500 --- NASDAQ --- MRK --- AMGN --- AFL --- SYMC --- TTWO --- FB --- AAPL --- AET --- UNH --- WLP --- MOS --- POT --- AGU --- CYH --- HMA --- JPM --- ? ? Comments ? More Comments ? ? Add Comments ? Your Comments (Up to 1100 characters): Remaining characters Preview Comment CNBC welcomes your contribution. Please respect our community and the integrity of its participants. CNBC reserves the right to moderate and approve your comment. Your comments have not been posted yet. Please review your submission to make sure you are comfortable with your entry. Your Comments: Edit Comment Submit Comment More From US Stock Indexes Commodities Treasurys Market Insider with Patti Domm Early movers: AAPL, BBRY, TOL, AA, APO, CTB & more Reasons for optimism, and fear, in budget battle Early movers: ADBE, WYNN, OPEN & more Brace yourselves: A longer shutdown is coming Early movers: STZ, BBRY, LLY, DPZ, FB, VOD & more NetNet Relax. The markets aren't running on QE anyway It's Fed Day: Here's what to watch for Taper off! Fed keeps foot on the easy money pedal Layoffs at Chesapeake just keep coming Summers out, sentiment up?watch out: Yoshikami Trader Talk with Bob Pisani The 'what, me worry?' rally continues Reasons for optimism, and fear, in budget battle Stocks grapple with triple witching of lousy news Brace yourselves: A longer shutdown is coming Stock market comes off low in day two of shutdown Futures Now The market?s worries extend beyond Washington: Pro Why the shutdown could mean no tapering this year Ron Paul: GOP 'not serious' about spending cuts As the shutdown continues, I?m getting short: Pro Ron Paul: Congress worse than the Fed???? Fast Money? Mad Money with Jim Cramer Long-term investing: Don't make the big mistake Cramer's Holy Grail: A stock above all others The right price: Cramer's strategy for buying stocks Company 401(k) plans: Cramer reveals the dirty secret Cramer: Stock market could get very ugly Fast Money Apple?s cash ?a little bit silly?: Wilbur Ross Beware DC's ?real, real scary deadline?: Analyst Global recovery driven by US: Pro???? Questions from Twitter: LULU, JCP & more???? Apple should tell Carl Icahn to ?stuff it?: Blodget Options Action Is Tesla's run ending????? 3 charts that could spell doom???? Making friends with Facebook???? The Final Call???? Options Action: Buy social media ahead of Twitter IPO????? Top News & Analysis Fill 'er up! Gas prices in biggest drop since 2012 Boehner takes a hard line in call for concessions Danger grows as US shutdown heads into second week Supreme Court opening, but maybe not for long Shutdown could hit IPOs?but what about Twitter? Most Popular Stories When will the next US recession come? Misunderstanding debt ceilings and defaults Paulson leads hedge funds charge into Greek banks Bloomberg challenged by new messaging system Europe at 4-week low as shutdown lingers; DAX down 1% Most Popular Video Swiss franc, yen good options amid shutdown: Pro???? Can Boehner survive the shutdown? ???? Airbus chosen by JAL on 'quality': CEO???? As East Asia growth slows, where should you invest in????? Sanofi: Not worried about TPP impact on pharma sector???? Most Shared Fill 'er up! Gas prices in biggest drop since 2012 Danger grows as US shutdown heads into second week When will the next US recession come? Europe at 4-week low as shutdown lingers; DAX down 1% Indonesia Finance Minister ?comfortable? with rupiah level NEWS U.S. Asia Europe Economy Earnings Energy Inside Wealth Politics Technology Blogs Slideshows Special Reports Corrections MARKETS Pre-Markets U.S. Europe Asia Stocks Commodities Currencies Bonds Funds ETFs INVESTING Stock Blog Personal Finance CNBC Explains Portfolio Watchlist Stock Screener Fund Screener Financial Advisors SMALL BUSINESS Franchising Financing Management Video VIDEO Latest Video Top Video U.S. Video Europe Video Asia Video CEO Interviews Analyst Interviews Digital Workshop Full Episodes Closed Captioning SHOWS Watch Live CNBC U.S. CNBC Asia-Pacific CNBC Europe CNBC World Full Episodes WATCH LIVE PRO CNBC About CNBC Site Map Video Reprints Advertise Careers Help Contact Privacy Policy Terms of Service Independent Programming Report Latest News Releases RSS Certain market data provided by Thomson ReutersData also provided by Data is a real-time snapshot *Data is delayed at least 15 minutes Global Business and Financial News, Stock Quotes, and Market Data and Analysis ? 2013 CNBC LLC. All Rights Reserved. A Division of NBCUniversal");
    }
    
}
