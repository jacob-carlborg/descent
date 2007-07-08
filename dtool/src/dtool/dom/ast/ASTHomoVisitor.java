package dtool.dom.ast;

import util.tree.ITreeNode;
import util.tree.IVisitable;

/** 
 * An abstract visitor that visits nodes in a homogeneous way, 
 * i.e., without any type-specific methods. Uses the accept0 mechanism and
 * not getChildren().
 */
public abstract class ASTHomoVisitor extends ASTNeoUpTreeVisitor {

	public <T extends IASTNeoVisitor> void traverse(IVisitable<? super IASTNeoVisitor> elem) {
		elem.accept(this);
	}
	
	abstract boolean enterNode(ITreeNode elem);
	abstract void leaveNode(ITreeNode elem);
	
	public final void preVisit(ASTNode elem) {
	}

	public final void postVisit(ASTNode elem) {
	}
	
	public final boolean visit(ASTNode elem) {
		return enterNode(elem);
	}

	public final void endVisit(ASTNode elem) {
		leaveNode(elem);
	}
}