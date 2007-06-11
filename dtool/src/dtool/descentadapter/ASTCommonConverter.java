package dtool.descentadapter;

import descent.core.dom.IDescentElement;
import descent.core.domX.IASTVisitor;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.ASTNode;

/**
 * This class is a mixin. 
 * Do not use it, instead use it's subclass: {@link DeclarationConverter}
 */
public abstract class ASTCommonConverter implements IASTVisitor {
	
	ASTNode ret = null;
	
	ASTNode convert(ASTNode elem) {
		elem.accept(this);
		return ret;
	}
	
	public void postVisit(ASTNode elem) {
	}

	public void preVisit(ASTNode elem) {
	}

	public void endVisit(IDescentElement elem) {
	}
	
	
	/* ---- common adaptors ---- */
	
	protected void rangeAdapt(ASTNeoNode newelem, ASTNode elem) {
		newelem.startPos = elem.getStartPos();
		newelem.length = elem.getLength();
	}

	protected boolean endAdapt(ASTNeoNode newelem) {
		ret = newelem;
		return false;
	}


	public boolean visit(ASTNode elem) {
		ret = elem;
		return false;	
	}

}