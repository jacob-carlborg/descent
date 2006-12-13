package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.ICommented;
import descent.core.dom.IDeclaration;
import descent.core.dom.IQualifiedName;
import descent.core.dom.IElement.ElementTypes;
import descent.core.domX.ASTVisitor;

public class ModuleDeclaration extends Declaration implements IDeclaration, ICommented {
	
	public List<Identifier> packages;
	public QualifiedName qName;

	public ModuleDeclaration(List<Identifier> packages, Identifier id) {
		super(id);
		this.packages = packages;
		this.qName = new QualifiedName(packages, id);
	}
	
	public IQualifiedName getQualifiedName() {
		return qName;
	}
	
	public int getElementType() {
		return ElementTypes.MODULE_DECLARATION;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, qName);
		}
		visitor.endVisit(this);
	}
	
}
