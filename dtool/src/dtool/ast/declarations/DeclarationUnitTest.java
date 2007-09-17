package dtool.ast.declarations;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.UnitTestDeclaration;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.statements.BlockStatement;
import dtool.ast.statements.Statement;

public class DeclarationUnitTest extends ASTNeoNode {

	public BlockStatement body;
	
	public DeclarationUnitTest(UnitTestDeclaration elem) {
		convertNode(elem);
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
