package descent.internal.core.dom;

import descent.core.dom.ASTVisitor;
import descent.core.dom.ISimpleName;
import descent.core.dom.ISelectiveImport;

public class SelectiveImport extends ASTNode implements ISelectiveImport {
	
	public Identifier name;
	public Identifier alias;

	public SelectiveImport(Identifier name, Identifier alias) {
		this.name = name;
		this.alias = alias;
	}
	
	public ISimpleName getAlias() {
		return alias;
	}

	public ISimpleName getName() {
		return name;
	}
	
	public int getNodeType0() {
		return SELECTIVE_IMPORT;
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
