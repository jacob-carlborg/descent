package dtool.descentadapter;

import melnorme.miscutil.Assert;
import descent.internal.compiler.parser.ast.ASTNode;
import descent.internal.compiler.parser.ast.IASTVisitor;
import dtool.ast.ASTNeoNode;

/**
 * This class is a mixin. 
 * Do not use it, instead use it's subclass: {@link DeclarationConverter}
 */
public abstract class ASTCommonConverter implements IASTVisitor {
	
	ASTNeoNode ret = null;
	
	ASTNeoNode convert(ASTNode elem) {
		elem.accept(this);
		return ret;
	}
	
	public void postVisit(ASTNode elem) {
	}

	public void preVisit(ASTNode elem) {
	}

	
	/* ---- common adaptors ---- */
	
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