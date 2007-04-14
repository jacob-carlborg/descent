package descent.internal.compiler.parser;

public class TypeInfoPointerDeclaration extends TypeInfoDeclaration {

	public TypeInfoPointerDeclaration(Loc loc, Type tinfo, SemanticContext context) {
		super(loc, tinfo, 0, context);
	}

}
