/**
 * 
 */
package dtool.dom.base;

import util.tree.TreeVisitor;
import descent.internal.core.dom.TypeSArray;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.dom.expressions.Expression;

public class TypeStaticArray extends Entity {
	public BaseEntityRef.TypeConstraint elemtype;
	public Expression sizeexp;

	public TypeStaticArray(TypeSArray elem) {
		setSourceRange(elem);
		this.elemtype = Entity.convertType(elem.next);
		this.sizeexp = Expression.convert(elem.dim); 
	}

	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, elemtype);
			TreeVisitor.acceptChildren(visitor, sizeexp);
		}
		visitor.endVisit(this);
	}

	@Override
	public DefUnit getTargetDefUnit() {
		// TODO return INTRINSIC
		return null;
	}
}