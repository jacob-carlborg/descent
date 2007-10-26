package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.TOK.TOKassert;
import static descent.internal.compiler.parser.TOK.TOKhalt;

// DMD 1.020
public class ExpStatement extends Statement {

	public Expression exp, sourceExp;

	public ExpStatement(Loc loc, Expression exp) {
		super(loc);
		this.exp = exp;
		this.sourceExp = exp;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
		}
		visitor.endVisit(this);
	}

	@Override
	public Expression doInline(InlineDoState ids) {
		return exp != null ? exp.doInline(ids) : null;
	}

	@Override
	public boolean fallOffEnd(SemanticContext context) {
		if (exp != null) {
			if (exp.op == TOKassert) {
				AssertExp a = (AssertExp) exp;

				if (a.e1.isBool(false)) {
					return false;
				}
			} else if (exp.op == TOKhalt) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int getNodeType() {
		return EXP_STATEMENT;
	}

	@Override
	public int inlineCost(InlineCostState ics, SemanticContext context) {
		return exp != null ? exp.inlineCost(ics, context) : 0;
	}

	@Override
	public Statement inlineScan(InlineScanState iss, SemanticContext context) {
		if (exp != null) {
			exp = exp.inlineScan(iss, context);
		}
		return this;
	}

	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		// START()
		if (istate.start != null) {
			if (istate.start != this) {
				return null;
			}
			istate.start = null;
		}
		// START()
		if (exp != null) {
			Expression e = exp.interpret(istate, context);
			if (e == EXP_CANT_INTERPRET) {
				return EXP_CANT_INTERPRET;
			}
		}
		return null;
	}

	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		if (exp != null) {
			exp = exp.semantic(sc, context);
			exp = ASTDmdNode.resolveProperties(sc, exp, context);
			exp.copySourceRange(this);			
			exp.checkSideEffect(0, context);
			exp = exp.optimize(0, context);
		}
		
		return this;
	}

	@Override
	public Statement syntaxCopy() {
		Expression e = exp != null ? exp.syntaxCopy() : null;
		ExpStatement es = new ExpStatement(loc, e);
		return es;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		if (exp != null) {
			exp.toCBuffer(buf, hgs, context);
		}
		buf.writeByte(';');
		if (0 == hgs.FLinit.init) {
			buf.writenl();
		}
	}

}
