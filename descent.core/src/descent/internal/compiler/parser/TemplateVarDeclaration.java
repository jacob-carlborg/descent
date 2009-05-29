package descent.internal.compiler.parser;

public class TemplateVarDeclaration extends VarDeclaration {

	public TemplateVarDeclaration(Loc loc, Type type, char[] ident, Initializer init) {
		super(loc, type, ident, init);
	}

	public TemplateVarDeclaration(Loc loc, Type type, IdentifierExp id, Initializer init) {
		super(loc, type, id, init);
	}
	
	@Override
	public boolean isTemplateArgument() {
		return true;
	}

}
