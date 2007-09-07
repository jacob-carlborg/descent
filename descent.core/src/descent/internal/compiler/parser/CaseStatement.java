package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.TOK.TOKint64;
import static descent.internal.compiler.parser.TOK.TOKstring;

// DMD 1.020
public class CaseStatement extends Statement {

	public Expression exp;
	public Statement statement;

	public CaseStatement(Loc loc, Expression exp, Statement s) {
		super(loc);
		this.exp = exp;
		this.statement = s;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
			TreeVisitor.acceptChildren(visitor, statement);
		}
		visitor.endVisit(this);
	}

	@Override
	public boolean comeFrom() {
		return true;
	}

	public boolean compare(ASTDmdNode obj) {
		if (!(obj instanceof CaseStatement)) {
			return false;
		}
		
		// Sort cases so we can do an efficient lookup
	    CaseStatement cs2 = (CaseStatement) obj;
	    return exp.compare(cs2.exp);
	}

	@Override
	public boolean fallOffEnd(SemanticContext context) {
		return statement.fallOffEnd(context);
	}

	@Override
	public int getNodeType() {
		return CASE_STATEMENT;
	}
	
	@Override
	public Statement inlineScan(InlineScanState iss, SemanticContext context) {
		exp = exp.inlineScan(iss, context);
		if (statement != null) {
			statement = statement.inlineScan(iss, context);
		}
		return this;
	}
	
	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		if (istate.start == this) {
			istate.start = null;
		}
		if (statement != null) {
			return statement.interpret(istate, context);
		} else {
			return null;
		}
	}
	
	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		SwitchStatement sw = sc.sw;

		exp = exp.semantic(sc, context);
		if (sw != null) {
			int i;

			exp = exp.implicitCastTo(sc, sw.condition.type, context);
			exp = exp.optimize(WANTvalue | WANTinterpret, context);
			if (exp.op != TOKstring && exp.op != TOKint64) {
				error("case must be a string or an integral constant, not %s",
						exp.toChars(context));
				exp = new IntegerExp(0);
			}

			for (i = 0; i < sw.cases.size(); i++) {
				CaseStatement cs = (CaseStatement) sw.cases.get(i);

				//printf("comparing '%s' with '%s'\n", exp.toChars(), cs.exp.toChars());
				if (cs.exp.equals(exp)) {
					error("duplicate case %s in switch statement", exp
							.toChars(context));
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
	
	@Override
	public Statement syntaxCopy() {
		CaseStatement s = new CaseStatement(loc, exp.syntaxCopy(), statement.syntaxCopy());
	    return s;
	}
	
	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		buf.writestring("case ");
	    exp.toCBuffer(buf, hgs, context);
	    buf.writebyte(':');
	    buf.writenl();
	    statement.toCBuffer(buf, hgs, context);
	}
	
	@Override
	public boolean usesEH() {
		return statement.usesEH();
	}

}
