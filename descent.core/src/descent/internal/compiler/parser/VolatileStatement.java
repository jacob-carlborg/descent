package descent.internal.compiler.parser;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class VolatileStatement extends Statement {

	public Statement statement;
	public Statement sourceStatement;

	public VolatileStatement(Loc loc, Statement statement) {
		super(loc);
		this.sourceStatement = statement;
		this.statement = statement;
	}

	@Override
	public int getNodeType() {
		return VOLATILE_STATEMENT;
	}

	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, sourceStatement);
		}
		visitor.endVisit(this);
	}

	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		statement = statement != null ? statement.semantic(sc, context) : null;
		return this;
	}

	@Override
	public List<Statement> flatten(Scope sc) {
		List<Statement> a;

		a = statement != null ? statement.flatten(sc) : null;
		if (a != null) {
			for (int i = 0; i < a.size(); i++) {
				Statement s = (Statement) a.get(i);

				s = new VolatileStatement(loc, s);
				a.set(i, s);
			}
		}

		return a;
	}

	@Override
	public boolean fallOffEnd() {
		return statement != null ? statement.fallOffEnd() : true;
	}

	@Override
	public Statement syntaxCopy() {
		VolatileStatement s = new VolatileStatement(loc,
				statement != null ? statement.syntaxCopy() : null);
		return s;
	}

}
