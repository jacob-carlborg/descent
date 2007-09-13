package dtool.dom.statements;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ForeachRangeStatement;
import descent.internal.compiler.parser.TOK;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.IFunctionParameter;
import dtool.dom.expressions.Expression;
import dtool.dom.expressions.Resolvable;

public class StatementForeachRange extends Statement {

	public final boolean reverse;
	public final IFunctionParameter param;
	public final Resolvable lwr;
	public final Resolvable upr;
	public final IStatement body;

	public StatementForeachRange(ForeachRangeStatement elem) {
		convertNode(elem);
		this.param = (IFunctionParameter) DescentASTConverter.convertElem(elem.arg);
		this.lwr = Expression.convert(elem.lwr);
		this.upr = Expression.convert(elem.upr);
		this.body = Statement.convert(elem.body);
		this.reverse = elem.op == TOK.TOKforeach_reverse;
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, param);
			TreeVisitor.acceptChildren(visitor, lwr);
			TreeVisitor.acceptChildren(visitor, upr);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}

}

