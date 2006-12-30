/**
 * 
 */
package dtool.dom.base;

import dtool.dom.ast.ASTNeoVisitor;

public class TypeDynArray extends Entity {
	public Entity elemtype;

	public void accept0(ASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChildren(visitor, elemtype);
		}
		visitor.endVisit(this);
	}

	public String toString() {
		return elemtype + "[]";
	}

	@Override
	public DefUnit getReferencedDefUnit() {
		// TODO: return INTRISINC
		return null;
	}
}