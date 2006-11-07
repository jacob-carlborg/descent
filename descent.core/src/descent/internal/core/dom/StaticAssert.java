package descent.internal.core.dom;

import descent.core.dom.IDElementVisitor;

public class StaticAssert extends Dsymbol {
	
	public Expression exp;
	public Expression msg;

	public StaticAssert(Loc loc, Expression exp, Expression msg) {
		this.exp = exp;
		this.msg = msg;
	}

	public int getElementType() {
//		 TODO Auto-generated method stub
		return -1;
	}
	
	public void accept(IDElementVisitor visitor) {
		// TODO Auto-generated method stub
		
	}

}
