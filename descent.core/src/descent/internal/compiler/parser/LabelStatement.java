package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class LabelStatement extends Statement {
	
	public IdentifierExp ident;
	public Statement statement;
	public TryFinallyStatement tf;
	public boolean isReturnLabel;

	public LabelStatement(Loc loc, IdentifierExp ident, Statement statement) {
		super(loc);
		this.ident = ident;
		this.statement = statement;
		this.start = ident.start;
		this.length = statement.start + statement.length - ident.start;
	}
	
	@Override
	public int getNodeType() {
		return LABEL_STATEMENT;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, statement);
		}
		visitor.endVisit(this);
	}

	
}
