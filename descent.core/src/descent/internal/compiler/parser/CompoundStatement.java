package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class CompoundStatement extends Statement {

	// Move to another class, maybe SemanticContext?
	public static int num;

	public boolean manyVars; 	// if true, the block is just to group variable declarations,
								// alias declarations or typedef declarations
	public List<Statement> statements;

	public List<Statement> sourceStatements;

	public CompoundStatement(Loc loc, List<Statement> statements) {
		super(loc);
		this.statements = statements;
		if (statements != null) {
			this.sourceStatements = new ArrayList<Statement>(statements);
		}
	}

	public CompoundStatement(Loc loc, Statement s1, Statement s2) {
		super(loc);
		this.statements = new ArrayList<Statement>(2);
		this.statements.add(s1);
		this.statements.add(s2);
		this.sourceStatements = new ArrayList<Statement>(statements);
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, sourceStatements);
		}
		visitor.endVisit(this);
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

				/* Check for:
				 *	if (condition)
				 *	    return exp1;
				 *	else
				 *	    return exp2;
				 */
				IfStatement ifs = s.isIfStatement();
				if (ifs != null && ifs.elsebody != null && ifs.ifbody != null
						&& ifs.ifbody.isReturnStatement() != null
						&& ifs.elsebody.isReturnStatement() != null) {
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

			if (null == s) {
				continue;
			}

			if (!falloff && context.global.params.warnings && !s.comeFrom()) {
				s.error("warning - statement is not reachable");
			}
			falloff = s.fallOffEnd(context);
		}
		return falloff;
	}

	@Override
	public List<Statement> flatten(Scope sc) {
		return statements;
	}

	@Override
	public int getNodeType() {
		return COMPOUND_STATEMENT;
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
	public Statement inlineScan(InlineScanState iss, SemanticContext context) {
		for (int i = 0; i < statements.size(); i++) {
			Statement s = statements.get(i);
			if (s != null) {
				statements.set(i, s.inlineScan(iss, context));
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

				if (s != null) {
					e = s.interpret(istate, context);
					if (e != null) {
						break;
					}
				}
			}
		}
		return e;
	}

	@Override
	public ReturnStatement isReturnStatement() {
		int i;
		ReturnStatement rs = null;

		for (i = 0; i < statements.size(); i++) {
			Statement s;

			s = statements.get(i);
			if (s != null) {
				rs = s.isReturnStatement();
				if (rs != null) {
					break;
				}
			}
		}
		return rs;
	}

	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		Statement s = null;

		if (statements != null) {
			for (int i = 0; i < statements.size();) {
				s = statements.get(i);
				if (s != null) {
					List<Statement> a = s.flatten(sc);

					if (a != null) {
						statements.remove(i);
						statements.addAll(i, a);
						continue;
					}
					s = s.semantic(sc, context);
					statements.set(i, s);
					if (s != null) {
						Statement[] sentry = { null };
						Statement[] sexception = { null };
						Statement[] sfinally = { null };

						s.scopeCode(sentry, sexception, sfinally);
						if (sentry[0] != null) {
							sentry[0] = sentry[0].semantic(sc, context);
							statements.set(i, sentry[0]);
						}
						if (sexception[0] != null) {
							if (i + 1 == statements.size()
									&& sfinally[0] == null) {
								sexception[0] = sexception[0].semantic(sc,
										context);
							} else {
								/*
								 * Rewrite: s; s1; s2; As: s; try { s1; s2; } catch
								 * (Object __o) { sexception; throw __o; }
								 */
								Statement body;
								List<Statement> a2 = new ArrayList<Statement>();

								for (int j = i + 1; j < statements.size(); j++) {
									a2.add(statements.get(j));
								}
								body = new CompoundStatement(loc, a2);
								body = new ScopeStatement(loc, body);

								char[] id = ("__o" + ++num).toCharArray();

								Statement handler = new ThrowStatement(loc,
										new IdentifierExp(loc, id));
								handler = new CompoundStatement(loc,
										sexception[0], handler);

								List catches = new ArrayList();
								Catch ctch = new Catch(loc, null,
										new IdentifierExp(loc, id), handler);
								catches.add(ctch);
								s = new TryCatchStatement(loc, body, catches);

								if (sfinally[0] != null) {
									s = new TryFinallyStatement(loc, s,
											sfinally[0]);
								}
								s = s.semantic(sc, context);
								// statements..setDim(i + 1);
								statements.add(s);
								break;
							}
						} else if (sfinally[0] != null) {
							if (false && i + 1 == statements.size()) {
								statements.add(sfinally[0]);
							} else {
								/*
								 * Rewrite: s; s1; s2; As: s; try { s1; s2; }
								 * finally { sfinally; }
								 */
								Statement body;
								List<Statement> a2 = new ArrayList<Statement>();

								for (int j = i + 1; j < statements.size(); j++) {
									a2.add(statements.get(j));
								}
								body = new CompoundStatement(loc, a2);
								s = new TryFinallyStatement(loc, body,
										sfinally[0]);
								s = s.semantic(sc, context);
								// statements.setDim(i + 1);
								statements.add(s);
								break;
							}
						}
					}
				}
				i++;
			}
		}
		if (statements != null && statements.size() == 1) {
			return s;
		}
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
			a.set(i, s);
		}
		CompoundStatement cs = new CompoundStatement(loc, a);
		return cs;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		int i;

		for (i = 0; i < statements.size(); i++) {
			Statement s;

			s = statements.get(i);
			if (s != null) {
				s.toCBuffer(buf, hgs, context);
			}
		}
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
