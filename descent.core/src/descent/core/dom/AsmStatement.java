package descent.core.dom;

import java.util.List;

import descent.internal.core.parser.Token;

public class AsmStatement extends Statement {

	public AsmStatement(AST ast, Token toklist) {
		super(ast);
	}
	
	public void accept0(ASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

	public int getNodeType0() {
		return ASM_STATEMENT;
	}

	@Override
	List internalStructuralPropertiesForType(int apiLevel) {
		return null;
	}

}
