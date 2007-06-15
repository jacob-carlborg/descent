package dtool.dom.expressions;

import java.util.List;

import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.definitions.DefUnit;
import dtool.model.IScope;
import dtool.model.IDefUnitReference;

public abstract class Expression extends ASTNeoNode implements IDefUnitReference {

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
	
	
	// SCOPE/TYPE BINDING
	public DefUnit getTargetDefUnit() {
		throw new UnsupportedOperationException(
				"Unsupported peering the type/scope of expression: "+toStringClassName());
	}
	
	// SCOPE/TYPE BINDING
	public IScope getTargetScope() {
		return getTargetDefUnit().getMembersScope();
	}
}
