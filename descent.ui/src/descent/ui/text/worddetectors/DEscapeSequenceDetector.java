package descent.ui.text.worddetectors;

import org.eclipse.jface.text.rules.IWordDetector;

public class DEscapeSequenceDetector implements IWordDetector {

	public boolean isWordPart(char c) {
		return !Character.isWhitespace(c);
	}

	public boolean isWordStart(char c) {
		return c == '\\';
	}
	
}
