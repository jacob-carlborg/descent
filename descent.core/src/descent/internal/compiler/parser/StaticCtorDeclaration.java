package descent.internal.compiler.parser;

public class StaticCtorDeclaration extends FuncDeclaration {
	
	public StaticCtorDeclaration() {
		super(new IdentifierExp(Id.staticCtor), STC.STCstatic, null);
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
	    type = new TypeFunction(null, Type.tvoid, false, LINK.LINKd);

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
	public int getNodeType() {
		return STATIC_CTOR_DECLARATION;
	}

}
