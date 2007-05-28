package dtool.dom.declarations;

import util.tree.TreeVisitor;
import descent.internal.core.dom.PragmaDeclaration;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.declarations.DefUnit.Symbol;
import dtool.dom.expressions.Expression;

public class DeclarationPragma extends ASTNeoNode {

	public Symbol ident;
	public Expression[] expressions;
	public Declaration[] decls;	// can be null?

	
	public DeclarationPragma(PragmaDeclaration elem) {
		convertNode(elem);
		ident = new Symbol(elem.ident);
		this.expressions = Expression.convertMany(elem.expressions);
		this.decls = Declaration.convertMany(elem.getDeclarationDefinitions());
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, expressions);
			TreeVisitor.acceptChildren(visitor, decls);
		}
		visitor.endVisit(this);
	}

}
