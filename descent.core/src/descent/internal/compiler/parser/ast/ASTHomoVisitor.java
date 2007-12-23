package descent.internal.compiler.parser.ast;

import melnorme.miscutil.tree.IVisitable;
import descent.internal.compiler.parser.INode;

/** 
 * An abstract visitor that visits nodes in a homogeneous way, 
 * i.e., without any type-specific methods. Uses the accept0 mechanism and
 * not getChildren().
 */
public abstract class ASTHomoVisitor extends ASTUpTreeVisitor {

	public <T extends IASTVisitor> void traverse(IVisitable<? super IASTVisitor> elem) {
		elem.accept(this);
	}
	
	public void preVisit(INode elem) {
	}

	public void postVisit(INode elem) {
	}
	
	abstract boolean enterNode(INode elem);
	abstract void leaveNode(INode elem);


	public final boolean visit(INode elem) {
		return enterNode(elem);
	}

	public final void endVisit(INode elem) {
		leaveNode(elem);
	}

}