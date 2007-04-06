package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.Scope.CSXany_ctor;
import static descent.internal.compiler.parser.Scope.CSXreturn;
import static descent.internal.compiler.parser.Scope.CSXsuper_ctor;
import static descent.internal.compiler.parser.Scope.CSXthis_ctor;
import static descent.internal.compiler.parser.TOK.TOKcomplex80;
import static descent.internal.compiler.parser.TOK.TOKfloat64;
import static descent.internal.compiler.parser.TOK.TOKimaginary80;
import static descent.internal.compiler.parser.TOK.TOKint64;
import static descent.internal.compiler.parser.TOK.TOKnull;
import static descent.internal.compiler.parser.TOK.TOKstring;
import static descent.internal.compiler.parser.TOK.TOKsuper;
import static descent.internal.compiler.parser.TOK.TOKthis;
import static descent.internal.compiler.parser.TOK.TOKvar;
import static descent.internal.compiler.parser.TY.Tvoid;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;

public class ReturnStatement extends Statement {

	public Expression exp;
	public Expression sourceExp;

	public ReturnStatement(Expression exp) {
		this.exp = exp;		
		this.sourceExp = exp;
	}
	
	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		FuncDeclaration fd = sc.parent.isFuncDeclaration();
		Scope scx = sc;
		int implicit0 = 0;

		if (sc.fes != null) {
			// Find scope of function foreach is in
			for (; true; scx = scx.enclosing) {
				Assert.isNotNull(scx);
				if (scx.func != fd) {
					fd = scx.func; // fd is now function enclosing foreach
					break;
				}
			}
		}

		Type tret = fd.type.next;
		if (fd.tintro != null) {
			tret = fd.tintro.next;
		}
		Type tbret = null;

		if (tret != null) {
			tbret = tret.toBasetype(context);
		}

		// main() returns 0, even if it returns void
		if (exp == null && (tbret == null || tbret.ty == Tvoid) && fd.isMain()) {
			implicit0 = 1;
			exp = new IntegerExp(0);
		}

		if (sc.incontract != 0 || scx.incontract != 0) {
			context.acceptProblem(Problem.newSemanticTypeError("Return statements cannot be in contracts", IProblem.ReturnStatementsCannotBeInContracts, 0, start, length));
		}
		if (sc.tf != null || scx.tf != null) {
			error("return statements cannot be in finally, scope(exit) or scope(success) bodies");
		}

		if (fd.isCtorDeclaration() != null) {
			// Constructors implicitly do:
			//	return this;
			if (exp != null && exp.op != TOKthis) {
				error("cannot return expression from constructor");
			}
			exp = new ThisExp();
		}

		if (exp == null)
			 fd.nrvo_can = 0;

			if (exp != null) {
				fd.hasReturnExp |= 1;

				exp = exp.semantic(sc, context);
				exp = resolveProperties(sc, exp, context);
				exp = exp.optimize(WANTvalue);

				if (fd.nrvo_can != 0 && exp.op == TOKvar) {
					VarExp ve = (VarExp) exp;
					VarDeclaration v = ve.var.isVarDeclaration();

					if (v == null || v.isOut()) {
						fd.nrvo_can = 0;
					}
					else if (fd.nrvo_var == null) {
						if (!v.isDataseg(context) && !v.isParameter()
								&& v.toParent2() == fd) {
							fd.nrvo_var = v;
						} else {
							fd.nrvo_can = 0;
						}
					} else if (fd.nrvo_var != v) {
						fd.nrvo_can = 0;
					}
				}

				if (fd.returnLabel != null && tbret.ty != Tvoid) {
				} else if (fd.inferRetType) {
					if (fd.type.next != null) {
						if (!exp.type.equals(fd.type.next))
							error(
									"mismatched function return type inference of %s and %s",
									exp.type.toChars(), fd.type.next.toChars());
					} else {
						fd.type.next = exp.type;
						fd.type = fd.type.semantic(sc, context);
						if (fd.tintro == null) {
							tret = fd.type.next;
							tbret = tret.toBasetype(context);
						}
					}
				} else if (tbret.ty != Tvoid) {
					exp = exp.implicitCastTo(sc, tret, context);
				}
			} else if (fd.inferRetType) {
				if (fd.type.next != null) {
					if (fd.type.next.ty != Tvoid)
						error(
								"mismatched function return type inference of void and %s",
								fd.type.next.toChars());
				} else {
					fd.type.next = Type.tvoid;
					fd.type = fd.type.semantic(sc, context);
					if (fd.tintro == null) {
						tret = Type.tvoid;
						tbret = tret;
					}
				}
			} else if (tbret.ty != Tvoid) { // if non-void return
				error("return expression expected");
			}

		if (sc.fes != null) {
			Statement s;

			if (exp != null && implicit0 == 0) {
				exp = exp.implicitCastTo(sc, tret, context);
			}
			if (exp == null || exp.op == TOKint64 || exp.op == TOKfloat64
					|| exp.op == TOKimaginary80 || exp.op == TOKcomplex80
					|| exp.op == TOKthis || exp.op == TOKsuper
					|| exp.op == TOKnull || exp.op == TOKstring) {
				sc.fes.cases.add(this);
				s = new ReturnStatement(new IntegerExp(sc.fes.cases.size() + 1));
			} else if (fd.type.next.toBasetype(context) == Type.tvoid) {
				Statement s1;
				Statement s2;

				s = new ReturnStatement(null);
				sc.fes.cases.add(s);

				// Construct: { exp; return cases.dim + 1; }
				s1 = new ExpStatement(exp);
				s2 = new ReturnStatement(
						new IntegerExp(sc.fes.cases.size() + 1));
				s = new CompoundStatement(s1, s2);
			} else {
				VarExp v;
				Statement s1;
				Statement s2;

				// Construct: return vresult;
				if (fd.vresult == null) {
					VarDeclaration v2;

					v2 = new VarDeclaration(tret, Id.result, null);
					v2.noauto = true;
					v2.semantic(scx, context);
					if (scx.insert(v2) == null) {
						org.eclipse.jface.text.Assert.isTrue(false);
					}
					v2.parent = fd;
					fd.vresult = v2;
				}

				v = new VarExp(fd.vresult);
				s = new ReturnStatement(v);
				sc.fes.cases.add(s);

				// Construct: { vresult = exp; return cases.dim + 1; }
				v = new VarExp(fd.vresult);
				exp = new AssignExp(v, exp);
				exp = exp.semantic(sc, context);
				s1 = new ExpStatement(exp);
				s2 = new ReturnStatement(
						new IntegerExp(sc.fes.cases.size() + 1));
				s = new CompoundStatement(s1, s2);
			}
			return s;
		}

		if (exp != null) {
			if (fd.returnLabel != null && tbret.ty != Tvoid) {
				Assert.isNotNull(fd.vresult);
				VarExp v = new VarExp(fd.vresult);

				exp = new AssignExp(v, exp);
				exp = exp.semantic(sc, context);
			}
			//exp.dump(0);
			//exp.print();
			exp.checkEscape(context);
		}

		/* BUG: need to issue an error on:
		 *	this
		 *	{   if (x) return;
		 *	    super();
		 *	}
		 */

		if ((sc.callSuper & CSXany_ctor) != 0
				&& (sc.callSuper & (CSXthis_ctor | CSXsuper_ctor)) == 0) {
			error("return without calling constructor");
		}

		sc.callSuper |= CSXreturn;

		// See if all returns are instead to be replaced with a goto returnLabel;
		if (fd.returnLabel != null) {
			GotoStatement gs = new GotoStatement(new IdentifierExp(
					Id.returnLabel));

			gs.label = fd.returnLabel;
			if (exp != null) {
				Statement s;

				s = new ExpStatement(exp);
				return new CompoundStatement(s, gs);
			}
			return gs;
		}

		if (exp != null && tbret.ty == Tvoid && !fd.isMain()) {
			Statement s;

			s = new ExpStatement(exp);
			exp = null;
			return new CompoundStatement(s, this);
		}

		return this;
	}
	
	@Override
	public int getNodeType() {
		return RETURN_STATEMENT;
	}

}
