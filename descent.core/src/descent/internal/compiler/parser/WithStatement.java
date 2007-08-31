package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.TOK.*;
import static descent.internal.compiler.parser.TY.*;

public class WithStatement extends Statement {

	public Expression exp;
	public Statement body;
	public VarDeclaration wthis;

	public WithStatement(Loc loc, Expression exp, Statement body) {
		super(loc);
		this.exp = exp;
		this.body = body;
	}

	@Override
	public int getNodeType() {
		return WITH_STATEMENT;
	}

	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}

	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		ScopeDsymbol sym;
		Initializer init;

		exp = exp.semantic(sc, context);
		exp = resolveProperties(sc, exp, context);
		if (exp.op == TOKimport) {
			ScopeExp es = (ScopeExp) exp;

			sym = es.sds;
		} else if (exp.op == TOKtype) {
			TypeExp es = (TypeExp) exp;

			sym = es.type.toDsymbol(sc, context).isScopeDsymbol();
			if (sym == null) {
				error("%s has no members", es.toChars(context));
				body = body.semantic(sc, context);
				return this;
			}
		} else {
			Type t = exp.type;

			if (t == null) {
				throw new IllegalStateException("assert(t);");
			}
			t = t.toBasetype(context);
			if (t.isClassHandle() != null) {
				init = new ExpInitializer(loc, exp);
				wthis = new VarDeclaration(loc, exp.type, Id.withSym, init);
				wthis.semantic(sc, context);

				sym = new WithScopeSymbol(this);
				sym.parent = sc.scopesym;
			} else if (t.ty == Tstruct) {
				Expression e = exp.addressOf(sc, context);
				init = new ExpInitializer(loc, e);
				wthis = new VarDeclaration(loc, e.type, Id.withSym, init);
				wthis.semantic(sc, context);
				sym = new WithScopeSymbol(this);
				sym.parent = sc.scopesym;
			} else {
				error("with expressions must be class objects, not '%s'",
						exp.type.toChars(context));
				return null;
			}
		}
		sc = sc.push(sym);

		if (body != null) {
			body = body.semantic(sc, context);
		}

		sc.pop();

		return this;
	}

}
