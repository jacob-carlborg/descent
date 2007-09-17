package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class WhileStatement extends Statement {

	public Expression condition;
	public Statement body;

	public WhileStatement(Loc loc, Expression c, Statement b) {
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
		return WHILE_STATEMENT;
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
	public Statement inlineScan(InlineScanState iss, SemanticContext context) {
		condition = condition.inlineScan(iss, context);
		body = body != null ? body.inlineScan(iss, context) : null;
		return this;
	}

	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		if (istate.start == this) {
			istate.start = null;
		}
		Expression e;

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
			if (e != EXP_CONTINUE_INTERPRET) {
				return e;
			}
		}

		while (true) {
			e = condition.interpret(istate, context);
			if (e == EXP_CANT_INTERPRET) {
				break;
			}
			if (!e.isConst()) {
				e = EXP_CANT_INTERPRET;
				break;
			}
			if (e.isBool(true)) {
				e = body != null ? body.interpret(istate, context) : null;
				if (e == EXP_CANT_INTERPRET) {
					break;
				}
				if (e == EXP_CONTINUE_INTERPRET) {
					continue;
				}
				if (e == EXP_BREAK_INTERPRET) {
					e = null;
					break;
				}
				if (e != null) {
					break;
				}
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
		condition = condition.semantic(sc, context);
		condition = resolveProperties(sc, condition, context);
	    condition = condition.optimize(WANTvalue, context);
		condition = condition.checkToBoolean(context);

		sc.noctor++;

		Scope scd = sc.push();
		scd.sbreak = this;
		scd.scontinue = this;
		if (body != null) {
			body = body.semantic(scd, context);
		}
		scd.pop();

		sc.noctor--;

		return this;
	}

	@Override
	public Statement syntaxCopy() {
		WhileStatement s = new WhileStatement(loc, condition.syntaxCopy(),
				body != null ? body.syntaxCopy() : null);
		return s;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("while (");
		condition.toCBuffer(buf, hgs, context);
		buf.writebyte(')');
		buf.writenl();
		if (body != null) {
			body.toCBuffer(buf, hgs, context);
		}
	}

	@Override
	public boolean usesEH() {
		return body != null ? body.usesEH() : false;
	}

}
