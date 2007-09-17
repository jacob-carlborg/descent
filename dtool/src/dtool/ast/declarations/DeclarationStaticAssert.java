package dtool.ast.declarations;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.StaticAssert;
import descent.internal.compiler.parser.StaticAssertStatement;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.Resolvable;
import dtool.ast.statements.IStatement;

public class DeclarationStaticAssert extends ASTNeoNode implements IStatement {

	public Resolvable pred;
	public Resolvable msg;
	
	public DeclarationStaticAssert(StaticAssert elem) {
		convertNode(elem);
		this.pred = Expression.convert(elem.exp);
		this.msg = Expression.convert(elem.msg);
	}
	
	public DeclarationStaticAssert(StaticAssertStatement elem) {
		this(elem.sa);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, pred);
			TreeVisitor.acceptChildren(visitor, msg);
		}
		visitor.endVisit(this);
	}

}
