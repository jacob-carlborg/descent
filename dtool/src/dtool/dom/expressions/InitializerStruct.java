package dtool.dom.expressions;

import util.tree.TreeVisitor;
import descent.internal.core.dom.StructInitializer;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.Symbol;

public class InitializerStruct extends Initializer {

	public Symbol[] indexes;
	public Initializer[] values;

	public InitializerStruct(StructInitializer elem) {
		convertNode(elem);
		//TODO
		//this.indexes = Expression.convertMany(elem.exps);
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
