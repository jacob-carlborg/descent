package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.CharOperation;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.LINK.LINKd;

import static descent.internal.compiler.parser.STC.STClazy;

import static descent.internal.compiler.parser.Scope.CSXany_ctor;
import static descent.internal.compiler.parser.Scope.CSXlabel;
import static descent.internal.compiler.parser.Scope.CSXsuper_ctor;
import static descent.internal.compiler.parser.Scope.CSXthis_ctor;
import static descent.internal.compiler.parser.TOK.TOKcall;
import static descent.internal.compiler.parser.TOK.TOKcomma;
import static descent.internal.compiler.parser.TOK.TOKdelegate;
import static descent.internal.compiler.parser.TOK.TOKdot;
import static descent.internal.compiler.parser.TOK.TOKdotexp;
import static descent.internal.compiler.parser.TOK.TOKdottd;
import static descent.internal.compiler.parser.TOK.TOKdotvar;
import static descent.internal.compiler.parser.TOK.TOKimport;
import static descent.internal.compiler.parser.TOK.TOKsuper;
import static descent.internal.compiler.parser.TOK.TOKtemplate;
import static descent.internal.compiler.parser.TOK.TOKthis;
import static descent.internal.compiler.parser.TOK.TOKvar;

import static descent.internal.compiler.parser.TY.Taarray;
import static descent.internal.compiler.parser.TY.Tarray;
import static descent.internal.compiler.parser.TY.Tclass;
import static descent.internal.compiler.parser.TY.Tdelegate;
import static descent.internal.compiler.parser.TY.Tfunction;
import static descent.internal.compiler.parser.TY.Tpointer;
import static descent.internal.compiler.parser.TY.Tsarray;
import static descent.internal.compiler.parser.TY.Tstruct;
import static descent.internal.compiler.parser.TY.Tvoid;

// DMD 1.020
public class CallExp extends UnaExp {

	public Expressions arguments, sourceArguments;

	public CallExp(Loc loc, Expression e) {
		super(loc, TOK.TOKcall, e);
		this.arguments = null;
	}

	public CallExp(Loc loc, Expression e, Expression earg1) {
		super(loc, TOK.TOKcall, e);
		this.arguments = new Expressions(1);
		this.arguments.add(earg1);
		this.sourceArguments = new Expressions(1);
		this.sourceArguments.add(earg1);
	}

	public CallExp(Loc loc, Expression e, Expression earg1, Expression earg2) {
		super(loc, TOK.TOKcall, e);
		this.arguments = new Expressions(2);
		this.arguments.add(earg1);
		this.arguments.add(earg2);
		this.sourceArguments = new Expressions(2);
		this.sourceArguments.add(earg1);
		this.sourceArguments.add(earg2);
	}

	public CallExp(Loc loc, Expression e, Expressions exps) {
		super(loc, TOK.TOKcall, e);
		this.arguments = exps;
		if (exps != null) {
			this.sourceArguments = new Expressions(arguments);
		}
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
	public int checkSideEffect(int flag, SemanticContext context) {
		return 1;
	}

	@Override
	public Expression doInline(InlineDoState ids) {
		CallExp ce;

		ce = (CallExp) copy();
		ce.e1 = e1.doInline(ids);
		ce.arguments = arrayExpressiondoInline(arguments, ids);
		return ce;
	}

	@Override
	public int getNodeType() {
		return CALL_EXP;
	}

	@Override
	public int inlineCost(InlineCostState ics, SemanticContext context) {
		return 1 + e1.inlineCost(ics, context)
				+ arrayInlineCost(ics, arguments, context);
	}

	@Override
	public Expression inlineScan(InlineScanState iss, SemanticContext context) {
		Expression e = this;

		e1 = e1.inlineScan(iss, context);
		arrayInlineScan(iss, arguments, context);

		if (e1.op == TOKvar) {
			VarExp ve = (VarExp) e1;
			FuncDeclaration fd = ve.var.isFuncDeclaration();

			if (fd != null && fd != iss.fd && fd.canInline(false, context)) {
				e = fd.doInline(iss, null, arguments, context);
			}
		} else if (e1.op == TOKdotvar) {
			DotVarExp dve = (DotVarExp) e1;
			FuncDeclaration fd = dve.var.isFuncDeclaration();

			if (fd != null && fd != iss.fd && fd.canInline(true, context)) {
				if (dve.e1.op == TOKcall
						&& dve.e1.type.toBasetype(context).ty == Tstruct) {
					/* To create ethis, we'll need to take the address
					 * of dve.e1, but this won't work if dve.e1 is
					 * a function call.
					 */
					;
				} else {
					e = fd.doInline(iss, dve.e1, arguments, context);
				}
			}
		}

		return e;
	}

	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		Expression e = EXP_CANT_INTERPRET;

		if (e1.op == TOKvar) {
			FuncDeclaration fd = ((VarExp) e1).var.isFuncDeclaration();
			if (fd != null) { // Inline .dup
				if (fd.ident != null
						&& CharOperation.equals(fd.ident.ident, Id.adDup)
						&& arguments != null && arguments.size() == 2) {
					e = arguments.get(1);
					e = e.interpret(istate, context);
					if (e != EXP_CANT_INTERPRET) {
						e = expType(type, e);
					}
				} else {
					Expression eresult = fd.interpret(istate, arguments,
							context);
					if (eresult != null) {
						e = eresult;
					} else if (fd.type.toBasetype(context).nextOf().ty == Tvoid) {
						e = EXP_VOID_INTERPRET;
					} else {
						if (istate.stackOverflow) {
							context.acceptProblem(Problem.newSemanticTypeError(IProblem.ExpressionIsNotEvaluatableAtCompileTime, 0, start, length, new String[] { toChars(context) }));
						} else {
							context.acceptProblem(Problem.newSemanticTypeError(IProblem.ExpressionLeadsToStackOverflowAtCompileTime, 0, start, length, new String[] { toChars(context) }));
						}
					}
				}
			}
		}
		return e;
	}

	@Override
	public Expression optimize(int result, SemanticContext context) {
		Expression e = this;

		e1 = e1.optimize(result, context);
		if (e1.op == TOKvar && (result & WANTinterpret) != 0) {
			FuncDeclaration fd = ((VarExp) e1).var.isFuncDeclaration();
			if (fd != null) {
				Expression eresult = fd.interpret(null, arguments, context);
				if (eresult != null) {
					e = eresult;
				} else if ((result & WANTinterpret) != 0) {
					error("cannot evaluate %s at compile time",
							toChars(context));
				}
			}
		}
		return e;
	}

	@Override
	public void scanForNestedRef(Scope sc, SemanticContext context) {
		e1.scanForNestedRef(sc, context);
		arrayExpressionScanForNestedRef(sc, arguments, context);
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		TypeFunction tf;
		FuncDeclaration f = null;
		//int i;
		Type t1 = null;
		int istemp;

		if (type != null) {
			return this; // semantic() already run
		}

		if (e1.op == TOKdelegate) {
			DelegateExp de = (DelegateExp) e1;

			e1 = new DotVarExp(de.loc, de.e1, de.func);
			return semantic(sc, context);
		}

		boolean gotoLagain = false;

		/* Transform:
		 *	array.id(args) into id(array,args)
		 *	aa.remove(arg) into delete aa[arg]
		 */
		if (e1.op == TOKdot) {
			// BUG: we should handle array.a.b.c.e(args) too

			DotIdExp dotid = (DotIdExp) (e1);
			dotid.e1 = dotid.e1.semantic(sc, context);
			Assert.isNotNull(dotid.e1);
			if (dotid.e1.type != null) {
				TY e1ty = dotid.e1.type.toBasetype(context).ty;
				if (e1ty == Taarray
						&& CharOperation.equals(dotid.ident.ident, Id.remove)) {
					if (arguments == null || arguments.size() != 1) {
						error("expected key as argument to aa.remove()");
						// goto Lagain;
						gotoLagain = true;
					}
					if (!gotoLagain) {
						Expression key = arguments.get(0);
						key = key.semantic(sc, context);
						key = resolveProperties(sc, key, context);
						key.rvalue(context);

						TypeAArray taa = (TypeAArray) dotid.e1.type
								.toBasetype(context);
						key = key.implicitCastTo(sc, taa.index, context);
						key = key.implicitCastTo(sc, taa.key, context);

						return new RemoveExp(loc, dotid.e1, key);
					}
				} else if (e1ty == Tarray || e1ty == Tsarray || e1ty == Taarray) {
					if (arguments == null) {
						arguments = new Expressions();
					}
					arguments.add(0, dotid.e1);
					e1 = new IdentifierExp(dotid.loc, dotid.ident);
				}
			}
		}

		if (!gotoLagain) {
			istemp = 0;
		}

		boolean loopLagain = true;
		Lagain: while (loopLagain) {
			loopLagain = false;
			f = null;
			if (e1.op == TOKthis || e1.op == TOKsuper) {
				// semantic() run later for these
			} else {
				super.semantic(sc, context);

				/* Look for e1 being a lazy parameter
				 */
				if (e1.op == TOKvar) {
					VarExp ve = (VarExp) e1;

					if ((ve.var.storage_class & STClazy) != 0) {
						tf = new TypeFunction(null, ve.var.type, 0, LINKd);
						TypeDelegate t = new TypeDelegate(tf);
						ve.type = t.semantic(loc, sc, context);
					}
				}

				if (e1.op == TOKimport) { // Perhaps this should be moved to ScopeExp.semantic()
					ScopeExp se = (ScopeExp) e1;
					e1 = new DsymbolExp(loc, se.sds);
					e1 = e1.semantic(sc, context);
				}
				// patch for #540 by Oskar Linde
				else if (e1.op == TOKdotexp) {
					DotExp de = (DotExp) e1;

					if (de.e2.op == TOKimport) { // This should *really* be moved to ScopeExp::semantic()
						ScopeExp se = (ScopeExp) de.e2;
						de.e2 = new DsymbolExp(loc, se.sds);
						de.e2 = de.e2.semantic(sc, context);
					}

					if (de.e2.op == TOKtemplate) {
						TemplateExp te = (TemplateExp) de.e2;
						e1 = new DotTemplateExp(loc, de.e1, te.td);
					}
				}

			}

			if (e1.op == TOKcomma) {
				CommaExp ce = (CommaExp) e1;

				e1 = ce.e2;
				e1.type = ce.type;
				ce.e2 = this;
				ce.type = null;
				return ce.semantic(sc, context);
			}

			t1 = null;
			if (e1.type != null) {
				t1 = e1.type.toBasetype(context);
			}

			// Check for call operator overload
			if (t1 != null) {
				AggregateDeclaration ad;

				if (t1.ty == Tstruct) {
					ad = ((TypeStruct) t1).sym;
					if (search_function(ad, Id.call, context) != null) {
						// goto L1;	// overload of opCall, therefore it's a call
						// Rewrite as e1.call(arguments)
						Expression e = new DotIdExp(loc, e1, new IdentifierExp(
								Id.call));
						e = new CallExp(loc, e, arguments);
						e = e.semantic(sc, context);
						return e;
					}
					/* It's a struct literal
					 */
					Expression e = new StructLiteralExp(loc,
							(StructDeclaration) ad, arguments);
					e = e.semantic(sc, context);
					return e;
				} else if (t1.ty == Tclass) {
					ad = ((TypeClass) t1).sym;
					// goto L1;
					// L1:
					// Rewrite as e1.call(arguments)
					Expression e = new DotIdExp(loc, e1, new IdentifierExp(
							Id.call));
					e = new CallExp(loc, e, arguments);
					e = e.semantic(sc, context);
					return e;
				}
			}

			arrayExpressionSemantic(arguments, sc, context);
			preFunctionArguments(loc, sc, arguments, context);

			if (e1.op == TOKdotvar && t1.ty == Tfunction || e1.op == TOKdottd) {
				DotVarExp dve = null;
				DotTemplateExp dte = null;
				AggregateDeclaration ad;
				UnaExp ue = (UnaExp) (e1);

				if (e1.op == TOKdotvar) { // Do overload resolution
					dve = (DotVarExp) (e1);

					f = dve.var.isFuncDeclaration();
					Assert.isNotNull(f);
					f = f.overloadResolve(arguments, context, this);

					ad = f.toParent().isAggregateDeclaration();
				} else {
					dte = (DotTemplateExp) (e1);
					TemplateDeclaration td = dte.td;
					Assert.isNotNull(td);
					if (arguments == null) {
						// Should fix deduce() so it works on null argument
						arguments = new Expressions();
					}
					f = td.deduce(sc, loc, null, arguments, context);
					if (f == null) {
						type = Type.terror;
						return this;
					}
					ad = td.toParent().isAggregateDeclaration();
				}
				/* Now that we have the right function f, we need to get the
				 * right 'this' pointer if f is in an outer class, but our
				 * existing 'this' pointer is in an inner class.
				 * This code is analogous to that used for variables
				 * in DotVarExp.semantic().
				 */
				boolean loopL10 = true;
				L10: while (loopL10) {
					loopL10 = false;
					Type t = ue.e1.type.toBasetype(context);
					if (f.needThis()
							&& ad != null
							&& !(t.ty == Tpointer && t.next.ty == Tstruct && ((TypeStruct) t.next).sym == ad)
							&& !(t.ty == Tstruct && ((TypeStruct) t).sym == ad)) {
						ClassDeclaration cd = ad.isClassDeclaration();
						ClassDeclaration tcd = t.isClassHandle();

						if (cd == null
								|| tcd == null
								|| !(tcd == cd || cd.isBaseOf(tcd, null,
										context))) {
							if (tcd != null && tcd.isNested()) { // Try again with outer scope

								ue.e1 = new DotVarExp(loc, ue.e1, tcd.vthis);
								ue.e1 = ue.e1.semantic(sc, context);
								// goto L10;
								loopL10 = true;
								continue L10;
							}
							context.acceptProblem(Problem.newSemanticTypeError(IProblem.ThisForSymbolNeedsToBeType, 0, start, length, new String[] { f.toChars(context), ad.toChars(context), t
											.toChars(context) }));
						}
					}
				}

				checkDeprecated(sc, f, context);
				accessCheck(sc, ue.e1, f, context);
				if (!f.needThis()) {
					VarExp ve = new VarExp(loc, f);
					e1 = new CommaExp(loc, ue.e1, ve);
					e1.type = f.type;
				} else {
					if (e1.op == TOKdotvar) {
						dve.var = f;
					} else {
						e1 = new DotVarExp(loc, dte.e1, f);
					}
					e1.type = f.type;

					// See if we need to adjust the 'this' pointer
					ad = f.isThis();
					ClassDeclaration cd = ue.e1.type.isClassHandle();
					if (ad != null && cd != null
							&& ad.isClassDeclaration() != null && ad != cd
							&& ue.e1.op != TOKsuper) {
						ue.e1 = ue.e1.castTo(sc, ad.type, context); //new CastExp(loc, ue.e1, ad.type);
						ue.e1 = ue.e1.semantic(sc, context);
					}
				}
				t1 = e1.type;
			} else if (e1.op == TOKsuper) {
				// Base class constructor call
				ClassDeclaration cd = null;

				if (sc.func != null) {
					cd = sc.func.toParent().isClassDeclaration();
				}
				if (cd == null || cd.baseClass == null
						|| sc.func.isCtorDeclaration() == null) {
					error("super class constructor call must be in a constructor");
					type = Type.terror;
					return this;
				} else {
					f = cd.baseClass.ctor;
					if (f == null) {
						error("no super class constructor for %s", cd.baseClass
								.toChars(context));
						type = Type.terror;
						return this;
					} else {
						if (sc.noctor != 0 || (sc.callSuper & CSXlabel) != 0) {
							error("constructor calls not allowed in loops or after labels");
						}
						if ((sc.callSuper & (CSXsuper_ctor | CSXthis_ctor)) != 0) {
							error("multiple constructor calls");
						}
						sc.callSuper |= CSXany_ctor | CSXsuper_ctor;

						f = f.overloadResolve(arguments, context, this);
						checkDeprecated(sc, f, context);
						e1 = new DotVarExp(e1.loc, e1, f);
						e1 = e1.semantic(sc, context);
						t1 = e1.type;
					}
				}
			} else if (e1.op == TOKthis) {
				// same class constructor call
				ClassDeclaration cd = null;

				if (sc.func != null) {
					cd = sc.func.toParent().isClassDeclaration();
				}
				if (cd == null || sc.func.isCtorDeclaration() == null) {
					error("class constructor call must be in a constructor");
					type = Type.terror;
					return this;
				} else {
					if (sc.noctor != 0 || (sc.callSuper & CSXlabel) != 0) {
						error("constructor calls not allowed in loops or after labels");
					}
					if ((sc.callSuper & (CSXsuper_ctor | CSXthis_ctor)) != 0) {
						error("multiple constructor calls");
					}
					sc.callSuper |= CSXany_ctor | CSXthis_ctor;

					f = cd.ctor;
					f = f.overloadResolve(arguments, context, this);
					checkDeprecated(sc, f, context);
					e1 = new DotVarExp(e1.loc, e1, f);
					e1 = e1.semantic(sc, context);
					t1 = e1.type;

					// BUG: this should really be done by checking the static
					// call graph
					if (f == sc.func) {
						error("cyclic constructor call");
					}
				}
			} else if (t1 == null) {
				context.acceptProblem(Problem.newSemanticTypeError(IProblem.FunctionExpectedBeforeCall, 0, start, length, new String[] { e1.toChars(context) }));
				type = Type.terror;
				return this;
			} else if (t1.ty != Tfunction) {
				if (t1.ty == Tdelegate) {
					Assert.isTrue(t1.next.ty == Tfunction);
					tf = (TypeFunction) (t1.next);
					// goto Lcheckargs;
					return semantic_Lcheckargs(sc, tf, f, context);
				} else if (t1.ty == Tpointer && t1.next.ty == Tfunction) {
					Expression e;

					e = new PtrExp(loc, e1);
					t1 = t1.next;
					e.type = t1;
					e1 = e;
				} else if (e1.op == TOKtemplate) {
					TemplateExp te = (TemplateExp) e1;
					f = te.td.deduce(sc, loc, null, arguments, context);
					if (f == null) {
						type = Type.terror;
						return this;
					}
					if (f.needThis() && hasThis(sc) != null) {
						// Supply an implicit 'this', as in
						//	  this.ident

						e1 = new DotTemplateExp(loc, (new ThisExp(loc))
								.semantic(sc, context), te.td);
						// goto Lagain;
						loopLagain = true;
						continue Lagain;
					}

					e1 = new VarExp(loc, f);
					// goto Lagain;
					loopLagain = true;
					continue Lagain;
				} else {
					context.acceptProblem(Problem.newSemanticTypeError(IProblem.FunctionExpectedBeforeCallNotSymbolOfType, 0, start, length, new String[] { e1.toChars(context), e1.type.toChars(context) }));
					type = Type.terror;
					return this;
				}
			} else if (e1.op == TOKvar) {
				// Do overload resolution
				VarExp ve = (VarExp) e1;

				f = ve.var.isFuncDeclaration();
				Assert.isNotNull(f);

				// Look to see if f is really a function template
				if (false && istemp == 0 && f.parent != null) {
					TemplateInstance ti = f.parent.isTemplateInstance();

					if (ti != null
							&& (ti.name.equals(f.ident) || ti.toAlias(context).ident
									.equals(f.ident)) && ti.tempdecl != null) {
						/* This is so that one can refer to the enclosing
						 * template, even if it has the same name as a member
						 * of the template, if it has a !(arguments)
						 */
						TemplateDeclaration tempdecl = ti.tempdecl;
						if (tempdecl.overroot != null) {
							tempdecl = tempdecl.overroot; // then get the start
						}
						e1 = new TemplateExp(loc, tempdecl);
						istemp = 1;
						// goto Lagain;
						loopLagain = true;
						continue Lagain;
					}
				}

				f = f.overloadResolve(arguments, context, this);
				checkDeprecated(sc, f, context);

				if (f.needThis() && hasThis(sc) != null) {
					// Supply an implicit 'this', as in
					//	  this.ident

					e1 = new DotVarExp(loc, new ThisExp(loc), f);
					// goto Lagain;
					loopLagain = true;
					continue Lagain;
				}

				accessCheck(sc, null, f, context);

				ve.var = f;
				ve.type = f.type;
				t1 = f.type;
			}
		}

		Assert.isTrue(t1.ty == Tfunction);
		tf = (TypeFunction) (t1);

		// Lcheckargs:
		return semantic_Lcheckargs(sc, tf, f, context);
	}

	private Expression semantic_Lcheckargs(Scope sc, TypeFunction tf,
			FuncDeclaration f, SemanticContext context) {
		Assert.isTrue(tf.ty == Tfunction);
		type = tf.next;

		if (arguments == null) {
			arguments = new Expressions();
		}
		functionArguments(loc, sc, tf, arguments, context);

		Assert.isNotNull(type);

		if (f != null && f.tintro != null) {
			Type t = type;
			int[] offset = { 0 };

			if (f.tintro.next.isBaseOf(t, offset, context) && offset[0] != 0) {
				type = f.tintro.next;
				assignBinding();
				return castTo(sc, t, context);
			}
		}

		assignBinding();
		return this;
	}

	@Override
	public Expression syntaxCopy() {
		return new CallExp(loc, e1.syntaxCopy(), arraySyntaxCopy(arguments));
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		expToCBuffer(buf, hgs, e1, op.precedence, context);
		buf.writeByte('(');
		argsToCBuffer(buf, arguments, hgs, context);
		buf.writeByte(')');
	}

	@Override
	public Expression toLvalue(Scope sc, Expression e, SemanticContext context) {
		if (type.toBasetype(context).ty == Tstruct) {
			return this;
		} else {
			return super.toLvalue(sc, e, context);
		}
	}
	
	@Override
	public ASTDmdNode getBinding() {
		return e1.getBinding();
	}

}
