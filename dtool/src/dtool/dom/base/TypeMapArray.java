package dtool.dom.base;

import util.tree.TreeVisitor;
import descent.internal.core.dom.TypeAArray;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.declarations.DefUnit;

public class TypeMapArray extends Entity {
	public EntityConstrainedRef.TypeConstraint keytype;
	public EntityConstrainedRef.TypeConstraint valuetype;

	public TypeMapArray(TypeAArray elem) {
		setSourceRange(elem);
		this.valuetype = Entity.convertType(elem.next);
		this.keytype = Entity.convertType(elem.index);
	}

	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, keytype);
			TreeVisitor.acceptChildren(visitor, valuetype);
		}
		visitor.endVisit(this);
	}

	@Override
	public DefUnit getTargetDefUnit() {
		// TODO: return INTRISINC
		return null;
	}
}