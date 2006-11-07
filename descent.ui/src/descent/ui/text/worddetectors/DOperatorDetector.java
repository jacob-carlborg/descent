package descent.ui.text.worddetectors;

import org.eclipse.jface.text.rules.IWordDetector;

public class DOperatorDetector implements IWordDetector {

	public boolean isWordPart(char c) {
		return CharacterUtil.isSymbol(c);
	}

	public boolean isWordStart(char c) {
		return CharacterUtil.isSymbol(c);		
	}
	
}
