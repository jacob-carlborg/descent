package dtool.ast.expressions;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.CallExp;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.DefinitionFunction;
import dtool.refmodel.DefUnitSearch;
import dtool.refmodel.ReferenceResolver;

public class ExpCall extends Expression {

	public Expression callee;
	public Resolvable[] args;
	
	public ExpCall(CallExp elem) {
		convertNode(elem);
		this.callee = Expression.convert(elem.e1); 
		this.args = Expression.convertMany(elem.arguments);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, callee);
			TreeVisitor.acceptChildren(visitor, args);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly) {
		DefUnit defUnit = callee.findTargetDefUnit();
		if(defUnit == null)
			return null;
		if (defUnit instanceof DefinitionFunction) {
			DefinitionFunction defOpCallFunc = (DefinitionFunction) defUnit;
			DefUnit targetDefUnit = defOpCallFunc.rettype.findTargetDefUnit();
			return Collections.singleton(targetDefUnit);
		}
		
		DefUnitSearch search = new DefUnitSearch("opCall", this);
		ReferenceResolver.findDefUnitInScope(defUnit.getMembersScope(), search);
		for (Iterator<DefUnit> iter = search.getDefUnits().iterator(); iter.hasNext();) {
			DefUnit defOpCall = iter.next();
			if (defOpCall instanceof DefinitionFunction) {
				DefinitionFunction defOpCallFunc = (DefinitionFunction) defOpCall;
				DefUnit targetDefUnit = defOpCallFunc.rettype.findTargetDefUnit();
				return Collections.singleton(targetDefUnit);
			}
		}
		return null;
	}

}