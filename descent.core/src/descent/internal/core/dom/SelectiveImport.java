package descent.internal.core.dom;

import descent.core.dom.ElementVisitor;
import descent.core.dom.IName;
import descent.core.dom.ISelectiveImport;

public class SelectiveImport extends ASTNode implements ISelectiveImport {
	
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
		return SELECTIVE_IMPORT;
	}
	
	public void accept0(ElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, name);
			acceptChild(visitor, alias);
		}
		visitor.endVisit(this);
	}

}
