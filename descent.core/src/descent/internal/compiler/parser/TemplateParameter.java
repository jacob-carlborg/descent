package descent.internal.compiler.parser;


// DMD 1.020
public abstract class TemplateParameter extends ASTDmdNode {

	public Loc loc;
	public IdentifierExp ident;
	public Declaration sparam;

	public TemplateParameter(Loc loc, IdentifierExp ident) {
		this.loc = loc;
		this.ident = ident;
		this.sparam = null;
	}

	public abstract void declareParameter(Scope sc, SemanticContext context);

	public abstract INode defaultArg(Scope sc, SemanticContext context);

	/**
	 * Create dummy argument based on parameter.
	 */
	public abstract INode dummyArg(SemanticContext context);

	public TemplateAliasParameter isTemplateAliasParameter() {
		return null;
	}

	public TemplateTupleParameter isTemplateTupleParameter() {
		return null;
	}

	public TemplateTypeParameter isTemplateTypeParameter() {
		return null;
	}

	public TemplateValueParameter isTemplateValueParameter() {
		return null;
	}

	/**
	 * Match actual argument against parameter.
	 */
	public abstract MATCH matchArg(Scope sc, Objects tiargs, int i,
			TemplateParameters parameters, Objects dedtypes,
			Declaration[] psparam, SemanticContext context);

	/**
	 * If TemplateParameter's match as far as overloading goes.
	 */
	public abstract int overloadMatch(TemplateParameter tp);

	public abstract void semantic(Scope sc, SemanticContext context);

	public abstract ASTDmdNode specialization();

	public abstract TemplateParameter syntaxCopy(SemanticContext context);

	public abstract void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context);

}
