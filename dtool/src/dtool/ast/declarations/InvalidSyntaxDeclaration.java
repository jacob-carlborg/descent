package dtool.ast.declarations;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ASTDmdNode;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.statements.IStatement;

public class InvalidSyntaxDeclaration extends ASTNeoNode implements IStatement {
	
	public ASTNeoNode[] genericChildren;
	
	public InvalidSyntaxDeclaration(ASTDmdNode elem, ASTNeoNode... children) {
		convertNode(elem);
		this.genericChildren = children;
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, genericChildren);
		}
		visitor.endVisit(this);
	}
	
}