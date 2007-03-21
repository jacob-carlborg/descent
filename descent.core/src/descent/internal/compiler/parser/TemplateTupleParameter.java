package descent.internal.compiler.parser;

import descent.core.compiler.IProblem;

public class TemplateTupleParameter extends TemplateParameter {
	
	public TemplateTupleParameter(IdentifierExp ident) {
		super(ident);
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		TypeIdentifier ti = new TypeIdentifier(ident);
	    Declaration sparam = new AliasDeclaration(ident, ti);
	    if (sc.insert(sparam) == null) {
	    	context.acceptProblem(Problem.newSemanticTypeError("Duplicate parameter " + ident, IProblem.DuplicatedParameter, 0, ident.start, ident.length));
	    }
	}
	
	@Override
	public int getNodeType() {
		return TEMPLATE_TUPLE_PARAMETER;
	}

}
