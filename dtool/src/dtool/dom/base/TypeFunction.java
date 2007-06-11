package dtool.dom.base;

import java.util.List;

import util.tree.TreeVisitor;

import descent.internal.core.dom.Argument;
import descent.internal.core.dom.LINK;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.base.TypeDynArray.IntrinsicDynArray;
import dtool.dom.definitions.DefUnit;
import dtool.model.IntrinsicDefUnit;

/**
 * A function pointer type
 */
public class TypeFunction extends Entity {
	
	public BaseEntityRef.TypeConstraint rettype;
	public List<Argument> arguments;
	public int varargs;
	public LINK linkage;

	public TypeFunction(descent.internal.core.dom.TypeFunction elem) {
		setSourceRange(elem);
		this.rettype = Entity.convertType(elem.getReturnType());
		this.varargs = elem.varargs;
		this.linkage = elem.linkage;
	}

	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, rettype);
			TreeVisitor.acceptChildren(visitor, arguments);
		}
		visitor.endVisit(this);
	}

	@Override
	protected DefUnit getTargetDefUnitAsRoot() {
		return IntrinsicDynArray.instance;
	}
	
	public static class IntrinsicFunction extends IntrinsicDefUnit {
		public static final IntrinsicFunction instance = new IntrinsicFunction();

		public List<DefUnit> getDefUnits() {
			// TODO Auto-generated method stub
			return null;
		}
	}
}