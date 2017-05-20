package stylegenerator.textgeneration.firstsentencechooser;

import java.util.function.Predicate;

import stylegenerator.core.Sentence;
import stylegenerator.core.StyleParameters;

public class FirstSentenceChooserFactory {
	
	public static Predicate<Sentence> getChooser(StyleParameters parameters) {
		// TODO Debug mode
		if (parameters.isWaitForEndOfText()) {
			return Sentence::isBot;
		}
		if ((parameters.getQuantityOfWords() != null) //
				|| (parameters.getQuantityOfPhrases() != null) //
				|| (parameters.getQuantityOfLines() != null)) {
			return Sentence::isBol;
		}

		throw new IllegalStateException("No Begin of text filter defined. Parameters: " + parameters);
	}

}
