package descent.internal.core.dom;

import descent.core.dom.IElement;
import descent.core.dom.ElementVisitor;
import descent.core.dom.IIdentifierType;
import descent.core.dom.IQualifiedName;
import descent.core.dom.ITemplateInstanceType;

public class TypeIdentifier extends TypeQualified implements IIdentifierType, ITemplateInstanceType {

	public Identifier ident;
	public QualifiedName qName;

	public TypeIdentifier(Identifier ident) {
		super(TY.Tident);
		this.ident = ident;
	}
	
	public int getElementType() {
		if (idents.size() == 0) {
			return IDENTIFIER_TYPE;
		} else {
			if (idents.get(idents.size() - 1) instanceof TemplateInstance) {
				return TEMPLATE_INSTANCE_TYPE;
			} else {
				return IDENTIFIER_TYPE;
			}
		}
	}
	
	public String getShortName() {
		if (idents.size() == 0) {
			return ident.string;
		} else {
			return idents.get(idents.size() - 1).string;
		}
	}
	
	@Override
	public IQualifiedName getName() {
		if (qName == null) {
			qName = new QualifiedName(ident, idents);
		}
		return qName;
	}
	
	public boolean isTemplate() {
		if (idents.size() == 0) {
			return false;
		} else {
			return idents.get(idents.size() - 1) instanceof TemplateInstance;
		}
	}
	
	public IElement[] getTemplateArguments() {
		return ((TemplateInstance) idents.get(idents.size() - 1)).getTemplateArguments();
	}
	
	@Override
	public Expression toExpression() {
		Expression e = new IdentifierExp(ident);
	    for (int i = 0; i < idents.size(); i++)
	    {
	    	Identifier id = (Identifier) idents.get(i);
	    	e = new DotIdExp(e, id);	
	    }
	    return e;
	}
	
	@Override
	public void accept0(ElementVisitor visitor) {
		switch(getElementType()) {
		case IDENTIFIER_TYPE:
			visitor.visit((IIdentifierType) this);
			visitor.endVisit((IIdentifierType) this);
			break;
		case TEMPLATE_INSTANCE_TYPE:
			boolean children = visitor.visit((ITemplateInstanceType) this);
			if (children) {
				acceptChildren(visitor, getTemplateArguments());
			}
			visitor.endVisit((ITemplateInstanceType) this);
			break;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(ident);
		for(Identifier id : idents) {
			sb.append('.');
			sb.append(id.string);
		}
		return sb.toString();
	}

}
