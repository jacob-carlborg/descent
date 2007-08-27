package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.TOK.*;

public class CaseStatement extends Statement {

	public Expression exp;
	public Statement statement;

	public CaseStatement(Loc loc, Expression exp, Statement s) {
		super(loc);
		this.exp = exp;
		this.statement = s;
	}

	@Override
	public int getNodeType() {
		return CASE_STATEMENT;
	}

	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
			TreeVisitor.acceptChildren(visitor, statement);
		}
		visitor.endVisit(this);
	}

	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		SwitchStatement sw = sc.sw;

		exp = exp.semantic(sc, context);
		if (sw != null) {
			int i;

			exp = exp.implicitCastTo(sc, sw.condition.type, context);
			exp = exp.optimize(WANTvalue | WANTinterpret);
			if (exp.op != TOKstring && exp.op != TOKint64) {
				error("case must be a string or an integral constant, not %s",
						exp.toChars());
				exp = new IntegerExp(0);
			}

			for (i = 0; i < sw.cases.size(); i++) {
				CaseStatement cs = (CaseStatement) sw.cases.get(i);

				//printf("comparing '%s' with '%s'\n", exp.toChars(), cs.exp.toChars());
				if (cs.exp.equals(exp)) {
					error("duplicate case %s in switch statement", exp
							.toChars());
					break;
				}
			}

			sw.cases.add(this);

			// Resolve any goto case's with no exp to this case statement
			for (i = 0; i < sw.gotoCases.size(); i++) {
				GotoCaseStatement gcs = (GotoCaseStatement) sw.gotoCases.get(i);

				if (gcs.exp == null) {
					gcs.cs = this;
					sw.gotoCases.remove(i); // remove from array
				}
			}
		} else {
			error("case not in switch statement");
		}
		statement = statement.semantic(sc, context);
		return this;
	}

}
