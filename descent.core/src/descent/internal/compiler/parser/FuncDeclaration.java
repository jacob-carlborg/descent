package descent.internal.compiler.parser;

import java.util.List;

public class FuncDeclaration extends Declaration {
	
	public Statement fensure;
	public Statement frequire;
	public Statement fbody;
	public IdentifierExp outId;
	public List<Argument> parameters;
	public List<TemplateParameter> templateParameters;
	
	public FuncDeclaration() {
	}
	
	public FuncDeclaration(IdentifierExp ident, Type type) {
		super(ident);
		this.type = type;
	}
	
	@Override
	public int kind() {
		return FUNC_DECLARATION;
	}

}
