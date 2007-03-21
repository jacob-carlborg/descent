package descent.internal.compiler.parser;

import descent.core.compiler.IProblem;

public class TemplateTypeParameter extends TemplateParameter {
	
	public Type specType;
	public Type defaultType;
	
	public TemplateTypeParameter(IdentifierExp ident, Type specType, Type defaultType) {
		super(ident);
		this.specType = specType;
	    this.defaultType = defaultType;
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		TypeIdentifier ti = new TypeIdentifier(ident);
		Declaration sparam = new AliasDeclaration(ident, ti);
		if (sc.insert(sparam) == null) {
			context.acceptProblem(Problem.newSemanticTypeError("Duplicate parameter " + ident, IProblem.DuplicatedParameter, 0, ident.start, ident.length));
		}

		if (specType != null) {
			specType = specType.semantic(sc, context);
		}
	}
	
	@Override
	public int getNodeType() {
		return TEMPLATE_TYPE_PARAMETER;
	}

}
