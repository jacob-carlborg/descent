package descent.internal.compiler.parser;

public class LabelDsymbol extends Dsymbol {
	
	public LabelStatement statement;
	
	public LabelDsymbol(IdentifierExp ident) {
		super(ident);
	}
	
	public LabelDsymbol(Identifier ident) {
		super(new IdentifierExp(ident));
	}

	@Override
	public int getNodeType() {
		return LABEL_DSYMBOL;
	}

}
