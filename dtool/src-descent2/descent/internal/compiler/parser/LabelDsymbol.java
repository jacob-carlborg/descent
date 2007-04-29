package descent.internal.compiler.parser;

public class LabelDsymbol extends Dsymbol {
	
	public LabelStatement statement;
	
	public LabelDsymbol(Loc loc, IdentifierExp ident) {
		super(loc, ident);
	}
	
	public LabelDsymbol(Loc loc, Identifier ident) {
		super(loc, new IdentifierExp(Loc.ZERO, ident));
	}

	@Override
	public int getNodeType() {
		return LABEL_DSYMBOL;
	}

}
