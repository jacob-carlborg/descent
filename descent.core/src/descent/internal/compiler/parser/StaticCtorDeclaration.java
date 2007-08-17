package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class StaticCtorDeclaration extends FuncDeclaration {
	
	public StaticCtorDeclaration(Loc loc) {
		super(loc, new IdentifierExp(Loc.ZERO, Id.staticCtor), STC.STCstatic, null);
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, modifiers);
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, ident);
			// Template args?
			TreeVisitor.acceptChildren(visitor, sourceFrequire);
			TreeVisitor.acceptChildren(visitor, sourceFbody);
			TreeVisitor.acceptChildren(visitor, outId);
			TreeVisitor.acceptChildren(visitor, sourceFensure);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public StaticCtorDeclaration isStaticCtorDeclaration() {
		return this;
	}
	
	@Override
	public AggregateDeclaration isThis() {
		return null;
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
	    type = new TypeFunction(null, Type.tvoid, 0, LINK.LINKd);

		super.semantic(sc, context);

		// We're going to need ModuleInfo
		Module m = getModule();
		if (m == null) {
			m = sc.module;
		}
		if (m != null) {
			m.needmoduleinfo = true;
		}
	}
	
	@Override
	public boolean addPreInvariant(SemanticContext context) {
		return false;
	}
	
	@Override
	public boolean addPostInvariant(SemanticContext context) {
		return false;
	}

	@Override
	public int getNodeType() {
		return STATIC_CTOR_DECLARATION;
	}

}
