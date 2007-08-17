package dtool.descentadapter;

import melnorme.miscutil.Assert;
import descent.core.domX.ASTNode;
import descent.core.domX.IASTVisitor;
import dtool.dom.ast.ASTNeoNode;

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

	
	/* ---- common adaptors ---- */
	
	protected void rangeAdapt(ASTNeoNode newelem, ASTNode elem) {
		newelem.start = elem.getStartPos();
		newelem.length = elem.getLength();
	}

	protected boolean endAdapt(ASTNeoNode newelem) {
		ret = newelem;
		return false;
	}

	protected boolean assertFailFAKENODE() {
		Assert.fail("Fake Node"); return false;
	}
	protected boolean assertFailABSTRACT_NODE() {
		Assert.fail("Abstract Node"); return false;
	}
	protected boolean assertFailHandledDirectly() {
		Assert.fail("This class is not converted directly by the visitor. ");
		return true;
	}

}