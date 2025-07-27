package feature;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.List;

public class WordCompletionAdapter implements Completer {

    private final WordCompleter completer;
    private static final String NO_SUGGESTIONS_MSG = "(No suggestions available)";
    private static final String EMPTY_INPUT_MSG = "(Start typing for suggestions)";
    
    public WordCompletionAdapter(WordCompleter completer) {
        this.completer = completer;
    }

    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        String currentInput = line.word();
        
        // Don't suggest anything if input is empty
        if (currentInput.isEmpty()) {
            candidates.add(new Candidate(EMPTY_INPUT_MSG, EMPTY_INPUT_MSG, null, null, null, null, false));
            return;
        }
        
        String prefix = currentInput.toLowerCase();
        List<String> suggestions = completer.complete(prefix);
        
        if (suggestions.isEmpty()) {
            // Add a non-selectable message when no suggestions are found
            candidates.add(new Candidate(NO_SUGGESTIONS_MSG, NO_SUGGESTIONS_MSG, null, null, null, null, false));
        } else {
            // Add all valid suggestions
            for (String suggestion : suggestions) {
                candidates.add(new Candidate(suggestion));
            }
        }
    }
}