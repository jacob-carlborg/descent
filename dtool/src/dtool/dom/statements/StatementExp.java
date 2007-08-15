package dtool.dom.statements;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ExpStatement;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.expressions.Expression;

public class StatementExp extends Statement {
	
	public Expression exp;

	public StatementExp(ExpStatement element) {
		if(element.hasNoSourceRangeInfo() && element.exp != null)
			setSourceRange(element.exp);
		else
			convertNode(element);
		
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
