package dtool.ast.expressions;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import dtool.ast.definitions.DefUnit;
import dtool.descentadapter.DescentASTConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.refmodel.IDefUnitReferenceNode;

public abstract class Expression extends Resolvable implements IDefUnitReferenceNode {

	// deprecate
	public Collection<DefUnit> getType() {
		return findTargetDefUnits(false);
	}
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly) {
		return Collections.emptySet();
		/*throw new UnsupportedOperationException(
				"Unsupported peering the type/scope of expression: "+toStringClassName());*/
	}
	
	
	/* ---------------- Conversion Funcs ---------------- */
	
	public static Expression convert(descent.internal.compiler.parser.Expression exp, ASTConversionContext convContext) {
		// TODO: AST: convert Exp parenthesis?
		return (Expression) DescentASTConverter.convertElem(exp, convContext);
	}

	public static Expression[] convertMany(descent.internal.compiler.parser.Expression[] elements
			, ASTConversionContext convContext) {
		if(elements == null)
			return null;
		Expression[] rets = new Expression[elements.length];
		DescentASTConverter.convertMany(elements, rets, convContext);
		return rets;
	}
	
	public static Expression[] convertMany(List<descent.internal.compiler.parser.Expression> elements
			, ASTConversionContext convContext) {
		if(elements == null)
			return null;
		Expression[] rets = new Expression[elements.size()];
		
		DescentASTConverter.convertMany(elements, rets, convContext);
		return rets;
	}
	
}
