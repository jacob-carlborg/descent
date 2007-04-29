package dtool.dom.declarations;

import descent.internal.core.dom.InvariantDeclaration;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;

public class DeclarationInvariant extends ASTNeoNode {

	public DeclarationInvariant(InvariantDeclaration element) {
		setSourceRange(element);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		// TODO Auto-generated method stub

	}

}
