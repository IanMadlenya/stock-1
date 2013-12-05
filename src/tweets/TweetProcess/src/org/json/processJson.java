package org.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

public class processJson {
	static BufferedReader buf;
	static ArrayList<String> symbol = new ArrayList<String>();
	static ArrayList<String> name = new ArrayList<String>();

	public void init() {
		try {
			BufferedReader bufTweet = new BufferedReader(new FileReader(
					"/Users/shijieru/Documents/tweet/tweets.2012-09-12"));
			BufferedReader bufStock = new BufferedReader(new FileReader(
					""));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
