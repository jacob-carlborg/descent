package dtool.dom.ast;

import descent.core.domX.IASTVisitor;
import dtool.dom.base.Entity;
import dtool.dom.base.EntitySingle;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Definition;
import dtool.dom.definitions.Module;

public interface IASTNeoVisitor extends IASTVisitor {

	boolean visit(ASTNeoNode elem);

	boolean visit(DefUnit elem);

	boolean visit(DefUnit.Symbol elem);

	boolean visit(Entity elem);

	boolean visit(Entity.QualifiedEnt elem);

	boolean visit(EntitySingle elem);

	boolean visit(EntitySingle.Identifier elem);

	boolean visit(EntitySingle.TemplateInstance elem);

	/* ---------------------------------- */
	boolean visit(Definition elem);

	/* ---------------------------------- */
	boolean visit(Module elem);

}