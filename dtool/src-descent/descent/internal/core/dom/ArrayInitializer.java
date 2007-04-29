package descent.internal.core.dom;

import java.util.ArrayList;
import java.util.List;

import util.tree.TreeVisitor;
import descent.core.dom.IInitializer;
import descent.core.domX.IASTVisitor;

public class ArrayInitializer extends Initializer {
	
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
	
	public Expression[] getLengths() {
		return exps.toArray(new Expression[exps.size()]);
	}
	
	public IInitializer[] getValues() {
		return values.toArray(new IInitializer[values.size()]);
	}
	
	public int getElementType() {
		return ElementTypes.ARRAY_INITIALIZER;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) { 
			TreeVisitor.acceptChildren(visitor, exps);
			TreeVisitor.acceptChildren(visitor, values);
		}
		visitor.endVisit(this);
	}

}
