package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IInitializer;
import descent.core.dom.IName;
import descent.core.dom.IType;
import descent.core.dom.ITypedefDeclaration;
import descent.core.domX.IASTVisitor;

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
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, ident);
			TreeVisitor.acceptChild(visitor, type);
			TreeVisitor.acceptChild(visitor, init);
		}
		visitor.endVisit(this);
	}

}
