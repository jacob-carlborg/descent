package descent.internal.compiler.parser.ast;

import melnorme.miscutil.tree.IVisitable;

/** 
 * An abstract visitor that visits nodes in a homogeneous way, 
 * i.e., without any type-specific methods. Uses the accept0 mechanism and
 * not getChildren().
 */
public abstract class ASTHomoVisitor extends ASTUpTreeVisitor {

	public <T extends IASTVisitor> void traverse(IVisitable<? super IASTVisitor> elem) {
		elem.accept(this);
	}
	
	public void preVisit(ASTNode elem) {
	}

	public void postVisit(ASTNode elem) {
	}
	
	abstract boolean enterNode(ASTNode elem);
	abstract void leaveNode(ASTNode elem);


	public final boolean visit(ASTNode elem) {
		return enterNode(elem);
	}

	public final void endVisit(ASTNode elem) {
		leaveNode(elem);
	}

}