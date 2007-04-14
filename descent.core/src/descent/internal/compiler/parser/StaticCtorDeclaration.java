package descent.internal.compiler.parser;

public class StaticCtorDeclaration extends FuncDeclaration {
	
	public StaticCtorDeclaration(Loc loc) {
		super(loc, new IdentifierExp(Loc.ZERO, Id.staticCtor), STC.STCstatic, null);
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
