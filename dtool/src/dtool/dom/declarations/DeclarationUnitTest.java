package dtool.dom.declarations;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.UnitTestDeclaration;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.statements.BlockStatement;
import dtool.dom.statements.Statement;

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
