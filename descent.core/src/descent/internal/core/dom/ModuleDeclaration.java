package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IDElementVisitor;
import descent.core.dom.IModuleDeclaration;
import descent.core.dom.IQualifiedName;

public class ModuleDeclaration extends AbstractElement implements IModuleDeclaration {
	
	public Identifier id;
	public List<Identifier> packages;
	public QualifiedName qName;

	public ModuleDeclaration(List<Identifier> packages, Identifier id) {
		this.id = id;
		this.packages = packages;
		this.qName = new QualifiedName(packages, id);
	}
	
	public IQualifiedName getQualifiedName() {
		return qName;
	}
	
	public int getElementType() {
		return MODULE_DECLARATION;
	}
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, qName);
		}
		visitor.endVisit(this);
	}
	
}
