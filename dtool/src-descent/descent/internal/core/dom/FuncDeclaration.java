package descent.internal.core.dom;

import descent.core.dom.IArgument;
import descent.core.dom.IFunctionDeclaration;
import descent.core.dom.IName;
import descent.core.dom.IStatement;
import descent.core.dom.IType;
import descent.core.domX.ASTVisitor;

public class FuncDeclaration extends Declaration implements IFunctionDeclaration {

	public Statement frequire;
	public Statement fensure;
	public Statement fbody;
	public Identifier outId;
	public TypeFunction type;
	public TemplateParameter[] templateParameters;

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
		return FUNCTION_DECLARATION;
	}
	
	public int getFunctionDeclarationType() {
		return FUNCTION;
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
