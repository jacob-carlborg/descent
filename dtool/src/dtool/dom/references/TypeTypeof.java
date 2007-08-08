/**
 * 
 */
package dtool.dom.references;

import java.util.Collection;

import melnorme.miscutil.tree.TreeVisitor;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.dom.expressions.Expression;

public class TypeTypeof extends CommonRefNative {
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

	public Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly) {
		return expression.getType();
	}

}