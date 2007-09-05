package descent.internal.compiler.parser;

import java.util.ArrayList;

import melnorme.miscutil.tree.TreeVisitor;

import org.eclipse.core.runtime.Assert;

import descent.internal.compiler.parser.ast.IASTVisitor;

import static descent.internal.compiler.parser.STC.STCfield;

import static descent.internal.compiler.parser.TOK.TOKdsymbol;
import static descent.internal.compiler.parser.TOK.TOKthis;

import static descent.internal.compiler.parser.TY.Tpointer;
import static descent.internal.compiler.parser.TY.Tstruct;

// DMD 1.020
public class DotVarExp extends UnaExp {

	public Declaration var;

	public DotVarExp(Loc loc, Expression e, Declaration var) {
		super(loc, TOK.TOKdotvar, e);
		this.var = var;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, e1);
		}
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
					v.ctorinit = true;
				} else {
					if (s != null) {
						s = s.toParent2();
						continue;
					} else {
						String p = var.isStatic() ? "static " : "";
						error(
								"can only initialize %sconst member %s inside %sconstructor",
								p, var.toChars(context), p);
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
				ArrayList<Expression> exps = new ArrayList<Expression>();
				exps.ensureCapacity(tup.objects.size());
				for (int i = 0; i < tup.objects.size(); i++) {
					ASTDmdNode o = tup.objects.get(i);
					if (o.dyncast() != DYNCAST.DYNCAST_EXPRESSION) {
						error("%s is not an expression", o.toChars(context));
					} else {
						Expression e = (Expression) o;
						if (e.op != TOKdsymbol) {
							error("%s is not a member", e.toChars(context));
						} else {
							DsymbolExp ve = (DsymbolExp) e;

							e = new DotVarExp(loc, e1, ve.s.isDeclaration());
							exps.add(e);
						}
					}
				}
				Expression e = new TupleExp(loc, exps);
				e = e.semantic(sc, context);
				return e;
			}

			e1 = e1.semantic(sc, context);
			type = var.type;
			if (type == null && context.global.errors > 0) { // var is goofed up, just return 0
				return new IntegerExp(loc, 0);
			}
			Assert.isNotNull(type);

			if (var.isFuncDeclaration() == null) // for functions, do checks after overload resolution
			{
				AggregateDeclaration ad = var.toParent()
						.isAggregateDeclaration();

				boolean loop = true;
				L1: while (loop) {
					loop = false;

					Type t = e1.type;

					if (ad != null
							&& !(t.ty == Tpointer && t.next.ty == Tstruct && ((TypeStruct) t.next).sym == ad)
							&& !(t.ty == Tstruct && ((TypeStruct) t).sym == ad)) {
						ClassDeclaration cd = ad.isClassDeclaration();
						ClassDeclaration tcd = t.isClassHandle();

						if (cd == null
								|| tcd == null
								|| !(tcd == cd || cd.isBaseOf(tcd, null,
										context))) {
							if (tcd != null && tcd.isNested()) { // Try again with outer scope

								e1 = new DotVarExp(loc, e1, tcd.vthis);
								e1 = e1.semantic(sc, context);

								// Skip over nested functions, and get the enclosing
								// class type.
								Dsymbol s = tcd.toParent();
								while (s != null
										&& s.isFuncDeclaration() != null) {
									FuncDeclaration f = s.isFuncDeclaration();
									if (f.vthis != null) {
										e1 = new VarExp(loc, f.vthis);
									}
									s = s.toParent();
								}
								if (s != null && s.isClassDeclaration() != null) {
									e1.type = s.isClassDeclaration().type;
								}

								// goto L1;
								loop = true;
								continue L1;
							}
							error(
									"this for %s needs to be type %s not type %s",
									var.toChars(context), ad.toChars(context),
									t.toChars(context));
						}
					}
				}
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
