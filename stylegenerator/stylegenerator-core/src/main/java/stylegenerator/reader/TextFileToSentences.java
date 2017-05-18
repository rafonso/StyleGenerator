package stylegenerator.reader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import stylegenerator.core.Constants;
import stylegenerator.core.Sentence;
import stylegenerator.core.TextFile;
import stylegenerator.core.Word;

public class TextFileToSentences implements Function<TextFile, List<Sentence>> {

	private final int coherence;

	public TextFileToSentences(int coherence) {
		this.coherence = coherence;
	}

	private List<Sentence> parseText(List<Word> words, int coherence) {
		Queue<Word> wordQueue = new LinkedList<>();
		Iterator<Word> itWords = words.iterator();
		while (wordQueue.size() < coherence) {
			wordQueue.add(itWords.next());
		}

		List<Sentence> sentences = new ArrayList<>();
		while (itWords.hasNext()) {
			Word sequence = itWords.next();
			Sentence sentence = new Sentence(new ArrayList<>(wordQueue));
			sentence.addSequence(sequence);

			sentences.add(sentence);

			wordQueue.poll();
			wordQueue.add(sequence);
		}

		return sentences;
	}

	private Word tokenToWord(String token, Word priorWord, boolean eot) {
		boolean eol = false;
		if (token.endsWith(Constants.EOL)) {
			token = token.replace(Constants.EOL, "");
			eol = true;
		}

		Word word = new Word(token);
		word.setBot(priorWord == null);
		word.setBol(word.isBot() || priorWord.isEol());
		word.setBop(word.isBol() || priorWord.isEop());
		word.setEot(eot);
		word.setEol(word.isEot() || eol);
		word.setEop(token.endsWith(".") || token.endsWith("?") || token.endsWith("!"));

		return word;
	}

	private List<Word> tokensToWords(List<String> tokens) {
		List<Word> words = new ArrayList<>(tokens.size());
		Word priorWord = null;

		for (Iterator<String> itToken = tokens.iterator(); itToken.hasNext();) {
			Word word = tokenToWord(itToken.next(), priorWord, !itToken.hasNext());

			words.add(word);
			priorWord = word;
		}

		return words;
	}

	private Stream<String> lineToTokens(String line) {
		String[] words = line.split("( |\\t)+");
		for (int i = 0; i < words.length; i++) {
			words[i] = words[i].trim();
		}
		words[words.length - 1] = words[words.length - 1] + Constants.EOL;

		return Stream.of(words);
	}

	@Override
	public List<Sentence> apply(TextFile textFile) {
		List<String> tokens = Stream.of(textFile.getText().split(Constants.EOL)) //
				.map(String::trim) //
				.flatMap(this::lineToTokens) //
				.collect(Collectors.toList());

		List<Word> words = tokensToWords(tokens);

		return this.parseText(words, coherence);
	}

}
