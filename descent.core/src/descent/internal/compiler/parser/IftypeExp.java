package descent.internal.compiler.parser;

public class IftypeExp extends Expression {

	public Type targ;
	public IdentifierExp ident;
	public TOK tok;
	public Type tspec;
	public TOK tok2;

	public IftypeExp(Loc loc, Type targ, IdentifierExp ident, TOK tok, Type tspec, TOK tok2) {
		super(loc, TOK.TOKis);
		this.targ = targ;
		this.ident = ident;
		this.tok = tok;
		this.tspec = tspec;
		this.tok2 = tok2;		
	}
	
	@Override
	public int getNodeType() {
		return IFTYPE_EXP;
	}

}
