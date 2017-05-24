package stylegenerator.validator;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import stylegenerator.core.Sentence;
import stylegenerator.core.Word;

@Data
@AllArgsConstructor
public class InvalidSentence {

	private List<Word> absentWords;

	private Sentence sourceSentence;

}
