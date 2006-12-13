package descent.internal.core.dom;

import descent.core.dom.IName;
import descent.core.dom.ISelectiveImport;
import descent.core.dom.IElement.ElementTypes;
import descent.core.domX.ASTVisitor;
import descent.core.domX.AbstractElement;

public class SelectiveImport extends AbstractElement implements ISelectiveImport {
	
	public Identifier name;
	public Identifier alias;

	public SelectiveImport(Identifier name, Identifier alias) {
		this.name = name;
		this.alias = alias;
	}
	
	public IName getAlias() {
		return alias;
	}

	public IName getName() {
		return name;
	}
	
	public int getElementType() {
		return ElementTypes.SELECTIVE_IMPORT;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, name);
			acceptChild(visitor, alias);
		}
		visitor.endVisit(this);
	}

}
