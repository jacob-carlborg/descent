/**
 * 
 */
package dtool.dom;

import descent.core.domX.ASTNode;
import dtool.dombase.ASTNeoVisitor;

public class TypeTypeof extends Entity {
	public ASTNode expression;

	public void accept0(ASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, expression);
		}
		visitor.endVisit(this);
	}
	
	public String toString() {
		return "typeof(" + "???" +")";
	}

	@Override
	public DefUnit getReferencedDefUnit() {
		// TODO: return INTRISINC
		// return expression.getType().getDefUnits();
		return null;
	}
}