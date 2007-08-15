package dtool.dom.declarations;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.InvariantDeclaration;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.statements.BlockStatement;
import dtool.dom.statements.Statement;

public class DeclarationInvariant extends ASTNeoNode {

	public BlockStatement body;
	
	public DeclarationInvariant(InvariantDeclaration elem) {
		setSourceRange(elem);
		this.body = (BlockStatement) Statement.convert(elem.fbody);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}

}
