package descent.internal.compiler.parser;

public class UnitTestDeclaration extends FuncDeclaration {
	
	public UnitTestDeclaration() {
		super(new IdentifierExp(unitTestId()), STC.STCundefined, null);
	}
	
	private static int unitTestId;
	private static Identifier unitTestId() {
		return new Identifier("__unittest" + ++unitTestId, TOK.TOKidentifier);
	}
	
	@Override
	public UnitTestDeclaration isUnitTestDeclaration() {
		return this;
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		if (context.global.params.useUnitTests) {
			// Type tret;

			type = new TypeFunction(null, Type.tvoid, 0, LINK.LINKd);
			super.semantic(sc, context);
		}

		// We're going to need ModuleInfo even if the unit tests are not
		// compiled in, because other modules may import this module and refer
		// to this ModuleInfo.
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
		return UNIT_TEST_DECLARATION;
	}
	
}
