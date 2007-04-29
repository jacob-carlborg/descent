package descent.internal.compiler.parser;

public class TypeInfoStaticArrayDeclaration extends TypeInfoDeclaration {

	public TypeInfoStaticArrayDeclaration(Loc loc, Type tinfo, SemanticContext context) {
		super(loc, tinfo, 0, context);
	}

}
