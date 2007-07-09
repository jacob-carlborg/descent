package descent.internal.core.dom;

import java.util.ArrayList;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;


import descent.core.dom.IExpression;
import descent.core.dom.IType;
import descent.core.domX.IASTVisitor;

public class TypeSArray extends TypeArray implements IType {
	
	public Expression dim;

	public TypeSArray(Type t, Expression dim) {
		super(TY.Tsarray, t);
		this.dim = dim;
	}
	
	public int getElementType() {
		return ElementTypes.STATIC_ARRAY_TYPE;
	}
	
	public IExpression getDimension() {
		return dim;
	}
	
	@Override
	public Expression toExpression() {
		Expression e = next.toExpression();
	    if (e != null)
	    {	
	    	List<Expression> arguments = new ArrayList<Expression>();
	    	arguments.add(dim);
	    	e = new ArrayExp(e, arguments);
	    }
	    return e;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, dim);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public String toString() {
		return next.toString() + "[" + dim.toString() + "]";
	}

}
