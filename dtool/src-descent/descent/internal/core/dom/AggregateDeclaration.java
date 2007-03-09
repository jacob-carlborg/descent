package descent.internal.core.dom;

import java.util.List;

import util.tree.TreeVisitor;

import descent.core.dom.IAggregateDeclaration;
import descent.core.dom.IDeclaration;
import descent.core.domX.ASTVisitor;
import descent.core.domX.AbstractElement;

public abstract class AggregateDeclaration extends ScopeDsymbol implements IAggregateDeclaration {

	public Type type;
	public Scope scope;
	public Type handle;
	public int structalign;
	public PROT protection;
	
	public BaseClass[] baseClasses;
	public TemplateParameter[] templateParameters;
	public int storage_class;
	
	public AggregateDeclaration(Identifier id, List<BaseClass> baseClasses) {
		this.ident = id;
		if (baseClasses == null) {
			this.baseClasses = new BaseClass[0];
		} else {
			this.baseClasses = baseClasses.toArray(new BaseClass[baseClasses.size()]);
		}
	}
	
	public Identifier getName() {
		return ident;
	}
	
	public boolean isTemplate() {
		return templateParameters != null;
	}
	
	public TemplateParameter[] getTemplateParameters() {
		return templateParameters;
	}
	
	public BaseClass[] getBaseClasses() {
		return baseClasses;
	}
	
	@SuppressWarnings("unchecked")
	public IDeclaration[] getDeclarationDefinitions() {
		if (members == null) return AbstractElement.NO_DECLARATIONS;
		return (IDeclaration[]) members.toArray(new IDeclaration[members.size()]);
	}
	
	@SuppressWarnings("unchecked")
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, ident);
			TreeVisitor.acceptChildren(visitor, templateParameters);
			TreeVisitor.acceptChildren(visitor, baseClasses);
			TreeVisitor.acceptChildren(visitor, members);
		}
		visitor.endVisit(this);
	}
	
	public int getElementType() {
		return ElementTypes.AGGREGATE_DECLARATION;
	}

}
