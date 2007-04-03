package descent.core.compiler;

import descent.internal.core.dom.TOK;
import descent.internal.core.dom.Token;

/**
 * Helper class for DMD's Token's
 */
public class DeeToken {


	public static boolean isComment(TOK tok) {
		return tok == TOK.TOKcomment;
	}

	public static boolean isDDocComment(String tokenstr, Token token) {
		return isComment(token.value) && 
		((tokenstr.startsWith("///") || tokenstr.startsWith("/**")));
	}

	public static boolean isSimpleComment(String tokenstr, Token token) {
		return isComment(token.value) && 
		((!tokenstr.startsWith("///") && !tokenstr.startsWith("/**")));
	}
	
}
