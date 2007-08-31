package descent.internal.compiler.parser;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class VolatileStatement extends Statement {

	public Statement statement;
	public Statement sourceStatement;

	public VolatileStatement(Loc loc, Statement statement) {
		super(loc);
		this.sourceStatement = statement;
		this.statement = statement;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, sourceStatement);
		}
		visitor.endVisit(this);
	}

	@Override
	public boolean fallOffEnd() {
		return statement != null ? statement.fallOffEnd() : true;
	}

	@Override
	public List<Statement> flatten(Scope sc) {
		List<Statement> a;

		a = statement != null ? statement.flatten(sc) : null;
		if (a != null) {
			for (int i = 0; i < a.size(); i++) {
				Statement s = a.get(i);

				s = new VolatileStatement(loc, s);
				a.set(i, s);
			}
		}

		return a;
	}

	@Override
	public int getNodeType() {
		return VOLATILE_STATEMENT;
	}

	@Override
	public Statement inlineScan(InlineScanState iss) {
		if (statement != null) {
			statement = statement.inlineScan(iss);
		}
		return this;
	}

	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		statement = statement != null ? statement.semantic(sc, context) : null;
		return this;
	}

	@Override
	public Statement syntaxCopy() {
		VolatileStatement s = new VolatileStatement(loc,
				statement != null ? statement.syntaxCopy() : null);
		return s;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("volatile");
		if (statement != null) {
			if (statement.isScopeStatement() != null) {
				buf.writenl();
			} else {
				buf.writebyte(' ');
			}
			statement.toCBuffer(buf, hgs, context);
		}
	}

}
