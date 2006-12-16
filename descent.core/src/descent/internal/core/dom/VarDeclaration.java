package descent.internal.core.dom;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IInitializer;
import descent.core.dom.ISimpleName;
import descent.core.dom.IType;
import descent.core.dom.IVariableDeclaration;

public class VarDeclaration extends Declaration implements IVariableDeclaration {
	
	public Type type;
	public Initializer init;

	public VarDeclaration(AST ast, Type type, Identifier ident, Initializer init) {
		super(ident);
		this.type = type;
		this.init = init;
	}
	
	public int getNodeType0() {
		return VARIABLE_DECLARATION;
	}
	
	public IType getType() {
		return type;
	}
	
	public ISimpleName getName() {
		return ident;
	}
	
	public IInitializer getInitializer() {
		return init;
	}
	
	public int getModifier() {
		return 0;
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
