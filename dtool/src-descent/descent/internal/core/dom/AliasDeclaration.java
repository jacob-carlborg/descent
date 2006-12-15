package descent.internal.core.dom;

import descent.core.dom.IDeclaration;
import descent.core.dom.IModifiersContainer;
import descent.core.dom.IName;
import descent.core.dom.IType;
import descent.core.domX.ASTVisitor;

public class AliasDeclaration extends Declaration implements IDeclaration, IModifiersContainer {
	
	public Type type;

	public AliasDeclaration(Identifier ident, Type type) {
		super(ident);
		this.type = type;
	}
	
	public IName getName() {
		return ident;
	}
	
	public IType getType() {
		return type;
	}
	
	public int getElementType() {
		return ElementTypes.ALIAS_DECLARATION;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, ident);
			acceptChild(visitor, type);
		}
		visitor.endVisit(this);
	}

}
