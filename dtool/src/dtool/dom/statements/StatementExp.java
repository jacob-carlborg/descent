package dtool.dom.statements;

import util.tree.TreeVisitor;
import descent.internal.core.dom.ExpStatement;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.expressions.Expression;

public class StatementExp extends ASTNeoNode {
	
	public Expression exp;

	public StatementExp(ExpStatement element) {
		if(element.hasNoSourceRangeInfo())
			setSourceRange(element.exp);
		else
			setSourceRange(element);
		
		this.exp = Expression.convert(element.exp);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, exp);
		}
		visitor.endVisit(this);	 
	}

}