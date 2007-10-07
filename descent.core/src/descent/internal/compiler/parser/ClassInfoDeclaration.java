package descent.internal.compiler.parser;

// DMD 1.020
public class ClassInfoDeclaration extends VarDeclaration {

	public ClassDeclaration cd;

	public ClassInfoDeclaration(ClassDeclaration cd, SemanticContext context) {
		this(Loc.ZERO, cd, context);
	}

	public ClassInfoDeclaration(Loc loc, ClassDeclaration cd,
			SemanticContext context) {
		super(loc, context.ClassDeclaration_classinfo.type, cd.ident, null);
		this.cd = cd;
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		// empty
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s) {
		throw new IllegalStateException("assert(0);");
	}

}
