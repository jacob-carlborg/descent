package dtool.dom.expressions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.CompileDeclaration;
import descent.internal.compiler.parser.CompileExp;
import dtool.dom.ast.IASTNeoVisitor;

public class ExpStringMacro extends Expression {

	public final Expression exp;

	public ExpStringMacro(CompileExp node) {
		convertNode(node);
		this.exp = Expression.convert(node.e1);
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
