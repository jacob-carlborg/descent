/**
 * 
 */
package dtool.dom.references;

import util.tree.TreeVisitor;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
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
		return "typeof(" + expression +")";
	}

	@Override
	public DefUnit getTargetDefUnit() {
		// return expression.getType().getDefUnits();
		return null;
	}
}