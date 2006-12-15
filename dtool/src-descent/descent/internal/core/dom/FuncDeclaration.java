package descent.internal.core.dom;

import descent.core.dom.IDeclaration;
import descent.core.dom.IModifiersContainer;
import descent.core.dom.IName;
import descent.core.dom.IStatement;
import descent.core.domX.ASTVisitor;

public class FuncDeclaration extends Declaration implements IDeclaration, IModifiersContainer {

	public Statement frequire;
	public Statement fensure;
	public Statement fbody;
	public Identifier outId;
	public TypeFunction type;
	public TemplateParameter[] templateParameters;

	public static interface IFunctionDeclaration {
		int FUNCTION = 1;
		int CONSTRUCTOR = 2;
		int DESTRUCTOR = 3;
		int STATIC_CONSTRUCTOR = 4;
		int STATIC_DESTRUCTOR = 5;
		int NEW = 6;
		int DELETE = 7;
	}
	
	public FuncDeclaration(Identifier ident, int storage_class, Type type) {
		super(ident);
		this.type = (TypeFunction) type;
	}
	
	public Identifier getName() {
		return ident;
	}
	
	public Type getReturnType() {
		return type == null ? null : type.getReturnType();
	}
	
	public Argument[] getArguments() {
		if (type == null) return new Argument[0];
		return type.getArguments();
	}
	
	public boolean isVariadic() {
		return type == null ? false : type.varargs != 0;
	}
	
	public boolean isTemplate() {
		return templateParameters != null;
	}
	
	public TemplateParameter[] getTemplateParameters() {
		return templateParameters;
	}
	
	public int getElementType() {
		return ElementTypes.FUNCTION_DECLARATION;
	}
	
	public int getFunctionDeclarationType() {
		return IFunctionDeclaration.FUNCTION;
	}
	
	public IStatement getBody() {
		return fbody;
	}
	
	public IStatement getIn() {
		return frequire;
	}
	
	public IStatement getOut() {
		return fensure;
	}
	
	public IName getOutName() {
		return outId;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, getReturnType());
			acceptChild(visitor, ident);
			acceptChildren(visitor, templateParameters);
			acceptChildren(visitor, getArguments());
			acceptChild(visitor, fbody);
			acceptChild(visitor, frequire);
			acceptChild(visitor, fensure);
			acceptChild(visitor, outId);
		}
		visitor.endVisit(this);
	}

}
