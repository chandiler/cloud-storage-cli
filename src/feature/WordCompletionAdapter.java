package feature;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.List;

public class WordCompletionAdapter implements Completer {

	private final WordCompleter completer;

	public WordCompletionAdapter(WordCompleter completer) {
		this.completer = completer;
	}

	@Override
	public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
		String prefix = line.word().toLowerCase();
		List<String> suggestions = completer.complete(prefix);
		for (String suggestion : suggestions) {
			candidates.add(new Candidate(suggestion));
		}
	}
}
