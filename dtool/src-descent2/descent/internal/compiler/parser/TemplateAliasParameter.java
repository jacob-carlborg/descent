package descent.internal.compiler.parser;

import descent.core.compiler.IProblem;

public class TemplateAliasParameter extends TemplateParameter {
	
	public Type specAliasT;
	public Type defaultAlias;
	public Dsymbol specAlias;

	public TemplateAliasParameter(Loc loc, IdentifierExp ident, Type specAliasT, Type defaultAlias) {
		super(loc, ident);
		this.specAliasT = specAliasT;
	    this.defaultAlias = defaultAlias;

	    this.specAlias = null;
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		TypeIdentifier ti = new TypeIdentifier(loc, ident);
		Declaration sparam = new AliasDeclaration(loc, ident, ti);
		if (sc.insert(sparam) == null) {
			context.acceptProblem(Problem.newSemanticTypeError("Duplicate parameter " + ident, IProblem.DuplicatedParameter, 0, ident.start, ident.length));
		}

		if (specAliasT != null) {
			specAlias = specAliasT.toDsymbol(sc, context);
			if (specAlias == null) {
				context.acceptProblem(Problem.newSemanticTypeError("Symbol " + specAliasT.toString() + " not found", IProblem.SymbolNotFound, 0, specAliasT.start, specAliasT.length));
			}
		}
	}
	
	@Override
	public int getNodeType() {
		return TEMPLATE_ALIAS_PARAMETER;
	}

}
