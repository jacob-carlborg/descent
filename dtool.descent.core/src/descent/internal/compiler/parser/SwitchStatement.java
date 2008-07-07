package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.Constfold.Equal;
import static descent.internal.compiler.parser.TOK.TOKequal;

import static descent.internal.compiler.parser.TY.Tarray;

// DMD 1.020
public class SwitchStatement extends Statement {

	public Expression condition;
	public Statement body;
	public DefaultStatement sdefault;
	public List gotoCases; // array of unresolved GotoCaseStatement's
	public List cases; // array of CaseStatement's
	public int hasNoDefault; // !=0 if no default statement

	public SwitchStatement(Loc loc, Expression c, Statement b) {
		super(loc);
		this.condition = c;
		this.body = b;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, condition);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}

	@Override
	public boolean fallOffEnd(SemanticContext context) {
		if (body != null) {
			body.fallOffEnd(context);
		}
		return true; // need to do this better
	}

	@Override
	public int getNodeType() {
		return SWITCH_STATEMENT;
	}

	@Override
	public boolean hasBreak() {
		return true;
	}

	@Override
	public Statement inlineScan(InlineScanState iss, SemanticContext context) {
		condition = condition.inlineScan(iss, context);
		body = body != null ? body.inlineScan(iss, context) : null;
		if (sdefault != null) {
			sdefault = (DefaultStatement) sdefault.inlineScan(iss, context);
		}
		if (cases != null) {
			for (int i = 0; i < cases.size(); i++) {
				Statement s;

				s = (Statement) cases.get(i);
				cases.set(i, s.inlineScan(iss, context));
			}
		}
		return this;
	}

	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		if (istate.start == this) {
			istate.start = null;
		}
		Expression e = null;

		if (istate.start != null) {
			e = body != null ? body.interpret(istate, context) : null;
			if (istate.start != null) {
				return null;
			}
			if (e == EXP_CANT_INTERPRET) {
				return e;
			}
			if (e == EXP_BREAK_INTERPRET) {
				return null;
			}
			return e;
		}

		Expression econdition = condition.interpret(istate, context);
		if (econdition == EXP_CANT_INTERPRET) {
			return EXP_CANT_INTERPRET;
		}

		Statement s = null;
		if (cases != null) {
			for (int i = 0; i < cases.size(); i++) {
				CaseStatement cs = (CaseStatement) cases.get(i);
				e = Equal.call(TOKequal, Type.tint32, econdition, cs.exp,
						context);
				if (e == EXP_CANT_INTERPRET) {
					return EXP_CANT_INTERPRET;
				}
				if (e.isBool(true)) {
					s = cs;
					break;
				}
			}
		}
		if (null == s) {
			if (hasNoDefault != 0) {
				error("no default or case for %s in switch statement",
						econdition.toChars(context));
			}
			s = sdefault;
		}

		if (s == null) {
			throw new IllegalStateException("assert(s);");
		}
		istate.start = s;
		e = body != null ? body.interpret(istate, context) : null;
		if (istate.start != null) {
			throw new IllegalStateException("assert(!istate.start);");
		}
		if (e == EXP_BREAK_INTERPRET) {
			return null;
		}
		return e;
	}

	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		if (cases != null) {
			throw new IllegalStateException("assert(!cases);"); // ensure semantic() is only run once
		}
		condition = condition.semantic(sc, context);
		condition = resolveProperties(sc, condition, context);
		if (condition.type.isString(context)) {
			// If it's not an array, cast it to one
			if (condition.type.ty != Tarray) {
				condition = condition.implicitCastTo(sc, condition.type.next
						.arrayOf(context), context);
			}
		} else {
			condition = condition.integralPromotions(sc, context);
			condition.checkIntegral(context);
		}

		sc = sc.push();
		sc.sbreak = this;
		sc.sw = this;

		cases = new ArrayList();
		sc.noctor++; // BUG: should use Scope::mergeCallSuper() for each case instead
		body = body.semantic(sc, context);
		sc.noctor--;

		// Resolve any goto case's with exp
		Lfoundcase: for (int i = 0; i < gotoCases.size(); i++) {
			GotoCaseStatement gcs = (GotoCaseStatement) gotoCases.get(i);

			if (null == gcs.exp) {
				gcs.error("no case statement following goto case;");
				break;
			}

			for (Scope scx = sc; scx != null; scx = scx.enclosing) {
				if (null == scx.sw) {
					continue;
				}
				for (int j = 0; j < scx.sw.cases.size(); j++) {
					CaseStatement cs = (CaseStatement) scx.sw.cases.get(j);

					if (cs.exp.equals(gcs.exp)) {
						gcs.cs = cs;
						// goto Lfoundcase;
						continue Lfoundcase;
					}
				}
			}
			gcs.error("case %s not found", gcs.exp.toChars(context));
			// Lfoundcase: ;
		}

		if (null == sc.sw.sdefault) {
			hasNoDefault = 1;

			if (context.global.params.warnings) {
				error("warning - switch statement has no default");
			}

			// Generate runtime error if the default is hit
			List<Statement> a = new ArrayList<Statement>();
			CompoundStatement cs;
			Statement s;

			if (context.global.params.useSwitchError) {
				s = new SwitchErrorStatement(loc);
			} else {
				Expression e = new HaltExp(loc);
				s = new ExpStatement(loc, e);
			}

			a.add(body);
			a.add(new BreakStatement(loc, null));
			sc.sw.sdefault = new DefaultStatement(loc, s);
			a.add(sc.sw.sdefault);
			cs = new CompoundStatement(loc, a);
			body = cs;
		}

		sc.pop();
		return this;
	}

	@Override
	public Statement syntaxCopy() {
		SwitchStatement s = new SwitchStatement(loc, condition.syntaxCopy(),
				body.syntaxCopy());
		return s;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("switch (");
		condition.toCBuffer(buf, hgs, context);
		buf.writebyte(')');
		buf.writenl();
		if (body != null) {
			if (null == body.isScopeStatement()) {
				buf.writebyte('{');
				buf.writenl();
				body.toCBuffer(buf, hgs, context);
				buf.writebyte('}');
				buf.writenl();
			} else {
				body.toCBuffer(buf, hgs, context);
			}
		}
	}

	@Override
	public boolean usesEH() {
		return body != null ? body.usesEH() : false;
	}

}