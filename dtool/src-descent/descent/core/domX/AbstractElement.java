package descent.core.domX;

import descent.core.dom.IDeclaration;
import dtool.dom.ast.ASTNode;

/**
 * The base AST node for DMD's AST 
 * It's "neo AST" homologue is ASTElement
 */
public abstract class AbstractElement extends ASTNode {
	
	public final static AbstractElement[] NO_ELEMENTS = new AbstractElement[0];
	public final static IDeclaration[] NO_DECLARATIONS = new IDeclaration[0];
	
	public String comments;
	public int modifiers;
	
	public int getModifiers() {
		return modifiers;
	}
	
	public void addComment(String string, int blockCommentStart) {
		comments = string;
		if (blockCommentStart != -1) {
			this.length += this.startPos - blockCommentStart; 
			this.startPos = blockCommentStart;
		}
	}
	
	public String getComments() {
		return comments;
	}
	
	/**
	 * Returns one of this interface constants, telling to
	 * which class one can cast safely.
	 */
	public abstract int getElementType();
}
