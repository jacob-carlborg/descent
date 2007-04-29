package descent.internal.compiler.parser;

public class TypeTypeof extends TypeQualified {
	
	public Expression exp;
	public int typeofStart;
	public int typeofLength;

	public TypeTypeof(Loc loc, Expression exp) {
		super(loc, TY.Ttypeof);
		this.exp = exp;		
	}
	
	@Override
	public int getNodeType() {
		return TYPE_TYPEOF;
	}
	
	public void setTypeofSourceRange(int start, int length) {
		this.typeofStart = start;
		this.typeofLength = length;
	}

}
