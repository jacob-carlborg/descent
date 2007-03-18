package mmrnmhrm.ui.text;

import org.eclipse.jface.text.rules.IToken;

public class EOFToken implements IToken {

	private final static EOFToken defaultInstance = new EOFToken();

	public Object getData() {
		return null;
	}

	public boolean isEOF() {
		return true;
	}

	public boolean isOther() {
		return false;
	}

	public boolean isUndefined() {
		return false;
	}

	public boolean isWhitespace() {
		return false;
	}

	public static EOFToken getDefault() {
		return defaultInstance ;
	}
}

