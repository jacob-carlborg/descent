package descent.internal.core.dom;

import descent.core.dom.IInitializer;
import descent.core.dom.IName;
import descent.core.dom.IType;
import descent.core.dom.ITypedefDeclaration;
import descent.core.dom.IElement.ElementTypes;
import descent.core.domX.ASTVisitor;

public class TypedefDeclaration extends Declaration implements ITypedefDeclaration {
	
	public Type type;
	public Initializer init;

	public TypedefDeclaration(Identifier ident, Type type, Initializer init) {
		super(ident);
		this.type = type;
		this.init = init;
	}
	
	public IName getName() {
		return ident;
	}
	
	public IType getType() {
		return type;
	}
	
	public IInitializer getInitializer() {
		return init;
	}
	
	public int getElementType() {
		return ElementTypes.TYPEDEF_DECLARATION;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, ident);
			acceptChild(visitor, type);
			acceptChild(visitor, init);
		}
		visitor.endVisit(this);
	}

}
