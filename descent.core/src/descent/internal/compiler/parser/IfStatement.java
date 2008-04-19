package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;

import org.eclipse.core.runtime.Assert;

import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class IfStatement extends Statement {

	public Argument arg;
	public Expression condition, sourceCondition;
	public Statement ifbody, sourceIfbody;
	public Statement elsebody, sourceElsebody;

	public VarDeclaration match; // for MatchExpression results

	public IfStatement(Loc loc, Argument arg, Expression condition,
			Statement ifbody, Statement elsebody) {
		super(loc);
		this.arg = arg;
		this.condition = this.sourceCondition = condition;
		this.ifbody = this.sourceIfbody = ifbody;
		this.elsebody = this.sourceElsebody = elsebody;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, arg);
			TreeVisitor.acceptChildren(visitor, sourceCondition);
			TreeVisitor.acceptChildren(visitor, sourceIfbody);
			TreeVisitor.acceptChildren(visitor, sourceElsebody);
		}
		visitor.endVisit(this);
	}

	@Override
	public boolean fallOffEnd(SemanticContext context) {
		if (null == ifbody || ifbody.fallOffEnd(context) || null == elsebody
				|| elsebody.fallOffEnd(context)) {
			return true;
		}
		return false;
	}

	@Override
	public int getNodeType() {
		return IF_STATEMENT;
	}

	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		if (istate.start == this) {
			istate.start = null;
		}
		if (istate.start != null) {
			Expression e = null;
			if (ifbody != null) {
				e = ifbody.interpret(istate, context);
			}
			if (istate.start != null && elsebody != null) {
				e = elsebody.interpret(istate, context);
			}
			return e;
		}

		Expression e = condition.interpret(istate, context);
		if (e == null) {
			throw new IllegalStateException("assert(e);");
		}
		if (e != EXP_CANT_INTERPRET) {
			if (!e.isConst()) {
				e = EXP_CANT_INTERPRET;
			} else {
				if (e.isBool(true)) {
					e = ifbody != null ? ifbody.interpret(istate, context)
							: null;
				} else if (e.isBool(false)) {
					e = elsebody != null ? elsebody.interpret(istate, context)
							: null;
				} else {
					e = EXP_CANT_INTERPRET;
				}
			}
		}
		return e;
	}

	@Override
	public IfStatement isIfStatement() {
		return this;
	}

	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		condition = condition.semantic(sc, context);
		condition = resolveProperties(sc, condition, context);
		condition = condition.checkToBoolean(context);

		// If we can short-circuit evaluate the if statement, don't do the
		// semantic analysis of the skipped code.
		// This feature allows a limited form of conditional compilation.
		condition = condition.optimize(WANTflags, context);

		// Evaluate at runtime
		int cs0 = sc.callSuper;
		int cs1;

		Scope scd;
		if (arg != null) {
			/*
			 * Declare arg, which we will set to be the result
			 * of condition.
			 */
			ScopeDsymbol sym = new ScopeDsymbol();
			sym.parent = sc.scopesym;
			scd = sc.push(sym);

			Type t = arg.type != null ? arg.type : condition.type;
			match = new VarDeclaration(loc, t, arg.ident, null);
			arg.var = match;
			
			match.noauto = true;
			match.semantic(scd, context);
			if (scd.insert(match) == null) {
				Assert.isTrue(false);
			}
			match.parent = sc.func;

			/*
			 * Generate: (arg = condition)
			 */
			VarExp v = new VarExp(loc, match);
			condition = new AssignExp(loc, v, condition);
			condition = condition.semantic(scd, context);
		} else {
			scd = sc.push();
		}
		
		// ifbody may be null if there is a syntaxis error
		if (ifbody == null) {
			return this;
		}
		
		ifbody = ifbody.semantic(scd, context);
		scd.pop();

		cs1 = sc.callSuper;
		sc.callSuper = cs0;
		if (elsebody != null) {
			elsebody = elsebody.semanticScope(sc, null, null, context);
		}
		sc.mergeCallSuper(loc, cs1);

		return this;
	}

	@Override
	public Statement syntaxCopy(SemanticContext context) {
		Statement i = null;
		if (ifbody != null) {
			i = ifbody.syntaxCopy(context);
		}

		Statement e = null;
		if (elsebody != null) {
			e = elsebody.syntaxCopy(context);
		}

		Argument a = arg != null ? arg.syntaxCopy(context) : null;
		IfStatement s = new IfStatement(loc, a, condition.syntaxCopy(context), i, e);
		return s;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("if (");
		if (arg != null) {
			if (arg.type != null) {
				arg.type.toCBuffer(buf, arg.ident, hgs, context);
			} else {
				buf.writestring(arg.ident.toChars());
			}
			buf.writebyte(';');
		}
		condition.toCBuffer(buf, hgs, context);
		buf.writebyte(')');
		buf.writenl();
		ifbody.toCBuffer(buf, hgs, context);
		if (elsebody != null) {
			buf.writestring("else");
			buf.writenl();
			elsebody.toCBuffer(buf, hgs, context);
		}
	}

	@Override
	public boolean usesEH() {
		return (ifbody != null && ifbody.usesEH())
				|| (elsebody != null && elsebody.usesEH());
	}

}
