import java.util.ArrayList;
import java.util.HashMap;

public class TrieNode {
	char value;
	ArrayList<Integer> index;
	HashMap<Character, TrieNode> map;

	TrieNode(char v) {
		value = v;
		index = new ArrayList<Integer>();
		map = new HashMap<Character, TrieNode>();
	}
}
