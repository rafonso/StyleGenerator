package stylegenerator.textgeneration;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import stylegenerator.core.Constants;
import stylegenerator.core.Word;

public class TextBuilderTest {

	TextBuilder textBuilder;

	@Before
	public void setUp() {
		textBuilder = new TextBuilder();
	}

	@Test(expected = NullPointerException.class)
	public void addNullWord() {
		textBuilder.append(null);
	}

	@Test
	public void addSingleSimpleWord() {
		Word word1 = Word.builder().value("Test").build();

		textBuilder.append(word1);

		assertEquals(word1.getValue(), textBuilder.build());
	}

	@Test
	public void add2SimpleWord2() {
		Word word1 = Word.builder().value("Hello").build();
		Word word2 = Word.builder().value("world").build();

		textBuilder.append(word1).append(word2);

		assertEquals(word1.getValue() + " " + word2.getValue(), textBuilder.build());
	}

	@Test
	public void addSimpleWordSimpleWordBopWord() {
		Word word1 = Word.builder().value("Hello").build();
		Word word2 = Word.builder().value("world.").build();
		Word word3 = Word.builder().value("OK?").bop(true).build();

		textBuilder.append(word1).append(word2).append(word3);

		assertEquals(word1.getValue() + " " + word2.getValue() + " " + word3.getValue(), textBuilder.build());
	}

	@Test
	public void addBolWordSimpleWord() {
		Word word1 = Word.builder().value("Hello").bol(true).build();
		Word word2 = Word.builder().value("world").build();

		textBuilder.append(word1).append(word2);

		assertEquals(word1.getValue() + " " + word2.getValue(), textBuilder.build());
	}

	@Test
	public void addBolWordBolWord() {
		Word word1 = Word.builder().value("Hello").bol(true).build();
		Word word2 = Word.builder().value("world").bol(true).build();

		textBuilder.append(word1).append(word2);

		assertEquals(word1.getValue() + Constants.EOL + word2.getValue(), textBuilder.build());
	}

}
