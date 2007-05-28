package dtool.dom.base;

import java.util.List;

import util.tree.TreeVisitor;
import descent.internal.core.dom.Argument;
import descent.internal.core.dom.TypeFunction;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.declarations.DefUnit;

/**
 * A delegate type;
 * XXX: Do D delegates have linkage?
 */
public class TypeDelegate extends Entity {

	public EntityConstrainedRef.TypeConstraint rettype;
	public List<Argument> arguments;
	public int varargs;
	//public LINK linkage;
	
	public TypeDelegate(descent.internal.core.dom.TypeDelegate elem) {
		setSourceRange(elem);
		this.rettype = Entity.convertType(elem.getReturnType());
		this.varargs = ((TypeFunction) elem.next).varargs;
		//this.linkage = ((TypeFunction) elem.next).linkage;
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, rettype);
			TreeVisitor.acceptChildren(visitor, arguments);
		}
		visitor.endVisit(this);
	}

	@Override
	public DefUnit getTargetDefUnit() {
		// TODO return INTRINSIC
		return null;
	}
}
