package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.TOK.*;
import static descent.internal.compiler.parser.TY.*;

// DMD 1.020
public class WithStatement extends Statement {

	public Expression exp;
	public Expression sourceExp;
	public Statement body;
	public Statement sourceBody;
	
	public VarDeclaration wthis;

	public WithStatement(Loc loc, Expression exp, Statement body) {
		super(loc);
		this.exp = exp;
		this.sourceExp = exp;
		this.body = body;
		this.sourceBody = body;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}

	@Override
	public boolean fallOffEnd(SemanticContext context) {
		return body != null ? body.fallOffEnd(context) : true;
	}

	@Override
	public int getNodeType() {
		return WITH_STATEMENT;
	}

	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		ScopeDsymbol sym;
		Initializer init;

		exp = exp.semantic(sc, context);
		exp = resolveProperties(sc, exp, context);
		if (exp.op == TOKimport) {
			ScopeExp es = (ScopeExp) exp;

			sym = (ScopeDsymbol) es.sds; // SEMANTIC
		} else if (exp.op == TOKtype) {
			TypeExp es = (TypeExp) exp;

			sym = es.type.toDsymbol(sc, context).isScopeDsymbol();
			if (sym == null) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.SymbolHasNoMembers, this, new String[] { es.toChars(context) }));
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
				context.acceptProblem(Problem.newSemanticTypeError(IProblem.WithExpressionsMustBeClassObject, sourceExp, new String[] { exp.type.toChars(context) }));
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

	@Override
	public Statement syntaxCopy(SemanticContext context) {
		WithStatement s = new WithStatement(loc, exp.syntaxCopy(context),
				body != null ? body.syntaxCopy(context) : null);
		return s;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("with (");
		exp.toCBuffer(buf, hgs, context);
		buf.writestring(")\n");
		if (body != null) {
			body.toCBuffer(buf, hgs, context);
		}
	}
	
	@Override
	public boolean usesEH() {
		return body != null ? body.usesEH() : false;
	}

}
