package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class TryCatchStatement extends Statement {

	public Statement body;
	public Array<Catch> catches;

	public TryCatchStatement(Loc loc, Statement body, Array<Catch> catches) {
		super(loc);
		this.body = body;
		this.catches = catches;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, body);
			TreeVisitor.acceptChildren(visitor, catches);
		}
		visitor.endVisit(this);
	}

	@Override
	public boolean fallOffEnd(SemanticContext context) {
		boolean result = false;

		if (body != null) {
			result = body.fallOffEnd(context);
		}
		for (int i = 0; i < catches.size(); i++) {
			Catch c;

			c = catches.get(i);
			if (c.handler != null) {
				result |= c.handler.fallOffEnd(context);
			}
		}
		return result;
	}

	@Override
	public int getNodeType() {
		return TRY_CATCH_STATEMENT;
	}

	@Override
	public boolean hasBreak() {
		return false;
	}

	@Override
	public Statement inlineScan(InlineScanState iss, SemanticContext context) {
		if (body != null) {
			body = body.inlineScan(iss, context);
		}
		if (catches != null) {
			for (int i = 0; i < catches.size(); i++) {
				Catch c = catches.get(i);

				if (c.handler != null) {
					c.handler = c.handler.inlineScan(iss, context);
				}
			}
		}
		return this;
	}

	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		body = body.semanticScope(sc, null /*this*/, null, context);

		for (int i = 0; i < size(catches); i++) {
			Catch c;

			c = catches.get(i);
			c.semantic(sc, context);

			// Determine if current catch 'hides' any previous catches
			for (int j = 0; j < i; j++) {
				Catch cj = catches.get(j);
				String si = c.loc.toChars();
				String sj = cj.loc.toChars();

				if (c.type.toBasetype(context).implicitConvTo(
						cj.type.toBasetype(context), context) != null) {
					context.acceptProblem(Problem.newSemanticTypeError(IProblem.CatchHidesCatch, 0, cj.start, 5 /* "catch".length() */, new String[] { sj, si }));
				}
			}
		}
		return this;
	}

	@Override
	public Statement syntaxCopy() {
		Array<Catch> a = new Array<Catch>();
		a.setDim(catches.size());
		for (int i = 0; i < a.size(); i++) {
			Catch c;

			c = catches.get(i);
			c = c.syntaxCopy();
			a.set(i, c);
		}
		TryCatchStatement s = new TryCatchStatement(loc, body.syntaxCopy(), a);
		return s;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("try");
		buf.writenl();
		if (body != null) {
			body.toCBuffer(buf, hgs, context);
		}
		int i;
		for (i = 0; i < catches.size(); i++) {
			Catch c = catches.get(i);
			c.toCBuffer(buf, hgs, context);
		}
	}

	@Override
	public boolean usesEH() {
		return true;
	}

}
