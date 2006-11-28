package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IElement;
import descent.core.dom.ElementVisitor;
import descent.core.dom.IMixinDeclaration;
import descent.core.dom.IName;
import descent.core.dom.IQualifiedName;
import descent.core.dom.ITypeofType;

public class TemplateMixin extends Declaration implements IMixinDeclaration {

	private QualifiedName qName;
	private TypeTypeof tqual;
	private IElement[] tiargs;

	public TemplateMixin(Identifier id, TypeTypeof tqual, List<Identifier> idents, List<IElement> tiargs) {
		super(id);
		this.tqual = tqual;
		this.qName = new QualifiedName(idents);
		if (tiargs != null) {
			this.tiargs = tiargs.toArray(new IElement[tiargs.size()]);
		}
	}
	
	public int getElementType() {
		return MIXIN_DECLARATION;
	}
	
	public IName getName() {
		return ident;
	}
	
	public IQualifiedName getType() {
		return qName;
	}
	
	public ITypeofType getTypeofType() {
		return tqual;
	}
	
	public IElement[] getTemplateArguments() {
		if (tiargs == null) return AbstractElement.NO_ELEMENTS; 
		return tiargs;
	}
	
	public void accept0(ElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, tqual);
			acceptChild(visitor, ident);
			acceptChildren(visitor, tiargs);
		}
		visitor.endVisit(this);
	}

}
