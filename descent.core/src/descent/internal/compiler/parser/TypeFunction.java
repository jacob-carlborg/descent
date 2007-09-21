package descent.internal.compiler.parser;

import melnorme.miscutil.Assert;
import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

import static descent.internal.compiler.parser.STC.STClazy;
import static descent.internal.compiler.parser.STC.STCout;
import static descent.internal.compiler.parser.STC.STCref;

import static descent.internal.compiler.parser.MATCH.*;

import static descent.internal.compiler.parser.Scope.SCOPEctor;

import static descent.internal.compiler.parser.TY.Tfunction;
import static descent.internal.compiler.parser.TY.Tsarray;
import static descent.internal.compiler.parser.TY.Ttuple;
import static descent.internal.compiler.parser.TY.Tvoid;
import static descent.internal.compiler.parser.TY.Tident;

// DMD 1.020
public class TypeFunction extends Type {

	public int inuse;
	public LINK linkage; // calling convention
	public Arguments parameters;
	public int varargs;

	public TypeFunction(Arguments parameters, Type treturn, int varargs,
			LINK linkage) {
		super(Tfunction, treturn);
		this.parameters = parameters;
		this.varargs = varargs;
		this.linkage = linkage;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, next);
			TreeVisitor.acceptChildren(visitor, parameters);
		}
		visitor.endVisit(this);
	}

	@Override
	public MATCH deduceType(Scope sc, Type tparam,
			TemplateParameters parameters, Objects dedtypes,
			SemanticContext context) {
		boolean L1 = true;

		// printf("TypeFunction.deduceType()\n");
		// printf("\tthis = %d, ", ty); print();
		// printf("\ttparam = %d, ", tparam.ty); tparam.print();

		// Extra check that function characteristics must match
		if (null != tparam && tparam.ty == Tfunction) {

			TypeFunction tp = (TypeFunction) tparam;
			if (varargs != tp.varargs || linkage != tp.linkage)
				return MATCHnomatch;

			int nfargs = Argument.dim(this.parameters, context);
			int nfparams = Argument.dim(tp.parameters, context);

			try {
				/*
				 * See if tuple match
				 */
				if (nfparams > 0 && nfargs >= nfparams - 1) {
					/*
					 * See if 'A' of the template parameter matches 'A' of the
					 * type of the last function parameter.
					 */
					Argument fparam = (Argument) tp.parameters
							.get(nfparams - 1);
					if (fparam.type.ty != Tident) {
						// goto L2;
						L1 = false;
						throw GOTO_L1;
					}
					TypeIdentifier tid = (TypeIdentifier) fparam.type;
					if (!tid.idents.isEmpty()) {
						L1 = false;
						throw GOTO_L1;
					}

					/*
					 * Look through parameters to find tuple matching tid.ident
					 */
					int tupi = 0;
					for (; true; tupi++) {
						if (tupi == parameters.size()) {
							// goto L2;
							L1 = false;
							throw GOTO_L1;
						}
						TemplateParameter t = (TemplateParameter) parameters
								.get(tupi);
						TemplateTupleParameter tup = t
								.isTemplateTupleParameter();
						if (null != tup && tup.ident.equals(tid.ident))
							break;
					}

					/*
					 * The types of the function arguments [nfparams - 1 ..
					 * nfargs] now form the tuple argument.
					 */
					int tuple_dim = nfargs - (nfparams - 1);

					/*
					 * See if existing tuple, and whether it matches or not
					 */
					Object o = (Object) dedtypes.get(tupi);
					if (null != o) { // Existing deduced argument must be a
										// tuple, and must
						// match
						Tuple t = isTuple((ASTDmdNode) o);
						if (null == t || t.objects.size() != tuple_dim)
							return MATCHnomatch;
						for (int i = 0; i < tuple_dim; i++) {
							Argument arg = Argument.getNth(this.parameters,
									nfparams - 1 + i, context);
							if (!arg.type.equals((Object) t.objects.get(i)))
								return MATCHnomatch;
						}
					} else { // Create new tuple
						Tuple t = new Tuple();
						t.objects = new Objects(tuple_dim);
						for (int i = 0; i < tuple_dim; i++) {
							Argument arg = Argument.getNth(this.parameters,
									nfparams - 1 + i, context);
							t.objects.set(i, arg.type);
						}
						dedtypes.set(tupi, t);
					}
					nfparams--; // don't consider the last parameter for type
					// deduction
					L1 = false;
				}
				throw GOTO_L1; // fallthrough
			} catch (GotoL1 $) {

				// L1:
				if (L1 && (nfargs != nfparams))
					return MATCHnomatch;

				// L2:
				for (int i = 0; i < nfparams; i++) {
					Argument a = Argument.getNth(this.parameters, i, context);
					Argument ap = Argument.getNth(tp.parameters, i, context);
					if (a.storageClass != ap.storageClass
							|| null == a.type.deduceType(sc, ap.type,
									parameters, dedtypes, context))
						return MATCHnomatch;
				}
			}
		}
		return super.deduceType(sc, tparam, parameters, dedtypes, context);
	}

	@Override
	public int getNodeType() {
		return TYPE_FUNCTION;
	}

	@Override
	public TypeInfoDeclaration getTypeInfoDeclaration(SemanticContext context) {
		return new TypeInfoFunctionDeclaration(this, context);
	}

	@Override
	public Type reliesOnTident() {

		if (null != parameters) {
			for (int i = 0; i < parameters.size(); i++) {
				Argument arg = (Argument) parameters.get(i);
				Type t = arg.type.reliesOnTident();
				if (null != t)
					return t;
			}
		}
		return next.reliesOnTident();
	}

	public RET retStyle() {
		return RET.RETstack;
	}

	@Override
	public Type semantic(Loc loc, Scope sc, SemanticContext context) {
		if (deco != null) { // if semantic() already run
			return this;
		}

		linkage = sc.linkage;
		if (next == null) {
			next = tvoid;
		}
		next = next.semantic(loc, sc, context);
		if (next.toBasetype(context).ty == Tsarray) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.FunctionsCannotReturnStaticArrays, 0, start,
					length));
			next = Type.terror;
		}
		if (next.toBasetype(context).ty == Tfunction) {
			error("functions cannot return a function");
			next = Type.terror;
		}
		if (next.toBasetype(context).ty == Ttuple) {
			error("functions cannot return a tuple");
			next = Type.terror;
		}
		if (next.isauto() && (sc.flags & SCOPEctor) == 0)
			error("functions cannot return auto %s", next.toChars(context));

		if (parameters != null) {
			int dim = Argument.dim(parameters, context);

			for (int i = 0; i < dim; i++) {
				Argument arg = Argument.getNth(parameters, i, context);
				Type t;

				inuse++;
				arg.type = arg.type.semantic(loc, sc, context);
				arg.sourceType.setBinding(arg.type.getBinding());
				if (inuse == 1) {
					inuse--;
				}
				t = arg.type.toBasetype(context);

				if ((arg.storageClass & (STCout | STCref | STClazy)) != 0) {
					if (t.ty == Tsarray) {
						context
								.acceptProblem(Problem
										.newSemanticTypeError(
												IProblem.CannotHaveOutOrInoutParameterOfTypeStaticArray,
												0, t.start, t.length));
					}
				}
				if ((arg.storageClass & STClazy) == 0 && t.ty == Tvoid) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.CannotHaveParameterOfTypeVoid, 0, t.start,
							t.length));
				}

				if (arg.defaultArg != null) {
					arg.defaultArg = arg.defaultArg.semantic(sc, context);
					arg.defaultArg = Expression.resolveProperties(sc,
							arg.defaultArg, context);
					arg.defaultArg = arg.defaultArg.implicitCastTo(sc,
							arg.type, context);
				}

				/*
				 * If arg turns out to be a tuple, the number of parameters may
				 * change.
				 */
				if (t.ty == Ttuple) {
					dim = Argument.dim(parameters, context);
					i--;
				}
			}
		}
		deco = merge(context).deco;

		if (inuse != 0) {
			error("recursive type");
			inuse = 0;
			return terror;
		}

		if (varargs != 0 && linkage != LINK.LINKd
				&& Argument.dim(parameters, context) == 0) {
			error("variadic functions with non-D linkage must have at least one parameter");
		}

		/*
		 * Don't return merge(), because arg identifiers and default args can be
		 * different even though the types match
		 */
		return this;
	}

	@Override
	public Type syntaxCopy() {
		Type treturn = next != null ? next.syntaxCopy() : null;
		Arguments params = Dsymbol.arraySyntaxCopy(parameters);
		Type t = new TypeFunction(params, treturn, varargs, linkage);
		return t;
	}

	@Override
	public void toDecoBuffer(OutBuffer buf, SemanticContext context) {
		char mc;

		if (inuse != 0) {
			inuse = 2; // flag error to caller
			return;
		}
		inuse++;
		switch (linkage) {
		case LINKd:
			mc = 'F';
			break;
		case LINKc:
			mc = 'U';
			break;
		case LINKwindows:
			mc = 'W';
			break;
		case LINKpascal:
			mc = 'V';
			break;
		case LINKcpp:
			mc = 'R';
			break;
		default:
			throw new IllegalStateException("assert(0);");
		}
		buf.writeByte(mc);
		// Write argument types
		// TODO semantic
		// argsToDecoBuffer(buf, parameters);
		buf.writeByte('Z' - varargs); // mark end of arg list
		next.toDecoBuffer(buf, context);
		inuse--;
	}

	public MATCH callMatch(Expressions args, SemanticContext context) {
		MATCH match = MATCHexact; // assume exact match

		int nparams = Argument.dim(parameters, context);
		int nargs = null != args ? args.size() : 0;
		if (nparams == nargs)
			;
		else if (nargs > nparams) {
			if (varargs == 0)
				return MATCHnomatch; // goto Nomatch; // too many args; no
										// match
			match = MATCHconvert; // match ... with a "conversion" match level
		}

		for (int u = 0; u < nparams; u++) {
			MATCH m;
			Expression arg;

			// BUG: what about out and ref?

			Argument p = Argument.getNth(parameters, u, context);
			Assert.isTrue(null != p);
			if (u >= nargs) {
				if (null != p.defaultArg)
					continue;
				if (varargs == 2 && u + 1 == nparams) {
					// goto L1;
					// PERHAPS this was copied & pasted, so i'm not 100% the
					// logic here is right
					if (varargs == 2 && u + 1 == nparams) // if last varargs
															// param
					{
						Type tb = p.type.toBasetype(context);
						TypeSArray tsa;
						integer_t sz;

						switch (tb.ty) {
						case Tsarray:
							tsa = (TypeSArray) tb;
							sz = tsa.dim.toInteger(context);
							if (!sz.equals(nargs - u))
								return MATCHnomatch; // goto Nomatch;
						case Tarray:
							for (; u < nargs; u++) {
								arg = (Expression) args.get(u);
								assert (null != arg);
								/*
								 * If lazy array of delegates, convert arg(s) to
								 * delegate(s)
								 */
								Type tret = p.isLazyArray(context);
								if (null != tret) {
									if (tb.next.equals(arg.type)) {
										m = MATCHexact;
									} else {
										m = arg.implicitConvTo(tret, context);
										if (m == MATCHnomatch) {
											if (tret.toBasetype(context).ty == Tvoid)
												m = MATCHconvert;
										}
									}
								} else
									m = arg.implicitConvTo(tb.next, context);
								if (m == MATCHnomatch)
									return MATCHnomatch; // goto Nomatch;
								if (m.ordinal() < match.ordinal())
									match = m;
							}
							return match; // goto Ldone;

						case Tclass:
							// Should see if there's a constructor match?
							// Or just leave it ambiguous?
							return match; // goto Ldone;

						default:
							return MATCHnomatch; // goto Nomatch;
						}
					}
					return MATCHnomatch; // goto Nomatch;
				} else {
					return MATCHnomatch; // goto Nomatch; // not enough
											// arguments
				}
			}
			arg = (Expression) args.get(u);
			assert (null != arg);
			if (0 != (p.storageClass & STClazy) && p.type.ty == Tvoid
					&& arg.type.ty != Tvoid)
				m = MATCHconvert;
			else
				m = arg.implicitConvTo(p.type, context);
			// printf("\tm = %d\n", m);
			if (m == MATCHnomatch) // if no match
			{
				// L1:
				if (varargs == 2 && u + 1 == nparams) // if last varargs
				// param
				{
					Type tb = p.type.toBasetype(context);
					TypeSArray tsa;
					integer_t sz;

					switch (tb.ty) {
					case Tsarray:
						tsa = (TypeSArray) tb;
						sz = tsa.dim.toInteger(context);
						if (!sz.equals(nargs - u))
							return MATCHnomatch; // goto Nomatch;
					case Tarray:
						for (; u < nargs; u++) {
							arg = (Expression) args.get(u);
							assert (null != arg);
							/*
							 * If lazy array of delegates, convert arg(s) to
							 * delegate(s)
							 */
							Type tret = p.isLazyArray(context);
							if (null != tret) {
								if (tb.next.equals(arg.type)) {
									m = MATCHexact;
								} else {
									m = arg.implicitConvTo(tret, context);
									if (m == MATCHnomatch) {
										if (tret.toBasetype(context).ty == Tvoid)
											m = MATCHconvert;
									}
								}
							} else
								m = arg.implicitConvTo(tb.next, context);
							if (m == MATCHnomatch)
								return MATCHnomatch; // goto Nomatch;
							if (m.ordinal() < match.ordinal())
								match = m;
						}
						return match; // goto Ldone;

					case Tclass:
						// Should see if there's a constructor match?
						// Or just leave it ambiguous?
						return match; // goto Ldone;

					default:
						return MATCHnomatch; // goto Nomatch;
					}
				}
				return MATCHnomatch; // goto Nomatch;
			}
			if (m.ordinal() < match.ordinal())
				match = m; // pick worst match
		}

		return match;
	}

	@Override
	public void toCBuffer2(OutBuffer buf, IdentifierExp ident, HdrGenState hgs,
			SemanticContext context) {
		String p = null;

	    if (inuse != 0)
	    {	inuse = 2;		// flag error to caller
		return;
	    }
	    inuse++;
	    if (hgs.ddoc != 1)
	    {
		switch (linkage)
		{
		    case LINKd:		p = null;	break;
		    case LINKc:		p = "C ";	break;
		    case LINKwindows:	p = "Windows ";	break;
		    case LINKpascal:	p = "Pascal ";	break;
		    case LINKcpp:	p = "C++ ";	break;
		    default:
		    	throw new IllegalStateException("assert(0);");
		}
	    }

	    if (buf.data.length() != 0)
	    {
		if (!hgs.hdrgen && p != null)
		    buf.prependstring(p);
		// TODO semantic
		// buf.bracket('(', ')');
		assert(ident == null);
	    }
	    else
	    {
		if (!hgs.hdrgen && p != null)
		    buf.writestring(p);
		if (ident != null)
		{   buf.writeByte(' ');
		    buf.writestring(ident.toHChars2());
		}
	    }
	    Argument.argsToCBuffer(buf, hgs, parameters, varargs, context);
	    if (next != null&& (null == ident || ident.toHChars2().equals(ident.toChars())))
		next.toCBuffer2(buf, null, hgs, context);
	    inuse--;
	}

	@SuppressWarnings("serial")
	private static class GotoL1 extends Exception {
	}

	private static final GotoL1 GOTO_L1 = new GotoL1();

	// PERHAPS type *toCtype();
	// PERHAPS enum RET retStyle();
	// PERHAPS unsigned totym();
}
