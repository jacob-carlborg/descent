package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;


public class DoStatement extends Statement {

	public Expression condition;
	public Statement body;

	public DoStatement(Loc loc, Statement b, Expression c) {
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
	public boolean comeFrom() {
		if (body != null) {
			return body.comeFrom();
		}
		return false;
	}

	@Override
	public boolean fallOffEnd(SemanticContext context) {
		if (body != null) {
			body.fallOffEnd(context);
		}
		return true;
	}

	@Override
	public int getNodeType() {
		return DO_STATEMENT;
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
	public Expression interpret(InterState istate, SemanticContext context) {
		if (istate.start == this) {
			istate.start = null;
		}
		Expression e;

		boolean gotoLcontinue = false;
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
			if (e == EXP_CONTINUE_INTERPRET) {
				// goto Lcontinue;
				gotoLcontinue = true;
			}
			if (e != null && !gotoLcontinue) {
				return e;
			}
		}

		while (true) {
			if (!gotoLcontinue) {
				e = body != null ? body.interpret(istate, context) : null;
				if (e == EXP_CANT_INTERPRET) {
					break;
				}
				if (e == EXP_BREAK_INTERPRET) {
					e = null;
					break;
				}
				if (e != null && e != EXP_CONTINUE_INTERPRET) {
					break;
				}
			}

			gotoLcontinue = false;
			// Lcontinue: 
			e = condition.interpret(istate, context);
			if (e == EXP_CANT_INTERPRET) {
				break;
			}
			if (!e.isConst()) {
				e = EXP_CANT_INTERPRET;
				break;
			}
			if (e.isBool(true)) {
			} else if (e.isBool(false)) {
				e = null;
				break;
			} else {
				throw new IllegalStateException("assert(0);");
			}
		}
		return e;
	}

	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		sc.noctor++;
		if (body != null) {
			body = body.semanticScope(sc, this, this, context);
		}
		sc.noctor--;
		condition = condition.semantic(sc, context);
		condition = resolveProperties(sc, condition, context);
	    condition = condition.optimize(WANTvalue, context);

		condition = condition.checkToBoolean(context);

		return this;
	}

	@Override
	public Statement syntaxCopy(SemanticContext context) {
		DoStatement s = new DoStatement(loc, body != null ? body.syntaxCopy(context)
				: null, condition.syntaxCopy(context));
		return s;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("do");
		buf.writenl();
		if (body != null) {
			body.toCBuffer(buf, hgs, context);
		}
		buf.writestring("while (");
		condition.toCBuffer(buf, hgs, context);
		buf.writebyte(')');
	}

	@Override
	public boolean usesEH() {
		return body != null ? body.usesEH() : false;
	}

}
