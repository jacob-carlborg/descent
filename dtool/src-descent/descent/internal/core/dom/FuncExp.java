package descent.internal.core.dom;

import descent.core.dom.IExpression;
import descent.core.dom.IName;
import descent.core.dom.IStatement;
import descent.core.domX.IASTVisitor;

public class FuncExp extends Expression implements IExpression {

	private final FuncLiteralDeclaration fd;

	public FuncExp(FuncLiteralDeclaration fd) {
		this.fd = fd;
	}
	
	public int getElementType() {
		return ElementTypes.FUNCTION_EXPRESSION;
	}

	public Argument[] getArguments() {
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
	
	public void accept0(IASTVisitor visitor) {
		fd.accept(visitor);
	}

}
