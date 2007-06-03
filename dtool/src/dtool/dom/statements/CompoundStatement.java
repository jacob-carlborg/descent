package dtool.dom.statements;

import java.util.List;

import util.tree.TreeVisitor;
import descent.internal.core.dom.ScopeStatement;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.IASTNeoVisitor;

/**
 * A compound statement. Allways introduces a new Scope.
 */
public class CompoundStatement extends Statement {
	
	public List<Statement> statements;

	@SuppressWarnings("unchecked")
	public CompoundStatement(descent.internal.core.dom.CompoundStatement elem) {
		convertNode(elem);
		this.statements = DescentASTConverter.convertMany(elem.as, statements); 
	}

	public CompoundStatement(ScopeStatement elem) {
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

}
