package dtool.dom.statements;

import java.util.List;

import util.tree.TreeVisitor;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.IASTNeoVisitor;

public class CompoundStatement extends Statement {
	
	public List<Statement> statements;

	@SuppressWarnings("unchecked")
	public CompoundStatement(descent.internal.core.dom.CompoundStatement elem) {
		convertNode(elem);
		this.statements = DescentASTConverter.convertMany(elem.as, statements); 
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
