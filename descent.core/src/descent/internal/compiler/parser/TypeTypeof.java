package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class TypeTypeof extends TypeQualified {
	
	public Expression exp;
	public int typeofStart;
	public int typeofLength;

	public TypeTypeof(Loc loc, Expression exp) {
		super(loc, TY.Ttypeof);
		this.exp = exp;		
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
		}
		visitor.endVisit(this);
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
