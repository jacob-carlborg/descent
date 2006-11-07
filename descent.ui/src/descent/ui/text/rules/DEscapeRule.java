package descent.ui.text.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class DEscapeRule implements IPredicateRule {
	
	private IToken token;
	private int[] singleChars = new int[] { '\'', '"', '?', '\\', 'a', 'b', 'f', 'n', 'r', 't', 'v', ICharacterScanner.EOF };
	
	public DEscapeRule(IToken token) {
		this.token = token;
	}

	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
		int c = scanner.read();
		if (c == '\\') {
			
			// simple escape sequences
			c = scanner.read();
			for(int single : singleChars) {
				if (c == single) {
					return token;
				}
			}
			
			// is it hexadecimal?
			if (c == 'x') {
				int d = scanner.read();
				if (isHexadecimal(d)) {
					int e = scanner.read();
					if (isHexadecimal(e)) {
						return token;
					}
					scanner.unread();
				}
				scanner.unread();
			}
			
			// is it octal?
			if (isOctal(c)) {
				int d = scanner.read();
				if (isOctal(d)) {
					int e = scanner.read();
					if (isOctal(e)) {
						return token;
					}
					scanner.unread();
					return token;
				}
				scanner.unread();
				return token;
			}
			
			// is it unicode "u"?
			if (c == 'u') {
				int i = 0;
				for(; i < 4; i++) {
					int d = scanner.read();
					if (!isHexadecimal(d)) {
						break;
					}
				}
				
				if (i == 4) {
					return token;
				} else {
					for(; i > 0; i--) {
						scanner.unread();
					}
				}
			}
			
			// is it unicode "U"?
			if (c == 'U') {
				int i = 0;
				for(; i < 8; i++) {
					int d = scanner.read();
					if (!isHexadecimal(d)) {
						break;
					}
				}
				
				if (i == 8) {
					return token;
				} else {
					for(; i > 0; i--) {
						scanner.unread();
					}
				}
			}
			
			scanner.unread();
		}
		scanner.unread();
		return Token.UNDEFINED;
	}

	public IToken getSuccessToken() {
		return token;
	}

	public IToken evaluate(ICharacterScanner scanner) {
		return evaluate(scanner, false);
	}
	
	private boolean isHexadecimal(int c) {
		return ('0' <= c && c <= '9') ||
			('a' <= c && c <= 'f') ||
			('A' <= c && c <= 'F');
	}
	
	private boolean isOctal(int c) {
		return ('0' <= c && c <= '7');
	}

}
