package descent.internal.compiler.parser;

/**
 * Encodes ASTNodes into char arrays and viceversa.
 * 
 * <p>This class is not thread safe.</p>
 */
public class ASTNodeEncoder {
	
	// TODO don't use a parser when possible, optimize for typical cases
	// like numbers.
	// TODO don't use toString() if the node is simple.
	
	private Parser parser;
	private Parser initParser(char[] source) {
		if (parser == null) {
			parser = new Parser(Parser.DEFAULT_LEVEL, source);
		} else {
			parser.reset(source, 0, source.length, false, false, false, false);
		}
		parser.nextToken();
		return parser;
	}
	
	public ASTNodeEncoder() {
		
	}
	
	public char[] encodeExpression(Expression value) {
		if (value == null) {
			return null;
		}
		
		return value.toString().toCharArray();
	}
	
	public Expression decodeExpression(char[] value) {
		if (value == null || value.length == 0) {
			return null;
		}
		return initParser(value).parseExpression();
	}
	
	public char[] encodeInitializer(IInitializer init) {
		if (init == null) {
			return null;
		}
		
		return init.toString().toCharArray();
	}
	
	public Initializer decodeInitializer(char[] value) {
		if (value == null || value.length == 0) {
			return null;
		}

		return initParser(value).parseInitializer();
	}

}
