package descent.internal.compiler.parser;

public class StaticDtorDeclaration extends FuncDeclaration {
	
	public StaticDtorDeclaration(Loc loc) {
		super(loc, new IdentifierExp(Loc.ZERO, Id.staticDtor), STC.STCstatic, null);
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
		return STATIC_DTOR_DECLARATION;
	}
	
}