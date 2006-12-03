package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IModuleDeclaration;
import descent.core.dom.IQualifiedName;

public class ModuleDeclaration extends Declaration implements IModuleDeclaration {
	
	public List<Identifier> packages;
	public QualifiedNameBak qName;

	public ModuleDeclaration(List<Identifier> packages, Identifier id) {
		super(id);
		this.packages = packages;
		this.qName = new QualifiedNameBak(packages, id);
	}
	
	public IQualifiedName getQualifiedName() {
		return qName;
	}
	
	public int getNodeType0() {
		return MODULE_DECLARATION;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, qName);
		}
		visitor.endVisit(this);
	}
	
}
