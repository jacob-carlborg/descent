package descent.internal.compiler.parser;


public class TemplateThisParameter extends TemplateTypeParameter {

	public TemplateThisParameter(Loc loc, IdentifierExp ident, Type specType, Type defaultType) {
		super(loc, ident, specType, defaultType);
	}

}
