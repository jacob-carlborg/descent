/**
 * 
 */
package dtool.dom;

import descent.core.domX.ASTNode;
import dtool.dombase.ASTNeoVisitor;

public class TypeStaticArray extends Entity {
	public Entity elemtype;
	public ASTNode sizeExp;

	public void accept0(ASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			// TODO: accept children
		}
		visitor.endVisit(this);
	}

	@Override
	public DefUnit getReferencedDefUnit() {
		// TODO return INTRINSIC
		return null;
	}
}