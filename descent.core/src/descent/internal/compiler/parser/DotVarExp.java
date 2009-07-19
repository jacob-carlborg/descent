package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.STC.STCfield;
import static descent.internal.compiler.parser.TOK.TOKdsymbol;
import static descent.internal.compiler.parser.TOK.TOKstructliteral;
import static descent.internal.compiler.parser.TOK.*;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;


public class DotVarExp extends UnaExp {

	public Declaration var;
	public boolean hasOverloads;
	public IdentifierExp ident; // Descent: for better error reporting
	
	public DotVarExp(Loc loc, Expression e, Declaration var) {
		this(loc, e, var, false);
	}

	public DotVarExp(Loc loc, Expression e, Declaration var, boolean hasOverloads) {
		super(loc, TOK.TOKdotvar, e);
		this.var = var;
		this.hasOverloads = hasOverloads;
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
	public Expression interpret(InterState istate, SemanticContext context) {
		Expression e = EXP_CANT_INTERPRET;

		Expression ex = e1.interpret(istate, context);
		if (ex != EXP_CANT_INTERPRET) {
			if (ex.op == TOKstructliteral) {
				StructLiteralExp se = (StructLiteralExp) ex;
				VarDeclaration v = var.isVarDeclaration();
				if (v != null) {
					e = se.getField(type, v.offset, context);
					if (null == e)
						e = EXP_CANT_INTERPRET;
					return e;
				}
			}
		}

		return e;
	}
	
	@Override
	public boolean isLvalue(SemanticContext context) {
		return true;
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
						if (context.acceptsErrors()) {
							context.acceptProblem(Problem.newSemanticTypeError(
									IProblem.CanOnlyInitiailizeConstMemberInsideConstructor, this, p, var.toChars(context), p));
						}
					}
				}
				break;
			}
		} else if (context.isD2()) {
			Type t1 = e1.type.toBasetype(context);

			if (!t1.isMutable() ||
			    (t1.ty == TY.Tpointer && !t1.nextOf().isMutable()) ||
			    !var.type.isMutable() ||
			    !var.type.isAssignable() ||
			    (var.storage_class & STC.STCmanifest) != 0
			   ) {
				if (context.acceptsErrors()) {
					context.acceptProblem(Problem.newSemanticTypeError(IProblem.CannotModifyConstInvariant, this, this.toChars(context)));
				}
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
				Expressions exps = new Expressions(tup.objects.size());
				exps.setDim(tup.objects.size());
				for (int i = 0; i < tup.objects.size(); i++) {
					ASTDmdNode o = tup.objects.get(i);
					if (o.dyncast() != DYNCAST.DYNCAST_EXPRESSION) {
						if (context.acceptsWarnings()) {
							context.acceptProblem(Problem.newSemanticTypeWarning(IProblem.SymbolNotAnExpression, 0, o.getStart(), o.getLength(), o.toChars(context)));
						}
					} else {
						Expression e = (Expression) o;
						if (e.op != TOKdsymbol) {
							if (context.acceptsErrors()) {
								context.acceptProblem(Problem.newSemanticTypeError(
										IProblem.SymbolIsNotAMember, this, e.toChars(context)));
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
			
			type = var.type;
			if (type == null && context.global.errors > 0) { // var is goofed up, just return 0
				return new IntegerExp(loc, 0);
			}
			Assert.isNotNull(type);

			if (var.isFuncDeclaration() == null) // for functions, do checks after overload resolution
			{
				if (context.isD2()) {
					Type t1 = e1.type;
					if (t1.ty == TY.Tpointer) {
						t1 = t1.nextOf();
					}
					if (t1.isConst()) {
						type = type.constOf(context);
					} else if (t1.isInvariant()) {
						type = type.invariantOf(context);
					}
				}
				
			    Dsymbol vparent = var.toParent();
			    AggregateDeclaration ad = vparent != null ? vparent.isAggregateDeclaration() : null;

			    if (context.isD2()) {
					boolean repeat = true;
				// L1:
					while(repeat) {
						repeat = false;
						Type t = e1.type.toBasetype(context);
	
						if (ad != null
								&& !(t.ty == TY.Tpointer
										&& ((TypePointer) t).next.ty == TY.Tstruct && ((TypeStruct) ((TypePointer) t).next).sym == ad)
								&& !(t.ty == TY.Tstruct && ((TypeStruct) t).sym == ad)) {
							ClassDeclaration cd = ad.isClassDeclaration();
							ClassDeclaration tcd = t.isClassHandle();
	
							if (null == cd
									|| null == tcd
									|| !(tcd == cd || cd.isBaseOf(tcd, null,
											context))) {
								if (tcd != null && tcd.isNested()) {
									// Try again with outer scope
	
									e1 = new DotVarExp(loc, e1, tcd.vthis);
									e1 = e1.semantic(sc, context);
	
									// Skip over nested functions, and get the
									// enclosing
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
									if (s != null && s.isClassDeclaration() != null)
										e1.type = s.isClassDeclaration().type;
	
									e1 = e1.semantic(sc, context); // get corrected nested refs
									// goto L1;
									repeat = true;
									continue;
								}
								if (context.acceptsErrors()) {
									context.acceptProblem(Problem.newSemanticTypeError(IProblem.SymbolForSymbolNeedsToBeType, this, e1.toChars(context), var.toChars(context),
											ad.toChars(context), t.toChars(context)));
								}
							}
						}
					}
				} else {
					e1 = getRightThis(loc, sc, ad, e1, var, context);
				}
			    if (0 == sc.noaccesscheck) {
			    	accessCheck(sc, e1, var, context, ident);
			    }
			    
			    if (context.isD2()) {
			    	
			    } else {
				    VarDeclaration v = var.isVarDeclaration();
					if (v != null && v.isConst()) {
						ExpInitializer ei = v.getExpInitializer(context);
						if (ei != null) {
							Expression e = ei.exp.copy();
							e = e.semantic(sc, context);
							return e;
						}
					}
			    }
			}
		}
		return this;
	}
	
	@Override
	public Expression optimize(int result, SemanticContext context) {
	    e1 = e1.optimize(result, context);

		if (context.isD2() && e1.op == TOKvar) {
			VarExp ve = (VarExp) e1;
			VarDeclaration v = ve.var.isVarDeclaration();
			Expression e = expandVar(result, v, context);
			if (e != null && e.op == TOKstructliteral) {
				StructLiteralExp sle = (StructLiteralExp) e;
				VarDeclaration vf = var.isVarDeclaration();
				if (vf != null) {
					e = sle.getField(type, vf.offset, context);
					if (e != null && e != EXP_CANT_INTERPRET)
						return e;
				}
			}
		} else if (e1.op == TOKstructliteral) {
			StructLiteralExp sle = (StructLiteralExp) e1;
			VarDeclaration vf = var.isVarDeclaration();
			if (vf != null) {
				Expression e = sle.getField(type, vf.offset, context);
				if (e != null && e != EXP_CANT_INTERPRET)
					return e;
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
