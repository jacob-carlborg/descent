package descent.internal.core.dom;

import descent.core.dom.ASTVisitor;
import descent.core.dom.ITypeidExpression;

public class TypeidExp extends Expression implements ITypeidExpression {

	private final Type type;

	public TypeidExp(AST ast, Type type) {
		super(ast);
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}
	
	public int getNodeType0() {
		return TYPEID_EXPRESSION;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, type);
		}
		visitor.endVisit(this);
	}

}
