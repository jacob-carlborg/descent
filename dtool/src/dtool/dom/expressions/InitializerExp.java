package dtool.dom.expressions;

import util.tree.TreeVisitor;
import descent.internal.core.dom.ExpInitializer;
import dtool.dom.ast.IASTNeoVisitor;

public class InitializerExp extends Initializer {
	
	public Expression exp;

	public InitializerExp(ExpInitializer element) {
		setSourceRange(element);
		this.exp = Expression.convert(element.e); 
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
		}
		visitor.endVisit(this);	 
	}

}
