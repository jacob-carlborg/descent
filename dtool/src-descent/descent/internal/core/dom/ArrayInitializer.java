package descent.internal.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.dom.IArrayInitializer;
import descent.core.dom.IExpression;
import descent.core.dom.IInitializer;
import descent.core.domX.ASTVisitor;

public class ArrayInitializer extends Initializer implements IArrayInitializer {
	
	private List<Expression> exps;
	private List<Initializer> values;

	public ArrayInitializer() {
		this.exps = new ArrayList<Expression>();
		this.values = new ArrayList<Initializer>();
	}

	public void addInit(Expression e, Initializer value) {
		this.exps.add(e);
		this.values.add(value);
	}
	
	public IExpression[] getLengths() {
		return exps.toArray(new IExpression[exps.size()]);
	}
	
	public IInitializer[] getValues() {
		return values.toArray(new IInitializer[values.size()]);
	}
	
	public int getElementType() {
		return ElementTypes.ARRAY_INITIALIZER;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) { 
			acceptChildren(visitor, exps);
			acceptChildren(visitor, values);
		}
		visitor.endVisit(this);
	}

}
