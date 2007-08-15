package dtool.dom.expressions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ExpInitializer;
import dtool.dom.ast.IASTNeoVisitor;

public class InitializerExp extends Initializer {
	
	public Expression exp;

	public InitializerExp(ExpInitializer elem) {
		convertNode(elem);
		this.exp = Expression.convert(elem.exp); 
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
