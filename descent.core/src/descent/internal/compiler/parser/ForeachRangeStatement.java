package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 2.003
public class ForeachRangeStatement extends Statement {

	public TOK op;
	public Argument arg;
	public Expression lwr;
	public Expression upr;
	public Statement body;

	public ForeachRangeStatement(Loc loc, TOK op, Argument arg, Expression lwr,
			Expression upr, Statement body) {
		super(loc);

		this.op = op;
		this.arg = arg;
		this.lwr = lwr;
		this.upr = upr;
		this.body = body;
	}

	@Override
	public int getNodeType() {
		return FOREACH_RANGE_STATEMENT;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, arg);
			TreeVisitor.acceptChildren(visitor, lwr);
			TreeVisitor.acceptChildren(visitor, upr);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}

	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		ScopeDsymbol sym;
		Statement s = this;

		lwr = lwr.semantic(sc, context);
		lwr = resolveProperties(sc, lwr, context);
		if (null == lwr.type) {
			error("invalid range lower bound %s", lwr.toChars(context));
			return this;
		}

		upr = upr.semantic(sc, context);
		upr = resolveProperties(sc, upr, context);
		if (null == upr.type) {
			error("invalid range upper bound %s", upr.toChars(context));
			return this;
		}

		if (null != arg.type) {
			lwr = lwr.implicitCastTo(sc, arg.type, context);
			upr = upr.implicitCastTo(sc, arg.type, context);
		} else {
			/* Must infer types from lwr and upr
			 */
			AddExp ea = new AddExp(loc, lwr, upr);
			ea.typeCombine(sc, context);
			arg.type = ea.type;
			lwr = ea.e1;
			upr = ea.e2;
		}

		if (!arg.type.isscalar(context))
			error("%s is not a scalar type", arg.type.toChars(context));

		sym = new ScopeDsymbol();
		sym.parent = sc.scopesym;
		sc = sc.push(sym);

		sc.noctor++;

		VarDeclaration key = new VarDeclaration(loc, arg.type, arg.ident, null);
		DeclarationExp de = new DeclarationExp(loc, key);
		de.semantic(sc, context);

		if (0 < key.storage_class)
			error("foreach range: key cannot have storage class");

		sc.sbreak = this;
		sc.scontinue = this;
		body = body.semantic(sc, context);

		sc.noctor--;
		sc.pop();
		return s;
	}

	@Override
	public Statement syntaxCopy() {
		ForeachRangeStatement s = new ForeachRangeStatement(loc, op, arg
				.syntaxCopy(), lwr.syntaxCopy(), upr.syntaxCopy(),
				null != body ? body.syntaxCopy() : null);
		return s;
	}

	@Override
	public boolean fallOffEnd(SemanticContext context) {
		if (null != body)
			body.fallOffEnd(context);
		return true;
	}

}
