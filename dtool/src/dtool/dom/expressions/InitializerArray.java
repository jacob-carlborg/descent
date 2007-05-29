package dtool.dom.expressions;

import util.tree.TreeVisitor;
import descent.internal.core.dom.ArrayInitializer;
import dtool.dom.ast.IASTNeoVisitor;

public class InitializerArray extends Initializer {

	public Expression[] indexes;
	public Initializer[] values;

		
	public InitializerArray(ArrayInitializer elem) {
		convertNode(elem);
		this.indexes = Expression.convertMany(elem.exps); 
		this.values = Initializer.convertMany(elem.values);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, indexes);
			TreeVisitor.acceptChildren(visitor, values);
		}
		visitor.endVisit(this);
	}

}
