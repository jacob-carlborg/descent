package mmrnmhrm.ui.internal.text;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

public class LangWhitespaceDetector implements IWhitespaceDetector {

	public boolean isWhitespace(char character) {
		return Character.isWhitespace(character);
	}
}
