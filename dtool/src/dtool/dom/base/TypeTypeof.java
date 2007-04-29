/**
 * 
 */
package dtool.dom.base;

import util.tree.TreeVisitor;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.declarations.DefUnit;
import dtool.dom.expressions.Expression;

public class TypeTypeof extends Entity {
	public Expression expression;

	public TypeTypeof(descent.internal.core.dom.TypeTypeof elem) {
		setSourceRange(elem);
		this.expression = Expression.convert(elem.exp);
	}

	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, expression);
		}
		visitor.endVisit(this);
	}
	
	public String toString() {
		return "typeof(" + "???" +")";
	}

	@Override
	public DefUnit getTargetDefUnit() {
		// TODO: return INTRISINC
		// return expression.getType().getDefUnits();
		return null;
	}
}