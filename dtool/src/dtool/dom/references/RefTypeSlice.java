package dtool.dom.references;

import java.util.Collection;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.TypeSlice;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.dom.expressions.Expression;
import dtool.refmodel.IDefUnitReferenceNode;

public class RefTypeSlice extends Reference {

	public IDefUnitReferenceNode slicee;
	public Expression from;
	public Expression to;
	
	public RefTypeSlice(TypeSlice elem) {
		slicee = Reference.convertType(elem.next);
		from = Expression.convert(elem.lwr);
		to = Expression.convert(elem.upr);
	}
	

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, slicee);
			TreeVisitor.acceptChildren(visitor, from);
			TreeVisitor.acceptChildren(visitor, to);
		}
		visitor.endVisit(this);
	}


	public Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly) {
		// TODO:
		return null;
	}


	@Override
	public String toStringAsElement() {
		return slicee.toStringAsElement()+"["+from+".."+to+"]";
	}

}
