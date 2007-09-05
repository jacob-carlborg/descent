package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class TryCatchStatement extends Statement {

	public Statement body;
	public List<Catch> catches;

	public TryCatchStatement(Loc loc, Statement body, List<Catch> catches) {
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
	public Statement inlineScan(InlineScanState iss) {
		if (body != null) {
			body = body.inlineScan(iss);
		}
		if (catches != null) {
			for (int i = 0; i < catches.size(); i++) {
				Catch c = catches.get(i);

				if (c.handler != null) {
					c.handler = c.handler.inlineScan(iss);
				}
			}
		}
		return this;
	}

	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		body = body.semanticScope(sc, null /*this*/, null, context);

		for (int i = 0; i < catches.size(); i++) {
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
					error("catch at %s hides catch at %s", sj, si);
				}
			}
		}
		return this;
	}

	@Override
	public Statement syntaxCopy() {
		List<Catch> a = new ArrayList<Catch>(catches.size());
		for (int i = 0; i < a.size(); i++) {
			Catch c;

			c = catches.get(i);
			c = c.syntaxCopy();
			a.add(c);
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
			c.toCBuffer(buf, hgs);
		}
	}

	@Override
	public boolean usesEH() {
		return true;
	}

}
