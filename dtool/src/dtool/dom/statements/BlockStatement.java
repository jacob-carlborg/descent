package dtool.dom.statements;

import java.util.ArrayList;
import java.util.List;

import util.tree.TreeVisitor;
import descent.internal.core.dom.ScopeStatement;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.ast.IASTNode;
import dtool.dom.definitions.DefUnit;
import dtool.model.IScope;

/**
 * A compound statement. Allways introduces a new Scope.
 */
public class BlockStatement extends Statement implements IScope {
	
	public List<IStatement> statements;

	@SuppressWarnings("unchecked")
	public BlockStatement(descent.internal.core.dom.CompoundStatement elem) {
		convertNode(elem);
		this.statements = DescentASTConverter.convertMany(elem.as, statements);
		
		for(@SuppressWarnings("unused")	IStatement decl : statements) {
			// just check class cast
		}
	}

	public BlockStatement(ScopeStatement elem) {
		convertNode(elem);
		descent.internal.core.dom.CompoundStatement compoundStat = 
			(descent.internal.core.dom.CompoundStatement) elem.s;
		this.statements = DescentASTConverter.convertMany(compoundStat.as, statements); 
	}


	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, statements);
		}
		visitor.endVisit(this);
	}

	public List<DefUnit> getDefUnits() {
		List<DefUnit> defunits = new ArrayList<DefUnit>();
		for(IASTNode elem : statements) {
			if(elem instanceof DefUnit) {
				defunits.add((DefUnit) elem);
			}
		}
		return defunits;
	}

	public IScope getSuperScope() {
		return null;
	}

}
