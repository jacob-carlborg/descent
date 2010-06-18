package dtool.ast.definitions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.PostBlitDeclaration;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.statements.IStatement;
import dtool.ast.statements.Statement;

public class DefinitionPostBlit extends ASTNeoNode {
	
	public IStatement fbody;
	
	public DefinitionPostBlit(PostBlitDeclaration elem) {
		convertNode(elem);
		this.fbody = Statement.convert(elem.fbody);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, fbody);
		}
		visitor.endVisit(this);
	}
	
}
