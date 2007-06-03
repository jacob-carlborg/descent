package dtool.dom.statements;

import util.tree.TreeVisitor;
import descent.internal.core.dom.DeclarationStatement;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.declarations.Declaration;

public class StatementDeclaration extends Statement {
	
	ASTNode decl;

	public StatementDeclaration(DeclarationStatement elem) {
		convertNode(elem);
		this.decl = Declaration.convert(elem.d);  
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, decl);
		}
		visitor.endVisit(this);	 
	}

}
