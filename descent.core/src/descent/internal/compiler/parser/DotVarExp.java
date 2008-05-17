package descent.internal.compiler.parser;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

import static descent.internal.compiler.parser.STC.STCfield;

import static descent.internal.compiler.parser.TOK.TOKdsymbol;
import static descent.internal.compiler.parser.TOK.TOKthis;


public class DotVarExp extends UnaExp {

	public Declaration var;

	public DotVarExp(Loc loc, Expression e, Declaration var) {
		super(loc, TOK.TOKdotvar, e);
		this.var = var;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

	@Override
	public int getNodeType() {
		return DOT_VAR_EXP;
	}

	@Override
	public Expression modifiableLvalue(Scope sc, Expression e,
			SemanticContext context) {
		if (var.isCtorinit()) { // It's only modifiable if inside the right constructor
			Dsymbol s = sc.func;
			while (true) {
				FuncDeclaration fd = null;
				if (s != null) {
					fd = s.isFuncDeclaration();
				}
				if (fd != null
						&& ((fd.isCtorDeclaration() != null && (var.storage_class & STCfield) != 0) || (fd
								.isStaticCtorDeclaration() != null && (var.storage_class & STCfield) == 0))
						&& fd.toParent() == var.toParent() && e1.op == TOKthis) {
					VarDeclaration v = var.isVarDeclaration();
					Assert.isNotNull(v);
					v.ctorinit(true);
				} else {
					if (s != null) {
						s = s.toParent2();
						continue;
					} else {
						String p = var.isStatic() ? "static " : "";
						if (context.acceptsProblems()) {
							context.acceptProblem(Problem.newSemanticTypeError(
									IProblem.CanOnlyInitiailizeConstMemberInsideConstructor, this, new String[] { p, var.toChars(context), p }));
						}
					}
				}
				break;
			}
		}
		return this;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		if (type == null) {
			var = var.toAlias(context).isDeclaration();

			TupleDeclaration tup = var.isTupleDeclaration();
			if (tup != null) {
				/* Replace:
				 *	e1.tuple(a, b, c)
				 * with:
				 *	tuple(e1.a, e1.b, e1.c)
				 */
				Expressions exps = new Expressions();
				exps.setDim(tup.objects.size());
				for (int i = 0; i < tup.objects.size(); i++) {
					ASTDmdNode o = tup.objects.get(i);
					if (o.dyncast() != DYNCAST.DYNCAST_EXPRESSION) {
						if (context.acceptsProblems()) {
							context.acceptProblem(Problem.newSemanticTypeWarning(IProblem.SymbolNotAnExpression, 0, o.getStart(), o.getLength(), new String[] { o.toChars(context) }));
						}
					} else {
						Expression e = (Expression) o;
						if (e.op != TOKdsymbol) {
							if (context.acceptsProblems()) {
								context.acceptProblem(Problem.newSemanticTypeError(
										IProblem.SymbolIsNotAMember, this, new String[] { e.toChars(context) }));
							}
						} else {
							DsymbolExp ve = (DsymbolExp) e;

							e = new DotVarExp(loc, e1, ve.s.isDeclaration());
							exps.set(i, e);
						}
					}
				}
				Expression e = new TupleExp(loc, exps);
				e = e.semantic(sc, context);
				return e;
			}

			e1 = e1.semantic(sc, context);
			
			// Descent: lazy initialization
			var.consumeRest();
			
			type = var.type;
			if (type == null && context.global.errors > 0) { // var is goofed up, just return 0
				return new IntegerExp(loc, 0);
			}
			Assert.isNotNull(type);

			if (var.isFuncDeclaration() == null) // for functions, do checks after overload resolution
			{
				AggregateDeclaration ad = var.toParent()
						.isAggregateDeclaration();
			    e1 = getRightThis(loc, sc, ad, e1, var, context);
				accessCheck(sc, e1, var, context);
			}
		}
		return this;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		expToCBuffer(buf, hgs, e1, PREC.PREC_primary, context);
		buf.writeByte('.');
		buf.writestring(var.toChars(context));
	}

	@Override
	public Expression toLvalue(Scope sc, Expression e, SemanticContext context) {
		return this;
	}

}
