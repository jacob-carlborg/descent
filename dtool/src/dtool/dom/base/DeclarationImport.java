package dtool.dom.base;

import java.util.List;

import descent.internal.core.dom.Import;
import dtool.dom.ast.ASTNeoVisitor;

/**
 * An Import Declaration
 */
public class DeclarationImport extends ASTNeoNode {

	public List<Import> imports;
	public boolean isStatic;
	
	public void accept0(ASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChildren(visitor, imports);
		}
		visitor.endVisit(this);
	}
}