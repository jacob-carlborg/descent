package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class TryFinallyStatement extends Statement {

	public Statement body, sourceBody;
	public Statement finalbody, sourceFinalBody;
	public boolean isTryCatchFinally;

	public TryFinallyStatement(Loc loc, Statement body, Statement finalbody) {
		this(loc, body, finalbody, false);
	}

	public TryFinallyStatement(Loc loc, Statement body, Statement finalbody,
			boolean isTryCatchFinally) {
		super(loc);
		this.body = this.sourceBody = body;
		this.finalbody = this.sourceFinalBody = finalbody;
		this.isTryCatchFinally = isTryCatchFinally;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, sourceBody);
			TreeVisitor.acceptChildren(visitor, sourceFinalBody);
		}
		visitor.endVisit(this);
	}

	@Override
	public boolean fallOffEnd(SemanticContext context) {
		boolean result;
		result = body.fallOffEnd(context);
		return result;
	}

	@Override
	public int getNodeType() {
		return TRY_FINALLY_STATEMENT;
	}

	@Override
	public boolean hasBreak() {
		return false;
	}

	@Override
	public boolean hasContinue() {
		return false;
	}

	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		body = body.semantic(sc, context);
		sc = sc.push();
		sc.tf = this;
		sc.sbreak = null;
		sc.scontinue = null; // no break or continue out of finally block
		finalbody = finalbody.semantic(sc, context);
		sc.pop();
		return this;
	}

	@Override
	public Statement syntaxCopy(SemanticContext context) {
		TryFinallyStatement s = new TryFinallyStatement(loc, body.syntaxCopy(context),
				finalbody.syntaxCopy(context));
		return s;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.printf("try\n{\n");
		body.toCBuffer(buf, hgs, context);
		buf.printf("}\nfinally\n{\n");
		finalbody.toCBuffer(buf, hgs, context);
		buf.writeByte('}');
		buf.writenl();
	}

	@Override
	public boolean usesEH() {
		return true;
	}

}
