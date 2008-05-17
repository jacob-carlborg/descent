package descent.internal.compiler.parser;

public class TemplateThisParameter extends TemplateTypeParameter {

	public TemplateThisParameter(Loc loc, IdentifierExp ident, Type specType,
			Type defaultType) {
		super(loc, ident, specType, defaultType);
	}

	@Override
	public TemplateThisParameter isTemplateThisParameter() {
		return this;
	}

	@Override
	public TemplateParameter syntaxCopy(SemanticContext context) {
		TemplateThisParameter tp = new TemplateThisParameter(loc, ident,
				specType, defaultType);
		if (tp.specType != null)
			tp.specType = specType.syntaxCopy(context);
		if (defaultType != null)
			tp.defaultType = defaultType.syntaxCopy(context);
		tp.copySourceRange(this);
		return tp;
	}
	
	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
	    buf.writestring("this ");
	    super.toCBuffer(buf, hgs, context);
	}

}
