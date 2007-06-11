package dtool.dom.expressions;

import java.util.List;

import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNeoNode;
import dtool.model.IScope;
import dtool.model.IScopeBinding;

public abstract class Expression extends ASTNeoNode implements IScopeBinding {

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
		
		DescentASTConverter.convertMany(rets, elements);
		return rets;
	}
	
	// SCOPE/TYPE BINDING
	public IScope getTargetScope() {
		throw new UnsupportedOperationException(
				"Unsupported peering the type/scope of expression: "+toStringClassName());
	}
}
