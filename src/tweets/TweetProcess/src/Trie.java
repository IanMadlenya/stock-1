import java.util.ArrayList;
import java.util.List;

public class Trie {
	TrieNode root;

	Trie() {
		root = new TrieNode(' ');
	}
	/**
	 * Give a stock symbol or stock name and its corresponding index, store it
	 * into the trie
	 * 
	 * @param str
	 */
	void AddWord(String str, int index) {
		TrieNode curr = root;
		for (int i = 0; i < str.length(); i++) {
			if (curr.map.containsKey(str.charAt(i)))
				curr = curr.map.get(str.charAt(i));
			else {
				TrieNode node = new TrieNode(str.charAt(i));
				curr.map.put(str.charAt(i), node);
				curr = node;
			}
		}
		curr.index.add(index);

	}
	/**
	 * Give a list of tokens from one sentence, check the token one by one and
	 * see if it is the particular stock name we want to find
	 * 
	 * @param tokens
	 * @return
	 */
	ArrayList<Integer> findWords(List<String> tokens, List<String> tags) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < tokens.size(); i++) {
			// Get stock indexes for stock name starting with tokens[i]
			result.addAll(findPhrase(tokens, i));
			// This is the case with one word(the same company name could
			// correspond to different) for stock name
			ArrayList<Integer> temp = findWord(tokens.get(i));
			if (tokens.get(i).startsWith("$") || checkValid(tags, i))
				result.addAll(temp);

		}
		return result;
	}
	/**
	 * Check if the tag is valid or not
	 * 
	 * @param tags
	 * @param index
	 * @return
	 */
	boolean checkValid(List<String> tags, int index) {
		String tag = tags.get(index);
		if (tag.equals("N") || tag.equals("O") || tag.equals("^")
				|| tag.equals("S") || tag.equals("Z")) {
			return true;
		} else
			return false;
	}
	/**
	 * Give a list of tokens, check if the phrase begin with specific token(not
	 * including only one word) is in the trie, if exists, return stock name
	 * indexes, otherwise, return empty list.
	 * 
	 * @param tokens
	 * @param index
	 * @return
	 */
	ArrayList<Integer> findPhrase(List<String> tokens, int index) {
		int start = index;
		ArrayList<Integer> result = new ArrayList<Integer>();
		TrieNode curr = root;
		String token = null;
		while (index < tokens.size()) {
			token = tokens.get(index);
			for (int i = 0; i < token.length(); i++) {
				if (!curr.map.containsKey(token.charAt(i)))
					return result;
				else
					curr = curr.map.get(token.charAt(i));
			}
			if (index != start)
				result.addAll(curr.index);
			if (curr.map.containsKey(' ')) {
				index++;
				curr = curr.map.get(' ');
			} else
				return result;
		}
		return result;
	}

	/**
	 * Give a string, check if it is in the trie, if exists, return stock name
	 * indexes, otherwise, return empty list.
	 * 
	 * @param token
	 * @return
	 */
	ArrayList<Integer> findWord(String token) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		TrieNode curr = root;
		for (int i = 0; i < token.length(); i++) {
			if (!curr.map.containsKey(token.charAt(i)))
				return result;
			else
				curr = curr.map.get(token.charAt(i));
		}
		result.addAll(curr.index);
		return result;
	}
}
