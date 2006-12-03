package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IAggregateDeclaration;
import descent.core.dom.IBaseClass;
import descent.core.dom.IDeclaration;
import descent.core.dom.ISimpleName;
import descent.core.dom.ITemplateParameter;

public abstract class AggregateDeclaration extends ScopeDsymbol implements IAggregateDeclaration {

	public IBaseClass[] baseClasses;
	public ITemplateParameter[] templateParameters;
	
	public AggregateDeclaration(Identifier id, List<BaseClass> baseClasses) {
		this.ident = id;
		if (baseClasses == null) {
			this.baseClasses = new IBaseClass[0];
		} else {
			this.baseClasses = baseClasses.toArray(new IBaseClass[baseClasses.size()]);
		}
	}
	
	public ISimpleName getName() {
		return ident;
	}
	
	public boolean isTemplate() {
		return templateParameters != null;
	}
	
	public ITemplateParameter[] getTemplateParameters() {
		return templateParameters;
	}
	
	public IBaseClass[] getBaseClasses() {
		return baseClasses;
	}
	
	@SuppressWarnings("unchecked")
	public IDeclaration[] getDeclarationDefinitions() {
		if (members == null) return ASTNode.NO_DECLARATIONS;
		return (IDeclaration[]) members.toArray(new IDeclaration[members.size()]);
	}
	
	@SuppressWarnings("unchecked")
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, ident);
			acceptChildren(visitor, templateParameters);
			acceptChildren(visitor, baseClasses);
			acceptChildren(visitor, members);
		}
		visitor.endVisit(this);
	}
	
	public int getElementType() {
		return AGGREGATE_DECLARATION;
	}

}
