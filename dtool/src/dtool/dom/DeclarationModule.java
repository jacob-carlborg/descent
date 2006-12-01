package dtool.dom;

import java.util.List;

import descent.internal.core.dom.Identifier;
import descent.internal.core.dom.QualifiedName;
import dtool.dom.ext.ASTNeoVisitor;

public class DeclarationModule extends ASTElement {

	public List<Identifier> packages;
	public QualifiedName qName;
	
	public void accept0(ASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChildren(visitor, packages);
			acceptChild(visitor, qName);
		}
		visitor.endVisit(this);
	}
}
