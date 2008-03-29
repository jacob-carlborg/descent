package descent.internal.compiler.parser;


/**
 * Encodes ASTNodes into char arrays and viceversa.
 * 
 * <p>This class is not thread safe.</p>
 */
public class ASTNodeEncoder {
	
//	private final static char INTEGER_EXP = '=';
//	private final static char IDENTIFIER_EXP = '?';
	
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
		
		// Optimize for IntegerExp and IdentifierExp, which are the most common cases
//		if (value instanceof IntegerExp) {
//			StringBuilder sb = new StringBuilder();
//			sb.append(INTEGER_EXP);
//			sb.append(((IntegerExp) value).value.toString());
//			return sb.toString().toCharArray();
//		} else if (value instanceof IdentifierExp) {
//			StringBuilder sb = new StringBuilder();
//			sb.append(IDENTIFIER_EXP);
//			sb.append(((IdentifierExp) value).ident);
//			return sb.toString().toCharArray();
//		} else {
			return value.toString().toCharArray();
//		}
	}
	
	public Expression decodeExpression(char[] value) {
		if (value == null || value.length == 0) {
			return null;
		}
		
		// Optimize for IntegerExp and IdentifierExp, which are the most common cases
//		if (value[0] == INTEGER_EXP) {
//			return new IntegerExp(Loc.ZERO, new integer_t(new BigInteger(new String(value, 1, value.length - 1))));
//		} else if (value[0] == IDENTIFIER_EXP) {
//			return new IdentifierExp(CharOperation.subarray(value, 1, value.length));
//		}
		
		return initParser(value).parseExpression();
	}
	
	public char[] encodeInitializer(Initializer init) {
		if (init == null) {
			return null;
		}
		
		if (init instanceof ExpInitializer) {
			return encodeExpression(((ExpInitializer) init).exp);
		}
		
		return init.toString().toCharArray();
	}
	
	public Initializer decodeInitializer(char[] value) {
		if (value == null || value.length == 0) {
			return null;
		}
		
		// Optimize for IntegerExp and IdentifierExp, which are the most common cases
//		if (value[0] == INTEGER_EXP || value[0] == IDENTIFIER_EXP) {
//			return new ExpInitializer(Loc.ZERO, decodeExpression(value));
//		}

		return initParser(value).parseInitializer();
	}

}
