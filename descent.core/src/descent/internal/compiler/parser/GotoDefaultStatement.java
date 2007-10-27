package descent.internal.compiler.parser;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class GotoDefaultStatement extends Statement {

	public SwitchStatement sw;

	public GotoDefaultStatement(Loc loc) {
		super(loc);
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
		}
		visitor.endVisit(this);
	}

	@Override
	public boolean fallOffEnd(SemanticContext context) {
		return false;
	}

	@Override
	public int getNodeType() {
		return GOTO_DEFAULT_STATEMENT;
	}

	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		// START()
		if (istate.start != null) {
			if (istate.start != this) {
				return null;
			}
			istate.start = null;
		}
		// START()
		if (!(sw != null && sw.sdefault != null)) {
			throw new IllegalStateException(
					"assert(sw != null && sw.sdefault != null);");
		}

		istate.gotoTarget = sw.sdefault;
		return EXP_GOTO_INTERPRET;
	}

	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		sw = sc.sw;
		if (null == sw) {
			context.acceptProblem(Problem.newSemanticTypeError(IProblem.GotoDefaultNotInSwitch, this));
		}
		return this;
	}

	@Override
	public Statement syntaxCopy() {
		GotoDefaultStatement s = new GotoDefaultStatement(loc);
		return s;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("goto default;\n");
	}

}
