package descent.ui.text.worddetectors;

public class CharacterUtil {
	
	public static boolean isSymbol(char c) {
		return 
			c == '/' || 
			c == '.' || 
			c == '&' || 
			c == '|' || 
			c == '-' || 
			c == '+' ||
			c == '=' ||
			c == '<' ||
			c == '>' ||
			c == '!' ||
			c == '?' ||
			c == '~' ||
			c == '[' ||
			c == ']' ||
			c == '{' ||
			c == '}' ||
			c == ',' ||
			c == ';' ||
			c == ':' ||
			c == '$' ||
			c == '=' ||
			c == '*' ||
			c == '%' ||
			c == '^' ||
			c == '(' ||
			c == ')';
	}

}
