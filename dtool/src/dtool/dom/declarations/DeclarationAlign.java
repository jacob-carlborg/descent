package dtool.dom.declarations;

import descent.internal.core.dom.AlignDeclaration;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;

public class DeclarationAlign extends ASTNeoNode {

	public DeclarationAlign(AlignDeclaration element) {
		setSourceRange(element);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		// TODO Auto-generated method stub

	}

}
