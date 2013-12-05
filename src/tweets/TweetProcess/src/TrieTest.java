import java.util.ArrayList;
import java.util.List;

public class TrieTest {
	public static void main(String[] args) {
		Trie trie = new Trie();
		List<String> tokens = new ArrayList<String>();
		List<String> tags = new ArrayList<String>();

		trie.AddWord("$GOOG", 2);
		trie.AddWord("The first car company", 1);
		trie.AddWord("The first car company", 3);
		trie.AddWord("The first cloth company", 19);

		tokens.add("The");
		tags.add("N");
		ArrayList<Integer> list = trie.findWords(tokens, tags);
		System.out.print("Find word \"The\":");
		for (int i = 0; i < list.size(); i++) {
			System.out.print(list.get(i) + "\t");
		}
		System.out.println();
		tokens.clear();
		tags.clear();
		tokens.add("$GOOG");
		tags.add("N");
		list = trie.findWords(tokens, tags);
		System.out.print("Find word \"$GOOG\":");
		for (int i = 0; i < list.size(); i++) {
			System.out.print(list.get(i) + "\t");
		}
		System.out.println();
		tokens.clear();
		tags.clear();
		String[] temp = new String(
				"I bought stock from The first car company $GOOG").split(" ");
		for (int i = 0; i < temp.length; i++) {
			tokens.add(temp[i]);
			tags.add("N");
		}
		list = trie.findPhrase(tokens, 4);
		System.out.println("Find phrase \"The first car company\": ");
		for (int i = 0; i < list.size(); i++) {
			System.out.print(list.get(i) + "\t");
		}
		System.out.println();
		list = trie.findWords(tokens, tags);
		System.out.println("Find symbol in the sentence ");
		for (int i = 0; i < list.size(); i++) {
			System.out.print(list.get(i) + "\t");
		}
	}
}
