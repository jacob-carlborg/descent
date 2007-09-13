package dtool.dom.statements;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ForeachStatement;
import descent.internal.compiler.parser.TOK;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.IFunctionParameter;
import dtool.dom.expressions.Expression;
import dtool.dom.expressions.Resolvable;

public class StatementForeach extends Statement {

	public boolean reverse;
	public IFunctionParameter[] params;
	public Resolvable iterable;
	public IStatement body;

	public StatementForeach(ForeachStatement elem) {
		convertNode(elem);
		// TODO: foreach parameters, unitest too.
		//this.params = new IFunctionParameter[elem.arguments.size()]; 
		//DescentASTConverter.convertMany(elem.arguments.toArray(), this.params);
		this.iterable = Expression.convert(elem.sourceAggr);
		this.body = Statement.convert(elem.body);
		this.reverse = elem.op == TOK.TOKforeach_reverse;
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
