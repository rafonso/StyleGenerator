package stylegenerator.textgeneration.terminator;

import stylegenerator.textgeneration.TextParameter;

public class TextTerminatorFactory {

	public static TextTerminator getTerminator(TextParameter parameter) {
		if (parameter.isWaitForEndOfText()) {
			return new EndOfTextTerminator();
		}
		if (parameter.getQuantityOfWords() != null) {
			if (parameter.isWaitForEndOfPrhase()) {
				return new QuantityOfWordsEndOfPhraseTerminator(parameter.getQuantityOfWords());
			}
			return new QuantityOfWordsTerminator(parameter.getQuantityOfWords());
		}
		if (parameter.getQuantityOfPhrases() != null) {
			return new QuantityOfPhrasesTerminator(parameter.getQuantityOfPhrases());
		}
		if (parameter.getQuantityOfLines() != null) {
			return new QuantityOfLinesTerminator(parameter.getQuantityOfLines());
		}

		throw new IllegalStateException("No text terminator defined. Parameters: " + parameter);
	}

}
