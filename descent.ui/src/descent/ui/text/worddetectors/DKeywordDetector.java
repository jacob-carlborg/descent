package descent.ui.text.worddetectors;

import org.eclipse.jface.text.rules.IWordDetector;

public class DKeywordDetector implements IWordDetector {

	public boolean isWordPart(char c) {
		return Character.isJavaIdentifierPart(c);
		//return !Character.isWhitespace(c) && !CharacterUtil.isSymbol(c);
	}

	public boolean isWordStart(char c) {
		return Character.isJavaIdentifierStart(c);
		//return !Character.isWhitespace(c) && !CharacterUtil.isSymbol(c);
	}
	
}
