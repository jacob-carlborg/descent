package descent.internal.core.dom;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;


import descent.core.dom.ICommented;
import descent.core.dom.IDeclaration;
import descent.core.domX.IASTVisitor;

public class ModuleDeclaration extends Declaration implements IDeclaration, ICommented {
	
	public List<Identifier> packages;
	public QualifiedName qName;

	public ModuleDeclaration(List<Identifier> packages, Identifier id) {
		super(id);
		this.packages = packages;
		this.qName = new QualifiedName(packages, id);
	}
	
	public QualifiedName getQualifiedName() {
		return qName;
	}
	
	public int getElementType() {
		return ElementTypes.MODULE_DECLARATION;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, qName);
		}
		visitor.endVisit(this);
	}
	
}
