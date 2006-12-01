package descent.core.domX;

import descent.core.dom.IDeclaration;
import descent.core.dom.IElement;

public abstract class AbstractElement extends ASTNode {
	
	public final static IElement[] NO_ELEMENTS = new IElement[0];
	public final static IDeclaration[] NO_DECLARATIONS = new IDeclaration[0];
	
	public String comments;
	public int modifiers;
	
	public int getStartPos() {
		return startPos;
	}
	
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
	
}
