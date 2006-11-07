package descent.ui.text.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class DOperatorRule implements IRule {
	
	/** Java operators */
	private final char[] D_OPERATORS= { ';', '(', ')', '{', '}', '.', '=', '/', '+', '-', '*', '[', ']', '<', '>', ':', '?', '!', ',', '|', '&', '^', '%', '~'};
	/** Token to return for this rule */
	private final IToken fToken;

	/**
	 * Creates a new operator rule.
	 *
	 * @param token Token to use for this rule
	 */
	public DOperatorRule(IToken token) {
		fToken= token;
	}

	/**
	 * Is this character an operator character?
	 *
	 * @param character Character to determine whether it is an operator character
	 * @return <code>true</code> iff the character is an operator, <code>false</code> otherwise.
	 */
	public boolean isOperator(char character) {
		for (int index= 0; index < D_OPERATORS.length; index++) {
			if (D_OPERATORS[index] == character)
				return true;
		}
		return false;
	}

	/*
	 * @see org.eclipse.jface.text.rules.IRule#evaluate(org.eclipse.jface.text.rules.ICharacterScanner)
	 */
	public IToken evaluate(ICharacterScanner scanner) {

		int character= scanner.read();
		if (isOperator((char) character)) {
			do {
				character= scanner.read();
			} while (isOperator((char) character));
			scanner.unread();
			return fToken;
		} else {
			scanner.unread();
			return Token.UNDEFINED;
		}
	}

	/*
	public DOperatorRule(IToken token) {
		super(new DOperatorDetector());
		
		this.addWord("/", token);
		this.addWord("/=", token);
		this.addWord(".", token);
		this.addWord("..", token);
		this.addWord("...", token);
		this.addWord("&", token);
		this.addWord("&=", token);
		this.addWord("&&", token);
		this.addWord("|", token);
		this.addWord("|=", token);
		this.addWord("||", token);
		this.addWord("-", token);
		this.addWord("-=", token);
		this.addWord("--", token);
		this.addWord("+", token);
		this.addWord("+=", token);
		this.addWord("++", token);
		this.addWord("<", token);
		this.addWord("<=", token);
		this.addWord("<<", token);
		this.addWord("<<=", token);
		this.addWord("<>", token);
		this.addWord("<>=", token);
		this.addWord(">", token);
		this.addWord(">=", token);
		this.addWord(">>=", token);
		this.addWord(">>>=", token);
		this.addWord(">>", token);
		this.addWord(">>>", token);
		this.addWord("!", token);
		this.addWord("!=", token);
		this.addWord("!==", token);
		this.addWord("!<>", token);
		this.addWord("!<>=", token);
		this.addWord("!<", token);
		this.addWord("!<=", token);
		this.addWord("!>", token);
		this.addWord("!>=", token);
		this.addWord("!~", token);
		this.addWord("(", token);
		this.addWord(")", token);
		this.addWord("[", token);
		this.addWord("]", token);
		this.addWord("{", token);
		this.addWord("}", token);
		this.addWord("?", token);
		this.addWord(",", token);
		this.addWord(";", token);
		this.addWord(":", token);
		this.addWord("$", token);
		this.addWord("=", token);
		this.addWord("==", token);
		this.addWord("===", token);
		this.addWord("*", token);
		this.addWord("*=", token);
		this.addWord("%", token);
		this.addWord("%=", token);
		this.addWord("^", token);
		this.addWord("^=", token);
		this.addWord("~", token);
		this.addWord("~=", token);
		this.addWord("~~", token);
	}
	*/

}
