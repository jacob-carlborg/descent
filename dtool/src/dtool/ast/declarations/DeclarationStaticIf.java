package dtool.ast.declarations;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.StaticIfCondition;
import descent.internal.compiler.parser.ast.ASTNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.Resolvable;

public class DeclarationStaticIf extends DeclarationConditional {
	
	public Resolvable exp;

	public DeclarationStaticIf(ASTNode elem,
			StaticIfCondition condition, NodeList thendecls, NodeList elsedecls) {
		convertNode(elem);
		this.exp = Expression.convert(condition.exp);
		this.thendecls = thendecls; 
		this.elsedecls = elsedecls;
	}


	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
			TreeVisitor.acceptChildren(visitor, thendecls.nodes);
			TreeVisitor.acceptChildren(visitor, elsedecls.nodes);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public String toStringAsElement() {
		return "[static if("+"..."+")]";
	}

}
