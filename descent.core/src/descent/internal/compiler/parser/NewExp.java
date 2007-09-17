package descent.internal.compiler.parser;

import java.math.BigInteger;

import melnorme.miscutil.tree.TreeVisitor;

import org.eclipse.core.runtime.Assert;

import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.TOK.TOKint64;

import static descent.internal.compiler.parser.TY.Tarray;
import static descent.internal.compiler.parser.TY.Tclass;
import static descent.internal.compiler.parser.TY.Tstruct;

public class NewExp extends Expression {

	public Expression thisexp;
	public Expressions newargs;
	public Type newtype;
	public Expressions arguments;
	public CtorDeclaration member; // constructor function
	public NewDeclaration allocator; // allocator function
	public boolean onstack; // allocate on stack

	public NewExp(Loc loc, Expression thisexp, Expressions newargs,
			Type newtype, Expressions arguments) {
		super(loc, TOK.TOKnew);
		this.thisexp = thisexp;
		this.newargs = newargs;
		this.newtype = newtype;
		this.arguments = arguments;
		this.member = null;
		this.allocator = null;
		this.onstack = false;
	}

	@Override
	public int getNodeType() {
		return NEW_EXP;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, thisexp);
			TreeVisitor.acceptChildren(visitor, newargs);
			TreeVisitor.acceptChildren(visitor, newtype);
			TreeVisitor.acceptChildren(visitor, arguments);
		}
		visitor.endVisit(this);
	}

	@Override
	public int checkSideEffect(int flag, SemanticContext context) {
		return 1;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		Type tb;
		ClassDeclaration cdthis = null;

		if (type != null) {
			return this;
		}

		boolean loop = true;
		Lagain: while (loop) {
			loop = false;
			if (thisexp != null) {
				thisexp = thisexp.semantic(sc, context);
				cdthis = thisexp.type.isClassHandle();
				if (cdthis != null) {
					sc = sc.push(cdthis);
					type = newtype.semantic(loc, sc, context);
					sc = sc.pop();
				} else {
					error(
							"'this' for nested class must be a class type, not %s",
							thisexp.type.toChars(context));
					type = newtype.semantic(loc, sc, context);
				}
			} else {
				type = newtype.semantic(loc, sc, context);
			}
		    newtype = type;		// in case type gets cast to something else
			tb = type.toBasetype(context);

			arrayExpressionSemantic(newargs, sc, context);
			preFunctionArguments(loc, sc, newargs, context);
			arrayExpressionSemantic(arguments, sc, context);
			preFunctionArguments(loc, sc, arguments, context);

			if (thisexp != null && tb.ty != Tclass) {
				error("e.new is only for allocating nested classes, not %s", tb
						.toChars(context));
			}

			if (tb.ty == Tclass) {
				TypeFunction tf;

				TypeClass tc = (TypeClass) (tb);
				ClassDeclaration cd = tc.sym.isClassDeclaration();
				if (cd.isInterfaceDeclaration() != null) {
					error("cannot create instance of interface %s", cd
							.toChars(context));
				} else if (cd.isAbstract()) {
					error("cannot create instance of abstract class %s", cd
							.toChars(context));
				}
				checkDeprecated(sc, cd, context);
				if (cd.isNested()) { /* We need a 'this' pointer for the nested class.
				 * Ensure we have the right one.
				 */
					Dsymbol s = cd.toParent2();
					ClassDeclaration cdn = s.isClassDeclaration();

					if (cdn != null) {
						if (cdthis == null) {
							// Supply an implicit 'this' and try again
							thisexp = new ThisExp(loc);
							for (Dsymbol sp = sc.parent; true; sp = sp.parent) {
								if (sp == null) {
									error(
											"outer class %s 'this' needed to 'new' nested class %s",
											cdn.toChars(context), cd
													.toChars(context));
									break;
								}
								ClassDeclaration cdp = sp.isClassDeclaration();
								if (cdp == null) {
									continue;
								}
								if (cdp == cdn
										|| cdn.isBaseOf(cdp, null, context)) {
									break;
								}
								// Add a '.outer' and try again
								thisexp = new DotIdExp(loc, thisexp,
										new IdentifierExp(loc, Id.outer));
							}
							if (context.global.errors == 0) {
								// goto Lagain;
								loop = true;
								continue Lagain;
							}
						}
						if (cdthis != null) {
							if (cdthis != cdn
									&& !cdn.isBaseOf(cdthis, null, context)) {
								error(
										"'this' for nested class must be of type %s, not %s",
										cdn.toChars(context), thisexp.type
												.toChars(context));
							}
						}
					} else if (thisexp != null) {
						error("e.new is only for allocating nested classes");
					}
				} else if (thisexp != null) {
					error("e.new is only for allocating nested classes");
				}

				FuncDeclaration f = cd.ctor;
				if (f != null) {
					f = f.overloadResolve(arguments, context);
					checkDeprecated(sc, f, context);
					member = f.isCtorDeclaration();
					Assert.isNotNull(member);

					cd.accessCheck(sc, member, context);

					tf = (TypeFunction) f.type;
					type = tf.next;

					if (arguments == null) {
						arguments = new Expressions();
					}
					functionArguments(loc, sc, tf, arguments, context);
				} else {
					if (arguments != null && arguments.size() > 0) {
						error("no constructor for %s", cd.toChars(context));
					}
				}

				if (cd.aggNew != null) {
					Expression e;

					f = cd.aggNew;

					// Prepend the uint size argument to newargs[]
					e = new IntegerExp(loc, cd.size(context), Type.tuns32);
					if (newargs == null) {
						newargs = new Expressions();
					}
					newargs.add(0, e);

					f = f.overloadResolve(newargs, context);
					allocator = f.isNewDeclaration();
					Assert.isNotNull(allocator);

					tf = (TypeFunction) f.type;
					functionArguments(loc, sc, tf, newargs, context);
				} else {
					if (newargs != null && newargs.size() > 0) {
						error("no allocator for %s", cd.toChars(context));
					}
				}

			} else if (tb.ty == Tstruct) {
				TypeStruct ts = (TypeStruct) tb;
				StructDeclaration sd = ts.sym;
				FuncDeclaration f = sd.aggNew;
				TypeFunction tf;

				if (arguments != null && arguments.size() > 0) {
					error("no constructor for %s", type.toChars(context));
				}

				if (f != null) {
					Expression e;

					// Prepend the uint size argument to newargs[]
					e = new IntegerExp(loc, sd.size(context), Type.tuns32);
					if (newargs == null) {
						newargs = new Expressions();
					}
					newargs.add(0, e);

					f = f.overloadResolve(newargs, context);
					allocator = f.isNewDeclaration();
					Assert.isNotNull(allocator);

					tf = (TypeFunction) f.type;
					functionArguments(loc, sc, tf, newargs, context);

					e = new VarExp(loc, f);
					e = new CallExp(loc, e, newargs);
					e = e.semantic(sc, context);
					e.type = type.pointerTo(context);
					return e;
				}

				type = type.pointerTo(context);
			} else if (tb.ty == Tarray
					&& (arguments != null && arguments.size() > 0)) {
				for (int i = 0; i < arguments.size(); i++) {
					if (tb.ty != Tarray) {
						error("too many arguments for array");
						// TODO semantic
						// arguments.dim = i;
						break;
					}

					Expression arg = arguments.get(i);
					arg = resolveProperties(sc, arg, context);
					arg = arg.implicitCastTo(sc, Type.tsize_t, context);
					if (arg.op == TOKint64
							&& arg.toInteger(context)
									.compareTo(BigInteger.ZERO) < 0) {
						error("negative array index %s", arg.toChars(context));
					}
					arguments.set(i, arg);
					tb = tb.next.toBasetype(context);
				}
			} else if (tb.isscalar(context)) {
				if (arguments != null && arguments.size() > 0) {
					error("no constructor for %s", type.toChars(context));
				}

				type = type.pointerTo(context);
			} else {
				error(
						"new can only create structs, dynamic arrays or class objects, not %s's",
						type.toChars(context));
				type = type.pointerTo(context);
			}
		}

		return this;
	}

	@Override
	public Expression syntaxCopy() {
		return new NewExp(loc, thisexp != null ? thisexp.syntaxCopy() : null,
				arraySyntaxCopy(newargs), newtype.syntaxCopy(),
				arraySyntaxCopy(arguments));
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		if (thisexp != null) {
			expToCBuffer(buf, hgs, thisexp, PREC.PREC_primary, context);
			buf.writeByte('.');
		}
		buf.writestring("new ");
		if (newargs != null && newargs.size() > 0) {
			buf.writeByte('(');
			argsToCBuffer(buf, newargs, hgs, context);
			buf.writeByte(')');
		}
		newtype.toCBuffer(buf, null, hgs, context);
		if (arguments != null && arguments.size() > 0) {
			buf.writeByte('(');
			argsToCBuffer(buf, arguments, hgs, context);
			buf.writeByte(')');
		}
	}

}
