package descent.internal.compiler.parser;

public class ThrowStatement extends Statement {

	public Expression exp;

	public ThrowStatement(Loc loc, Expression exp) {
		super(loc);
		this.exp = exp;		
	}
	
	@Override
	public int getNodeType() {
		return THROW_STATEMENT;
	}

}
