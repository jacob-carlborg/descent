package descent.internal.core.dom;

import descent.core.dom.IDElementVisitor;
import descent.core.dom.IInitializer;
import descent.core.dom.IName;
import descent.core.dom.IType;
import descent.core.dom.IVariableDeclaration;

public class VarDeclaration extends Declaration implements IVariableDeclaration {
	
	public Type type;
	public Initializer init;

	public VarDeclaration(Loc loc, Type type, Identifier ident, Initializer init) {
		super(ident);
		this.type = type;
		this.init = init;
	}
	
	public int getElementType() {
		return VARIABLE_DECLARATION;
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
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, type);
			acceptChild(visitor, ident);
			acceptChild(visitor, init);
		}
		visitor.endVisit(this);
	}

}
