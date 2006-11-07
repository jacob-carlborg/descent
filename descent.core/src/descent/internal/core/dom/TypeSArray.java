package descent.internal.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.dom.IDElementVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IStaticArrayType;

public class TypeSArray extends TypeArray implements IStaticArrayType {
	
	public Expression dim;

	public TypeSArray(Type t, Expression dim) {
		super(TY.Tsarray, t);
		this.dim = dim;
	}
	
	public int getArrayTypeType() {
		return STATIC_ARRAY;
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
	    	e = new ArrayExp(dim.loc, e, arguments);
	    }
	    return e;
	}
	
	@Override
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, dim);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public String toString() {
		return next.toString() + "[" + dim.toString() + "]";
	}

}
