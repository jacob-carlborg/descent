package dtool.dom.declarations;

import descent.internal.core.dom.DebugSymbol;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;

public class DebugSymbolDefinition extends ASTNeoNode {

	public DebugSymbolDefinition(DebugSymbol element) {
		setSourceRange(element);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		// TODO Auto-generated method stub

	}

}
