package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

public class CompoundStatement extends Statement {
	
	public List<Statement> statements;

	public CompoundStatement(List<Statement> statements) {
		this.statements = statements;
	}
	
	public CompoundStatement(Statement s1, Statement s2) {
		this.statements = new ArrayList<Statement>(2);
		this.statements.add(s1);
		this.statements.add(s2);
	}
	
	@Override
	public List<Statement> flatten(Scope sc) {
		return statements;
	}
	
	// Move to another class, maybe SemanticContext?
	public static int num;
	
	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		Statement s = null;

		if (statements != null) {
			for (int i = 0; i < statements.size();) {
				s = (Statement) statements.get(i);
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
							if (i + 1 == statements.size() && sfinally[0] == null) {
								sexception[0] = sexception[0].semantic(sc, context);
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
								body = new CompoundStatement(a2);
								body = new ScopeStatement(body);
	
								Identifier id = new Identifier("__o" + ++num,
										TOK.TOKidentifier);
	
								Statement handler = new ThrowStatement(
										new IdentifierExp(id));
								handler = new CompoundStatement(sexception[0],
										handler);
	
								List catches = new ArrayList();
								Catch ctch = new Catch(null, new IdentifierExp(id),
										handler);
								catches.add(ctch);
								s = new TryCatchStatement(body, catches);
	
								if (sfinally[0] != null) {
									s = new TryFinallyStatement(s, sfinally[0]);
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
								body = new CompoundStatement(a2);
								s = new TryFinallyStatement(body, sfinally[0]);
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
	public int getNodeType() {
		return COMPOUND_STATEMENT;
	}

}
