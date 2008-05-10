package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.TOK.TOKint64;
import static descent.internal.compiler.parser.TOK.TOKstring;


public class CaseStatement extends Statement {

	public Expression exp, sourceExp;
	public Statement statement, sourceStatement;
	
	public SwitchStatement sw; // descent

	public CaseStatement(Loc loc, Expression exp, Statement s) {
		super(loc);
		this.exp = sourceExp = exp;
		this.statement = sourceStatement = s;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, sourceExp);
			TreeVisitor.acceptChildren(visitor, sourceStatement);
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
		this.sw = sw;

		exp = exp.semantic(sc, context);
		if (sw != null) {
			int i;

			exp = exp.implicitCastTo(sc, sw.condition.type, context);
			exp = exp.optimize(WANTvalue | WANTinterpret, context);
			if (exp.op != TOKstring && exp.op != TOKint64) {
				if (context.acceptsProblems()) {
					context.acceptProblem(Problem.newSemanticTypeError(IProblem.CaseMustBeAnIntegralOrStringConstant, sourceExp, new String[] { exp.toChars(context) }));
				}
				exp = new IntegerExp(0);
			}

			for (i = 0; i < sw.cases.size(); i++) {
				CaseStatement cs = (CaseStatement) sw.cases.get(i);

				//printf("comparing '%s' with '%s'\n", exp.toChars(), cs.exp.toChars());
				if (cs.exp.equals(exp)) {
					if (context.acceptsProblems()) {
						context.acceptProblem(Problem.newSemanticTypeErrorLoc(IProblem.DuplicateCaseInSwitchStatement, this, new String[] { exp.toChars(context) }));
					}
					break;
				}
			}

			sw.cases.add(this);

			// Resolve any goto case's with no exp to this case statement
			if (sw.gotoCases != null) {
				for (i = 0; i < sw.gotoCases.size(); i++) {
					GotoCaseStatement gcs = (GotoCaseStatement) sw.gotoCases.get(i);
	
					if (gcs.exp == null) {
						gcs.cs = this;
						sw.gotoCases.remove(i); // remove from array
					}
				}
			}
		} else {
			if (context.acceptsProblems()) {
				context.acceptProblem(Problem.newSemanticTypeError(IProblem.CaseIsNotInSwitch, this));
			}
		}
		statement = statement.semantic(sc, context);
		return this;
	}
	
	public void setStatement(Statement s) {
		this.statement = this.sourceStatement = s;
	}
	
	@Override
	public Statement syntaxCopy(SemanticContext context) {
		CaseStatement s = new CaseStatement(loc, exp.syntaxCopy(context), statement.syntaxCopy(context));
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
	
	@Override
	public int getErrorStart() {
		return start;
	}
	
	@Override
	public int getErrorLength() {
		return sourceExp.start + sourceExp.length - start;
	}

	

}
