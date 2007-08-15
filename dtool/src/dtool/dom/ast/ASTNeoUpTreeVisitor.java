package dtool.dom.ast;

import melnorme.miscutil.Assert;
import descent.core.domX.ASTNode;
import descent.core.domX.ASTUpTreeVisitor;
import dtool.dom.declarations.DeclarationImport;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Definition;
import dtool.dom.definitions.Module;
import dtool.dom.definitions.Symbol;
import dtool.dom.references.CommonRefNative;
import dtool.dom.references.CommonRefQualified;
import dtool.dom.references.CommonRefSingle;
import dtool.dom.references.RefIdentifier;
import dtool.dom.references.RefTemplateInstance;
import dtool.dom.references.Reference;

public abstract class ASTNeoUpTreeVisitor extends ASTUpTreeVisitor implements IASTNeoVisitor {

	
	public boolean visit(ASTNeoNode elem) {
		Assert.isTrue(ASTNeoNode.class.getSuperclass().equals(ASTNode.class));
		return visit((ASTNode) elem);
		// return visitAsSuperType(elem, ASTNeoNode.class);
	}

	public boolean visit(DefUnit elem) {
		Assert.isTrue(DefUnit.class.getSuperclass().equals(ASTNeoNode.class));
		return visit((ASTNeoNode) elem);
		//return visitAsSuperType(elem, DefUnit.class);
	}

	public boolean visit(Symbol elem) {
		Assert.isTrue(Symbol.class.getSuperclass().equals(ASTNeoNode.class));
		return visit((ASTNeoNode) elem);
	}
	

	public boolean visit(Reference elem) {
		Assert.isTrue(Reference.class.getSuperclass().equals(ASTNeoNode.class));
		return visit((ASTNeoNode) elem);
	}
	
	public boolean visit(CommonRefNative elem) {
		Assert.isTrue(CommonRefNative.class.getSuperclass().equals(Reference.class));
		return visit((Reference) elem);
	}
	

	public boolean visit(CommonRefQualified elem) {
		Assert.isTrue(CommonRefQualified.class.getSuperclass().equals(Reference.class));
		return visit((Reference) elem);
	}

	public boolean visit(CommonRefSingle elem) {
		Assert.isTrue(CommonRefSingle.class.getSuperclass().equals(Reference.class));
		return visit((Reference) elem);
	}

	public boolean visit(RefIdentifier elem) {
		Assert.isTrue(RefIdentifier.class.getSuperclass().equals(CommonRefSingle.class));
		return visit((CommonRefSingle) elem);
	}

	public boolean visit(RefTemplateInstance elem) {
		Assert.isTrue(RefTemplateInstance.class.getSuperclass().equals(CommonRefSingle.class));
		return visit((CommonRefSingle) elem);
	}

	/* ---------------------------------- */

	public boolean visit(Definition elem) {
		Assert.isTrue(Definition.class.getSuperclass().equals(DefUnit.class));
		return visit((DefUnit) elem);
	}
	
	public boolean visit(Module elem) {
		Assert.isTrue(Module.class.getSuperclass().equals(DefUnit.class));
		return visit((DefUnit) elem);
	}

	/* ---------------------------------- */

	public boolean visit(DeclarationImport elem) {
		Assert.isTrue(DeclarationImport.class.getSuperclass().equals(ASTNeoNode.class));
		return visit((ASTNeoNode) elem);
	}

	
	
	/* ============================================= */

}
