package stylegenerator.core;

import java.util.Comparator;
import java.util.List;

public class WordsListComparator implements Comparator<List<Word>> {

	@Override
	public int compare(List<Word> words1, List<Word> words2) {
		if (words1.size() != words2.size()) {
			throw new IllegalStateException(String.format("Words List with different sizes: %s X %s ", words1, words2));
		}

		for (int i = 0; i < words1.size(); i++) {
			int diff = words1.get(i).compareTo(words2.get(i));
			if (diff != 0) {
				return diff;
			}
		}

		return 0;
	}

}
