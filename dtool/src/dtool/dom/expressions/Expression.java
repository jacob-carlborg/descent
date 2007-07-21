package dtool.dom.expressions;

import java.util.Collection;
import java.util.List;

import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.definitions.DefUnit;
import dtool.refmodel.IDefUnitReference;

public abstract class Expression extends ASTNeoNode implements IDefUnitReference {

	
	// TYPE BINDING
	public Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly) {
		throw new UnsupportedOperationException(
				"Unsupported peering the type/scope of expression: "+toStringClassName());
	}
	

	public Collection<DefUnit> getType() {
		return findTargetDefUnits(false);
	}
	
	/* ---------------- Conversion Funcs ---------------- */
	
	public static Expression convert(descent.internal.core.dom.Expression exp) {
		return (Expression) DescentASTConverter.convertElem(exp);
	}

	public static Expression[] convertMany(descent.internal.core.dom.Expression[] elements) {
		Expression[] rets = new Expression[elements.length];
		DescentASTConverter.convertMany(elements, rets);
		return rets;
	}
	
	public static Expression[] convertMany(List<descent.internal.core.dom.Expression> elements) {
		Expression[] rets = new Expression[elements.size()];
		
		DescentASTConverter.convertManyL(rets, elements);
		return rets;
	}
	
}
