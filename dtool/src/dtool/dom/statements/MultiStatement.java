package dtool.dom.statements;

import java.util.List;

import util.tree.TreeVisitor;
import descent.internal.core.dom.ScopeStatement;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.IASTNeoVisitor;

/**
 * A compound statement which does not introduces a new Scope.
 */
public class MultiStatement extends Statement {
	
	public List<IStatement> statements;

	@SuppressWarnings("unchecked")
	public MultiStatement(descent.internal.core.dom.CompoundStatement elem) {
		convertNode(elem);
		this.statements = DescentASTConverter.convertManyL(elem.as, statements);
		
		for(@SuppressWarnings("unused")	IStatement decl : statements) {
			// just check class cast
		}
	}

	public MultiStatement(ScopeStatement elem) {
		convertNode(elem);
		descent.internal.core.dom.CompoundStatement compoundStat = 
			(descent.internal.core.dom.CompoundStatement) elem.s;
		this.statements = DescentASTConverter.convertManyL(compoundStat.as, statements); 
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
