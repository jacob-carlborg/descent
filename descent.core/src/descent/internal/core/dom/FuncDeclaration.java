package descent.internal.core.dom;

import descent.core.dom.IArgument;
import descent.core.dom.ElementVisitor;
import descent.core.dom.IFunctionDeclaration;
import descent.core.dom.IName;
import descent.core.dom.IStatement;
import descent.core.dom.ITemplateParameter;
import descent.core.dom.IType;

public class FuncDeclaration extends Declaration implements IFunctionDeclaration {

	public Statement frequire;
	public Statement fensure;
	public Statement fbody;
	public Loc endloc;
	public Identifier outId;
	public TypeFunction type;
	public ITemplateParameter[] templateParameters;

	public FuncDeclaration(Loc loc, int endloc, Identifier ident, int storage_class, Type type) {
		super(ident);
		this.type = (TypeFunction) type;
	}
	
	public IName getName() {
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
	
	public void accept0(ElementVisitor visitor) {
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
