package descent.internal.core.dom;

import descent.core.dom.IArgument;
import descent.core.dom.IFunctionExpression;
import descent.core.dom.IName;
import descent.core.dom.IStatement;
import descent.core.dom.IElement.ElementTypes;
import descent.core.domX.ASTVisitor;

public class FuncExp extends Expression implements IFunctionExpression {

	private final FuncLiteralDeclaration fd;

	public FuncExp(FuncLiteralDeclaration fd) {
		this.fd = fd;
	}
	
	public int getElementType() {
		return ElementTypes.FUNCTION_EXPRESSION;
	}

	public IArgument[] getArguments() {
		return fd.getArguments();
	}

	public IStatement getBody() {
		return fd.getBody();
	}

	public IStatement getIn() {
		return fd.getIn();
	}

	public IStatement getOut() {
		return fd.getOut();
	}

	public IName getOutName() {
		return fd.getOutName();
	}

	public boolean isVariadic() {
		return fd.isVariadic();
	}
	
	public void accept0(ASTVisitor visitor) {
		fd.accept(visitor);
	}

}
