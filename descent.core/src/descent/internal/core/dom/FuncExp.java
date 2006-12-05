package descent.internal.core.dom;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IArgument;
import descent.core.dom.IFunctionExpression;
import descent.core.dom.ISimpleName;
import descent.core.dom.IStatement;

public class FuncExp extends Expression implements IFunctionExpression {

	private final FuncLiteralDeclaration fd;

	public FuncExp(FuncLiteralDeclaration fd) {
		this.fd = fd;
	}
	
	public int getNodeType0() {
		return FUNCTION_EXPRESSION;
	}

	public IArgument[] getArguments() {
		return fd.arguments().toArray(new IArgument[fd.arguments().size()]);
	}

	public IStatement getBody() {
		return fd.getBody();
	}

	public IStatement getIn() {
		return fd.getPrecondition();
	}

	public IStatement getOut() {
		return fd.getPostcondition();
	}

	public ISimpleName getOutName() {
		return fd.getPostconditionVariableName();
	}

	public boolean isVariadic() {
		return fd.isVariadic();
	}
	
	public void accept0(ASTVisitor visitor) {
		fd.accept(visitor);
	}

}
