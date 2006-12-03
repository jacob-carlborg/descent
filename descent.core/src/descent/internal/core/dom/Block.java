package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.ICompoundStatement;
import descent.core.dom.ASTVisitor;
import descent.core.dom.IStatement;

public class Block extends Statement implements ICompoundStatement {

	private List<Statement> as;
	
	Block(AST ast) {
		super(ast);
	}

	public Block(List<Statement> as) {
		this.as = as;
	}
	
	public IStatement[] getStatements() {
		if (as == null) return new IStatement[0];
		return this.as.toArray(new IStatement[as.size()]);
	}
	
	public int getNodeType0() {
		return COMPOUND_STATEMENT;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChildren(visitor, as);
		}
		visitor.endVisit(this);
	}

}
