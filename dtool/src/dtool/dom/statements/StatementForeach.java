package dtool.dom.statements;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.core.dom.ForeachStatement;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.IFunctionParameter;
import dtool.dom.expressions.Expression;

public class StatementForeach extends Statement {

	public boolean reverse;
	public IFunctionParameter[] params;
	public Expression iterable;
	public IStatement body;

	public StatementForeach(ForeachStatement elem) {
		convertNode(elem);
		this.params = DescentASTConverter.convertMany(
				elem.arguments, new IFunctionParameter[elem.arguments.length]);
		this.iterable = Expression.convert(elem.aggr);
		this.body = Statement.convert(elem.body);
		this.reverse = elem.reverse;
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, params);
			TreeVisitor.acceptChildren(visitor, iterable);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}

}
