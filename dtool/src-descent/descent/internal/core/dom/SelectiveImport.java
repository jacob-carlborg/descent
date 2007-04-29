package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IName;
import descent.core.domX.IASTVisitor;
import descent.core.domX.AbstractElement;

public class SelectiveImport extends AbstractElement {
	
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
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, name);
			TreeVisitor.acceptChild(visitor, alias);
		}
		visitor.endVisit(this);
	}

}
