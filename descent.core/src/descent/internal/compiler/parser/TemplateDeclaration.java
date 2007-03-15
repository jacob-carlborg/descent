package descent.internal.compiler.parser;

import java.util.List;

public class TemplateDeclaration extends ScopeDsymbol {
	
	// Wether this template declaration is just a wrapper for "class B(T) ..."
	public boolean wrapper; 
	public List<TemplateParameter> parameters;
	
	public TemplateDeclaration(IdentifierExp id, List<TemplateParameter> parameters, List<Dsymbol> decldefs) {
		super(id);
		this.parameters = parameters;
		this.members = decldefs;
	}
	
	@Override
	public int kind() {
		return TEMPLATE_DECLARATION;
	}

}
