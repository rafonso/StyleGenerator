package stylegenerator.textgeneration.terminator;

import stylegenerator.core.StyleParameters;

public class TextTerminatorFactory {

	public static TextTerminator getTerminator(StyleParameters parameters) {
		if (parameters.isWaitForEndOfText()) {
			return new EndOfTextTerminator();
		}
		if (parameters.getQuantityOfWords() != null) {
			if (parameters.isWaitForEndOfPrhase()) {
				return new QuantityOfWordsEndOfPhraseTerminator(parameters.getQuantityOfWords());
			}
			return new QuantityOfWordsTerminator(parameters.getQuantityOfWords());
		}
		if (parameters.getQuantityOfPhrases() != null) {
			return new QuantityOfPhrasesTerminator(parameters.getQuantityOfPhrases());
		}
		if (parameters.getQuantityOfLines() != null) {
			return new QuantityOfLinesTerminator(parameters.getQuantityOfLines());
		}

		throw new IllegalStateException("No text terminator defined. Parameters: " + parameters);
	}

}
