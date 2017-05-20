package stylegenerator.textgeneration.firstsentencechooser;

import java.util.function.Predicate;

import stylegenerator.core.Sentence;
import stylegenerator.textgeneration.TextParameter;

public class FirstSentenceChooserFactory {
	
	public static Predicate<Sentence> getChooser(TextParameter parameter) {
		// TODO Debug mode
		if (parameter.isWaitForEndOfText()) {
			return Sentence::isBot;
		}
		if ((parameter.getQuantityOfWords() != null) //
				|| (parameter.getQuantityOfPhrases() != null) //
				|| (parameter.getQuantityOfLines() != null)) {
			return Sentence::isBol;
		}

		throw new IllegalStateException("No Begin of text filter defined. Parameters: " + parameter);
	}

}
