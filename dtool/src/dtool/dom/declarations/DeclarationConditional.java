package dtool.dom.declarations;

import descent.internal.core.dom.ConditionalDeclaration;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;

public class DeclarationConditional extends ASTNeoNode {

	public DeclarationConditional(ConditionalDeclaration element) {
		setSourceRange(element);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		// TODO Auto-generated method stub

	}

}
