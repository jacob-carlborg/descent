package descent.internal.codeassist;

import static descent.internal.compiler.parser.TOK.TOKblockcomment;
import static descent.internal.compiler.parser.TOK.TOKdocblockcomment;
import static descent.internal.compiler.parser.TOK.TOKdoclinecomment;
import static descent.internal.compiler.parser.TOK.TOKdocpluscomment;
import static descent.internal.compiler.parser.TOK.TOKlinecomment;
import static descent.internal.compiler.parser.TOK.TOKpluscomment;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.Parser;
import descent.internal.compiler.parser.TOK;
import descent.internal.compiler.parser.Token;
import descent.internal.compiler.parser.TypeFunction;

public class SelectionParser extends Parser {
	
	/**
	 * If the selection is inside a comment token, this holds it.
	 */
	Token commentToken;
	
	int selectionOffset;
	int selectionLength;

	public SelectionParser(int apiLevel,char[] source, char[] filename) {
		super(source, 0, source.length,
				true, false, false, false,
				apiLevel,
				null, null, false,
				filename);
		this.diet = true;
	}
	
	/*
	 * Do diet parsing, and return false if the selection location
	 * falls in a function.
	 */
	@Override
	protected boolean dietParse(FuncDeclaration f) {
		
		// If any argument is being selected, don't skip
		// Same for return type
		TypeFunction type = (TypeFunction) f.type;
		if (type != null && type.parameters != null) {
			for(Argument arg : type.parameters) {
				if (intersectsSelection(arg)) {
					return false;
				}
			}
			if (type.next != null && intersectsSelection(type.next)) {
				return false;
			}
		}
		
		int before = token.ptr + token.sourceLen;
		
		boolean ret = super.dietParse(f);
		
		int after = token.ptr + token.sourceLen;
		
		//       |---------|
		//     before     after
		//
		//
		//    |------------------|
		//  offset            offset + length
		//
		// If any of these points fall between the other two in the top or bottom,
		// there is an intersection.
		
		if (intersectsSelection(before, after)) {
			return false;
		}
		
		return ret;
	}
	
	private boolean intersectsSelection(ASTDmdNode node) {
		return intersectsSelection(node.start, node.start + node.length);
	}
	
	private boolean intersectsSelection(int start, int end) {
		return intersects(start, end, selectionOffset, selectionOffset + selectionLength);
	}
	
	private boolean intersects(int start1, int end1, int start2, int end2) {
		return (isBetween(start1, start2, end1) ||
			isBetween(start1, end2, end2) ||
			isBetween(start2, start1, end2) ||
			isBetween(start2, end1, end2));
	}
	
	private boolean isBetween(int min, int val, int max) {
		return min <= val && val <= max;
	}
	
	@Override
	public TOK nextToken() {
		TOK tok = Lexer_nextToken();

		while ((tok == TOKlinecomment || tok == TOKdoclinecomment
				|| tok == TOKblockcomment
				|| tok == TOKdocblockcomment
				|| tok == TOKpluscomment || tok == TOKdocpluscomment)) {
			if (token.ptr <= selectionOffset
					&& selectionOffset <= token.ptr + token.sourceLen) {
				this.commentToken = new Token(token);
			}

			tok = Lexer_nextToken();
		}

		return tok;
	}

}
