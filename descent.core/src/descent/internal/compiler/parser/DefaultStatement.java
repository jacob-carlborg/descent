package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class DefaultStatement extends Statement {

	public Statement statement;
	public Statement sourceStatement;

	public DefaultStatement(Loc loc, Statement s) {
		super(loc);
		this.statement = s;
		this.sourceStatement = s;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, statement);
		}
		visitor.endVisit(this);
	}

	@Override
	public boolean comeFrom() {
		return true;
	}

	@Override
	public boolean fallOffEnd(SemanticContext context) {
		return statement.fallOffEnd(context);
	}

	@Override
	public int getNodeType() {
		return DEFAULT_STATEMENT;
	}

	@Override
	public Statement inlineScan(InlineScanState iss, SemanticContext context) {
		if (statement != null) {
			statement = statement.inlineScan(iss, context);
		}
		return this;
	}

	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		if (istate.start == this) {
			istate.start = null;
		}
		if (statement != null) {
			return statement.interpret(istate, context);
		} else {
			return null;
		}
	}

	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		if (sc.sw != null) {
			if (sc.sw.sdefault != null) {
				context.acceptProblem(Problem.newSemanticTypeError(IProblem.SwitchAlreadyHasDefault, 0, start, length));
			}
			sc.sw.sdefault = this;
		} else {
			context.acceptProblem(Problem.newSemanticTypeError(IProblem.DefaultNotInSwitch, 0, start, length));
		}
		
		statement = statement.semantic(sc, context);
		return this;
	}

	@Override
	public Statement syntaxCopy() {
		DefaultStatement s = new DefaultStatement(loc, statement.syntaxCopy());
		return s;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("default:\n");
		statement.toCBuffer(buf, hgs, context);
	}

	@Override
	public boolean usesEH() {
		return statement.usesEH();
	}

}
