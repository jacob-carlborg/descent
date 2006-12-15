package descent.internal.core.dom;

import descent.core.dom.IInitializer;
import descent.core.dom.IName;
import descent.core.dom.IType;
import descent.core.dom.IVariableDeclaration;
import descent.core.domX.ASTVisitor;

public class VarDeclaration extends Declaration implements IVariableDeclaration {
	
	public Type type;
	public Initializer init;

	public VarDeclaration(Type type, Identifier ident, Initializer init) {
		super(ident);
		this.type = type;
		this.init = init;
	}
	
	public int getElementType() {
		return ElementTypes.VARIABLE_DECLARATION;
	}
	
	public IType getType() {
		return type;
	}
	
	public IName getName() {
		return ident;
	}
	
	public IInitializer getInitializer() {
		return init;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, type);
			acceptChild(visitor, ident);
			acceptChild(visitor, init);
		}
		visitor.endVisit(this);
	}

}
