package descent.internal.compiler.parser;


/**
 * Encodes constant expressions and initializers into char arrays, and viceversa.
 */
public class ASTNodeEncoder {
	
	// TODO: don't use a parser: create a more compact and faster way to encode expressions
	
	private static Parser parser;
	private static Parser initParser(char[] source) {
		if (parser == null) {
			parser = new Parser(Parser.D2, source);
		} else {
			parser.reset(source, 0, source.length, false, false, false, false);
			parser.nextToken();
		}
		return parser;
	}
	
	public static char[] encodeExpression(Expression value) {
		if (value == null) {
			return null;
		}
		
		return value.toString().toCharArray();
	}
	
	public static Expression decodeExpression(char[] value) {
		if (value == null || value.length == 0) {
			return null;
		}
		return initParser(value).parseExpression();
	}
	
	public static char[] encodeInitializer(IInitializer init) {
		if (init == null) {
			return null;
		}
		
		return init.toString().toCharArray();
	}
	
	public static Initializer decodeInitializer(char[] value) {
		if (value == null || value.length == 0) {
			return null;
		}

		return initParser(value).parseInitializer();
	}

}
