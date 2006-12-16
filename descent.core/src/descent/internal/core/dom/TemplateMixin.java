package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IElement;
import descent.core.dom.ASTVisitor;
import descent.core.dom.IMixinDeclaration;
import descent.core.dom.ISimpleName;
import descent.core.dom.IQualifiedName;
import descent.core.dom.ITypeofType;

public class TemplateMixin extends Declaration implements IMixinDeclaration {

	private QualifiedNameBak qName;
	private TypeofType tqual;
	private IElement[] tiargs;

	public TemplateMixin(Identifier id, TypeofType tqual, List<Identifier> idents, List<IElement> tiargs) {
		super(id);
		this.tqual = tqual;
		this.qName = new QualifiedNameBak(idents);
		if (tiargs != null) {
			this.tiargs = tiargs.toArray(new IElement[tiargs.size()]);
		}
	}
	
	public int getNodeType0() {
		return MIXIN_DECLARATION;
	}
	
	public ISimpleName getName() {
		return ident;
	}
	
	public IQualifiedName getType() {
		return qName;
	}
	
	public ITypeofType getTypeofType() {
		return tqual;
	}
	
	public IElement[] getTemplateArguments() {
		if (tiargs == null) return ASTNode.NO_ELEMENTS; 
		return tiargs;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, tqual);
			acceptChild(visitor, ident);
			acceptChildren(visitor, tiargs);
		}
		visitor.endVisit(this);
	}

	@Override
	ChildListPropertyDescriptor internalModifiersProperty() {
		return null;
	}

}
