/**
 * 
 */
package dtool.dom.references;

import java.util.List;

import util.tree.TreeVisitor;
import descent.internal.core.dom.TypeSArray;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.dom.expressions.Expression;
import dtool.dom.references.TypeDynArray.IntrinsicDynArray;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.IntrinsicDefUnit;

public class TypeStaticArray extends Entity {
	public Entity elemtype;
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
		return IntrinsicDynArray.instance;
	}
	
	public static class IntrinsicStaticArray extends IntrinsicDefUnit {
		public static final IntrinsicStaticArray instance = new IntrinsicStaticArray();

		public List<DefUnit> getDefUnits() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IScopeNode getMembersScope() {
			// TODO Auto-generated method stub
			return null;
		}

		public List<IScopeNode> getSuperScopes() {
			// TODO Auto-generated method stub
			return null;
		}
	}
}