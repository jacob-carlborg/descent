package descent.internal.core.dom;

import descent.core.dom.IArgument;
import descent.core.dom.ASTVisitor;
import descent.core.dom.IFunctionDeclaration;
import descent.core.dom.ISimpleName;
import descent.core.dom.IStatement;
import descent.core.dom.ITemplateParameter;
import descent.core.dom.IType;

public class FuncDeclaration extends Declaration implements IFunctionDeclaration {

	public Statement frequire;
	public Statement fensure;
	public Statement fbody;
	public Identifier outId;
	public TypeFunction type;
	public ITemplateParameter[] templateParameters;

	public FuncDeclaration(Identifier ident, int storage_class, Type type) {
		super(ident);
		this.type = (TypeFunction) type;
	}
	
	public ISimpleName getName() {
		return ident;
	}
	
	public IType getReturnType() {
		return type == null ? null : type.getReturnType();
	}
	
	public IArgument[] getArguments() {
		if (type == null) return new IArgument[0];
		return type.getArguments();
	}
	
	public boolean isVariadic() {
		return type == null ? false : type.varargs != 0;
	}
	
	public boolean isTemplate() {
		return templateParameters != null;
	}
	
	public ITemplateParameter[] getTemplateParameters() {
		return templateParameters;
	}
	
	public int getNodeType0() {
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
	
	public ISimpleName getOutName() {
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
