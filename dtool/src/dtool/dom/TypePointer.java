/**
 * 
 */
package dtool.dom;

import dtool.dombase.ASTNeoVisitor;

public class TypePointer extends Entity {
	public Entity elemtype;
	
	public void accept0(ASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			// TODO: accept children
		}
		visitor.endVisit(this);
	}

	@Override
	public DefUnit getReferencedDefUnit() {
		// TODO return intrinsic
		return null;
	}
}