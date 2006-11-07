package descent.ui.text.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class DNumberRule implements IRule {
	
	protected IToken token;
	
	public DNumberRule(IToken token) {
		this.token = token;
	}

	public IToken evaluate(ICharacterScanner scanner) {
		int c = scanner.read();
		if (Character.isDigit(c)) {
			do {
				c = scanner.read();
			} while(Character.isDigit(c) || c == '_');
			scanner.unread();
			return token;
		}
		scanner.unread();
		return Token.UNDEFINED;
	}

}
