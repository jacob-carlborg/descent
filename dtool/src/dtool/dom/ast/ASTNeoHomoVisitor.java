package dtool.dom.ast;

import melnorme.miscutil.tree.IVisitable;
import descent.internal.compiler.parser.ast.ASTNode;

/** 
 * An abstract visitor that visits nodes in a homogeneous way, 
 * i.e., without any type-specific methods. Uses the accept0 mechanism and
 * not getChildren().
 */
public abstract class ASTNeoHomoVisitor extends ASTNeoUpTreeVisitor {

	public <T extends IASTNeoVisitor> void traverse(IVisitable<? super IASTNeoVisitor> elem) {
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