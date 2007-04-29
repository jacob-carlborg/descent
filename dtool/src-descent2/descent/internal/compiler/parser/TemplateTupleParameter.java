package descent.internal.compiler.parser;

import descent.core.compiler.IProblem;

public class TemplateTupleParameter extends TemplateParameter {
	
	public TemplateTupleParameter(Loc loc, IdentifierExp ident) {
		super(loc, ident);
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		TypeIdentifier ti = new TypeIdentifier(loc, ident);
	    Declaration sparam = new AliasDeclaration(loc, ident, ti);
	    if (sc.insert(sparam) == null) {
	    	context.acceptProblem(Problem.newSemanticTypeError("Duplicate parameter " + ident, IProblem.DuplicatedParameter, 0, ident.start, ident.length));
	    }
	}
	
	@Override
	public int getNodeType() {
		return TEMPLATE_TUPLE_PARAMETER;
	}

}
