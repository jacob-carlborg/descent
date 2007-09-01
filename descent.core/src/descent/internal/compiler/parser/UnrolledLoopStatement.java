package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;
import melnorme.miscutil.Assert;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class UnrolledLoopStatement extends Statement {

	public List<Statement> statements;

	public UnrolledLoopStatement(Loc loc, List<Statement> statements) {
		super(loc);
		this.statements = statements;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		Assert.fail("Accept0 on a fake node");
	}

	@Override
	public boolean comeFrom() {
		boolean comefrom = false;

		for (int i = 0; i < statements.size(); i++) {
			Statement s = statements.get(i);

			if (null == s) {
				continue;
			}

			comefrom |= s.comeFrom();
		}
		return comefrom;
	}

	@Override
	public Expression doInline(InlineDoState ids) {
		Expression e = null;

		for (int i = 0; i < statements.size(); i++) {
			Statement s = statements.get(i);
			if (s != null) {
				Expression e2 = s.doInline(ids);
				e = Expression.combine(e, e2);
				if (s.isReturnStatement() != null) {
					break;
				}
			}
		}
		return e;
	}

	@Override
	public boolean fallOffEnd(SemanticContext context) {
		boolean falloff = true;

		for (int i = 0; i < statements.size(); i++) {
			Statement s = statements.get(i);

			if (s == null) {
				continue;
			}

			if (!falloff && context.global.params.warnings && !s.comeFrom()) {
				context
						.acceptProblem(Problem.newSemanticTypeWarning(
								IProblem.StatementIsNotReachable, 0, s.start,
								s.length));
			}
			falloff = s.fallOffEnd(context);
		}
		return falloff;
	}

	@Override
	public int getNodeType() {
		return UNROLLED_LOOP_STATEMENT;
	}

	@Override
	public boolean hasBreak() {
		return true;
	}

	@Override
	public boolean hasContinue() {
		return true;
	}

	@Override
	public int inlineCost(InlineCostState ics) {
		int cost = 0;

		for (int i = 0; i < statements.size(); i++) {
			Statement s = statements.get(i);
			if (s != null) {
				cost += s.inlineCost(ics);
				if (cost >= COST_MAX) {
					break;
				}
			}
		}
		return cost;
	}

	@Override
	public Statement inlineScan(InlineScanState iss) {
		for (int i = 0; i < statements.size(); i++) {
			Statement s = statements.get(i);
			if (s != null) {
				statements.set(i, s.inlineScan(iss));
			}
		}
		return this;
	}

	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		Expression e = null;

		if (istate.start == this) {
			istate.start = null;
		}
		if (statements != null) {
			for (int i = 0; i < statements.size(); i++) {
				Statement s = statements.get(i);

				e = s.interpret(istate, context);
				if (e == EXP_CANT_INTERPRET) {
					break;
				}
				if (e == EXP_CONTINUE_INTERPRET) {
					e = null;
					continue;
				}
				if (e == EXP_BREAK_INTERPRET) {
					e = null;
					break;
				}
				if (e != null) {
					break;
				}
			}
		}
		return e;
	}

	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		sc.noctor++;
		Scope scd = sc.push();
		scd.sbreak = this;
		scd.scontinue = this;

		for (int i = 0; i < statements.size(); i++) {
			Statement s = statements.get(i);
			if (s != null) {
				s = s.semantic(scd, context);
				statements.set(i, s);
			}
		}

		scd.pop();
		sc.noctor--;
		return this;
	}

	@Override
	public Statement syntaxCopy() {
		List<Statement> a = new ArrayList<Statement>(statements.size());
		for (int i = 0; i < statements.size(); i++) {
			Statement s = statements.get(i);
			if (s != null) {
				s = s.syntaxCopy();
			}
			a.add(s);
		}
		UnrolledLoopStatement cs = new UnrolledLoopStatement(loc, a);
		return cs;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("unrolled {");
		buf.writenl();

		for (int i = 0; i < statements.size(); i++) {
			Statement s;

			s = statements.get(i);
			if (s != null) {
				s.toCBuffer(buf, hgs, context);
			}
		}

		buf.writeByte('}');
		buf.writenl();
	}

	@Override
	public boolean usesEH() {
		for (int i = 0; i < statements.size(); i++) {
			Statement s;

			s = statements.get(i);
			if (s != null && s.usesEH()) {
				return true;
			}
		}
		return false;
	}

}
