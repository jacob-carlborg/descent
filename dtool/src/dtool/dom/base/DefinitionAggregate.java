package dtool.dom.base;

import java.util.ArrayList;
import java.util.List;

import util.tree.TreeVisitor;

import dtool.dom.ast.ASTNeoVisitor;
import dtool.model.IScope;

/**
 * A definition of a aggregate. TODO.
 */
public class DefinitionAggregate extends Definition implements IScope {
	
	public List<ASTNeoNode> members; 
	
	
	public EArcheType getArcheType() {
		return EArcheType.Aggregate;
	}
	
	public void accept0(ASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, members);
		}
		visitor.endVisit(this);
	}

	@Override
	public IScope getScope() {
		return this;
	}
	
	public List<DefUnit> getDefUnits() {
		List<DefUnit> defunits = new ArrayList<DefUnit>();
		for(ASTNode elem: members) {
			if(elem instanceof DefUnit)
				defunits.add((DefUnit)elem);
		}
		return defunits;
	}
}
