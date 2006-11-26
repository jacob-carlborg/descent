package descent.internal.core.dom;

import descent.core.dom.IElement;
import descent.core.dom.IDElementVisitor;
import descent.core.dom.IIdentifierType;
import descent.core.dom.IQualifiedName;
import descent.core.dom.ITemplateInstanceType;

public class TypeIdentifier extends TypeQualified implements IIdentifierType, ITemplateInstanceType {

	public Identifier ident;
	public QualifiedName qName;

	public TypeIdentifier(Loc loc, Identifier ident) {
		super(TY.Tident, loc);
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
		Expression e = new IdentifierExp(null, ident);
	    for (int i = 0; i < idents.size(); i++)
	    {
	    	Identifier id = (Identifier) idents.get(i);
	    	e = new DotIdExp(null, e, id);	
	    }
	    return e;
	}
	
	@Override
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			if (isTemplate()) {
				acceptChildren(visitor, getTemplateArguments());
			}
		}
		visitor.endVisit(this);
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
