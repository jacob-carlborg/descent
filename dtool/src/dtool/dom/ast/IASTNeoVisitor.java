package dtool.dom.ast;

import descent.core.domX.IASTVisitor;
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

public interface IASTNeoVisitor extends IASTVisitor {

	boolean visit(ASTNeoNode elem);

	boolean visit(DefUnit elem);

	boolean visit(Symbol elem);

	boolean visit(Reference elem);
	
	boolean visit(CommonRefNative elem);
	
	boolean visit(CommonRefQualified elem);
	
	boolean visit(CommonRefSingle elem);

	boolean visit(RefIdentifier elem);

	boolean visit(RefTemplateInstance elem);

	/* ---------------------------------- */
	boolean visit(Definition elem);
	boolean visit(Module elem);

	/* ---------------------------------- */
	boolean visit(DeclarationImport elem);

}