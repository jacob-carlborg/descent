package descent.ui.text.scanners;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

public class DWhitespaceDetector implements IWhitespaceDetector {

	public boolean isWhitespace(char c) {
		return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
	}
}
