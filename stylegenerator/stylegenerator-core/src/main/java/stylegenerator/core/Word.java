package stylegenerator.core;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Word implements Comparable<Word> {

	private String value;

	@JsonInclude(value = Include.NON_DEFAULT)
	private boolean bot;

	@JsonInclude(value = Include.NON_DEFAULT)
	private boolean bol;

	@JsonInclude(value = Include.NON_DEFAULT)
	private boolean bop;

	@JsonInclude(value = Include.NON_DEFAULT)
	private boolean eop;

	@JsonInclude(value = Include.NON_DEFAULT)
	private boolean eol;

	@JsonInclude(value = Include.NON_DEFAULT)
	private boolean eot;

	public Word(String value) {
		this.value = value;
	}

	private void appendFlag(boolean flag, String description, List<String> flags) {
		if (flag) {
			flags.add(description);
		}
	}

	@Override
	public int compareTo(Word o) {
		if (!this.value.equalsIgnoreCase(o.value)) {
			return this.value.compareToIgnoreCase(o.value);
		}
		if (this.bot != o.bot) {
			return this.bot ? 1 : -1;
		}
		if (this.bol != o.bol) {
			return this.bol ? 1 : -1;
		}
		if (this.bop != o.bop) {
			return this.bop ? 1 : -1;
		}
		if (this.eop != o.eop) {
			return this.eop ? -1 : 1;
		}
		if (this.eol != o.eol) {
			return this.eol ? -1 : 1;
		}
		if (this.eot != o.eot) {
			return this.eot ? -1 : 1;
		}
		return 0;
	}

	@Override
	public String toString() {
		List<String> flags = new ArrayList<String>();
		appendFlag(bot, "BOT", flags);
		appendFlag(bol, "BOL", flags);
		appendFlag(bop, "BOP", flags);
		appendFlag(eop, "EOP", flags);
		appendFlag(eol, "EOL", flags);
		appendFlag(eot, "EOT", flags);

		String strFlags = flags.isEmpty() ? "" : flags.stream().collect(Collectors.joining(", ", "(", ")"));

		return "'" + value + "'" + strFlags;
	}

}
