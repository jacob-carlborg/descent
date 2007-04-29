package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IInitializer;
import descent.core.dom.IName;
import descent.core.dom.IType;
import descent.core.dom.IVariableDeclaration;
import descent.core.domX.IASTVisitor;

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
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, type);
			TreeVisitor.acceptChild(visitor, ident);
			TreeVisitor.acceptChild(visitor, init);
		}
		visitor.endVisit(this);
	}

}
