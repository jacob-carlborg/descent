package dtool.dom.ast;

import descent.core.domX.IASTVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Definition;
import dtool.dom.definitions.Module;
import dtool.dom.definitions.Symbol;
import dtool.dom.references.EntIdentifier;
import dtool.dom.references.EntQualified;
import dtool.dom.references.EntTemplateInstance;
import dtool.dom.references.Entity;
import dtool.dom.references.EntitySingle;

public interface IASTNeoVisitor extends IASTVisitor {

	boolean visit(ASTNeoNode elem);

	boolean visit(DefUnit elem);

	boolean visit(Symbol elem);

	boolean visit(Entity elem);

	boolean visit(EntQualified elem);

	boolean visit(EntitySingle elem);

	boolean visit(EntIdentifier elem);

	boolean visit(EntTemplateInstance elem);

	/* ---------------------------------- */
	boolean visit(Definition elem);

	/* ---------------------------------- */
	boolean visit(Module elem);

}