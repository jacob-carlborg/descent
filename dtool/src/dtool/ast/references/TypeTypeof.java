/**
 * 
 */
package dtool.ast.references;

import java.util.Collection;

import melnorme.miscutil.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.expressions.Expression;

public class TypeTypeof extends CommonRefNative {
	public Expression expression;

	public TypeTypeof(descent.internal.compiler.parser.TypeTypeof elem) {
		//setSourceRange(elem);
		setSourceRange(elem.typeofStart, elem.typeofLength);
		this.expression = Expression.convert(elem.exp);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, expression);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public String toStringAsElement() {
		return "typeof(" + expression.toStringAsElement() +")";
	}

	@Override
	public Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly) {
		return expression.getType();
	}

}