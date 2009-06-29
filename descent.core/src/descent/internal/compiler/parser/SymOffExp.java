package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.MATCH.MATCHexact;
import static descent.internal.compiler.parser.MATCH.MATCHnomatch;
import static descent.internal.compiler.parser.TY.Tdelegate;
import static descent.internal.compiler.parser.TY.Tfunction;
import static descent.internal.compiler.parser.TY.Tpointer;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;


public class SymOffExp extends SymbolExp {

	public integer_t offset;
	
	public SymOffExp(Loc loc, Declaration var, integer_t offset, SemanticContext context) {
		this(loc, var, offset, false, context);
	}
	
	public SymOffExp(Loc loc, Declaration var, integer_t offset, boolean hasOverloads, SemanticContext context) {
		super(loc, TOK.TOKsymoff, var, hasOverloads);
		Assert.isNotNull(var);
		this.offset = offset;
		VarDeclaration v = var.isVarDeclaration();
		if (v != null && v.needThis()) {
			if (context.acceptsErrors()) {
				context.acceptProblem(Problem.newSemanticTypeError(IProblem.NeedThisForAddressOfSymbol, this, new String[] { v.toChars(context) }));
			}
		}
	}

	public SymOffExp(Loc loc, Declaration var, int offset, SemanticContext context) {
		this(loc, var, new integer_t(offset), false, context);
	}
	
	public SymOffExp(Loc loc, Declaration var, int offset, boolean hasOverloads, SemanticContext context) {
		this(loc, var, new integer_t(offset), hasOverloads, context);
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}


	@Override
	public Expression castTo(Scope sc, Type t, SemanticContext context) {
		Expression e;
		
		if (context.isD1()) {
			Type tb;
	
			e = this;
	
			tb = t.toBasetype(context);
			type = type.toBasetype(context);
			if (!same(tb, type, context)) {
				// Look for pointers to functions where the functions are
				// overloaded.
				FuncDeclaration f;
	
				if (type.ty == Tpointer && type.next.ty == Tfunction
						&& tb.ty == Tpointer && tb.next.ty == Tfunction) {
					f = var.isFuncDeclaration();
					if (f != null) {
						f = f.overloadExactMatch(tb.next, context);
						if (f != null) {
							e = new SymOffExp(loc, f, 0, context);
							e.type = t;
							return e;
						}
					}
				}
				e = super.castTo(sc, t, context);
			}
			e.type = t;
		} else {
		    if (same(type, t, context) && !hasOverloads)
				return this;
			Type tb = t.toBasetype(context);
			Type typeb = type.toBasetype(context);
			if (tb != typeb) {
				// Look for pointers to functions where the functions are
				// overloaded.
				FuncDeclaration f;

				if (hasOverloads && typeb.ty == Tpointer
						&& typeb.nextOf().ty == Tfunction
						&& (tb.ty == Tpointer || tb.ty == Tdelegate)
						&& tb.nextOf().ty == Tfunction) {
					f = var.isFuncDeclaration();
					if (f != null) {
						f = f.overloadExactMatch(tb.nextOf(), context);
						if (f != null) {
							if (tb.ty == Tdelegate && f.needThis()
									&& hasThis(sc) != null) {
								e = new DelegateExp(loc, new ThisExp(loc), f);
								e = e.semantic(sc, context);
							} else if (tb.ty == Tdelegate && f.isNested()) {
								e = new DelegateExp(loc, new IntegerExp(0), f);
								e = e.semantic(sc, context);
							} else {
								e = new SymOffExp(loc, f, integer_t.ZERO,
										context);
								e.type = t;
							}
							f.tookAddressOf++;
							return e;
						}
					}
				}
				e = super.castTo(sc, t, context);
			} else {
				e = copy();
				e.type = t;
				((SymOffExp) e).hasOverloads = false;
			}
		}
		return e;
	}

	@Override
	public void checkEscape(SemanticContext context) {
		VarDeclaration v = var.isVarDeclaration();
		if (v != null) {
			if (!v.isDataseg(context)) {
				if (context.acceptsErrors()) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.EscapingReferenceToLocalVariable, this, new String[] { toChars(context) }));
				}
			}
		}
	}

	@Override
	public int getNodeType() {
		return SYM_OFF_EXP;
	}

	@Override
	public MATCH implicitConvTo(Type t, SemanticContext context) {
		MATCH result;

		result = type.implicitConvTo(t, context);

		if (result == MATCHnomatch) {
			// Look for pointers to functions where the functions are overloaded.
			FuncDeclaration f;

			t = t.toBasetype(context);
			
			if (context.isD1()) {
				if (type.ty == Tpointer && type.next.ty == Tfunction
						&& t.ty == Tpointer && t.next.ty == Tfunction) {
					f = var.isFuncDeclaration();
					if (f != null && f.overloadExactMatch(t.next, context) != null) {
						result = MATCHexact;
					}
				}
			} else {
				if (type.ty == Tpointer && type.nextOf().ty == Tfunction
						&& (t.ty == Tpointer || t.ty == Tdelegate)
						&& t.nextOf().ty == Tfunction) {
					f = var.isFuncDeclaration();
					if (f != null) {
						f = f.overloadExactMatch(t.nextOf(), context);
						if (f != null) {
							if ((t.ty == Tdelegate && (f.needThis() || f
									.isNested()))
									|| (t.ty == Tpointer && !(f.needThis() || f
											.isNested()))) {
								result = MATCHexact;
							}
						}
					}
				}
			}
		}
		return result;
	}

	@Override
	public boolean isBool(boolean result) {
		return result ? true : false;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		if (type == null) {
			type = var.type.pointerTo(context);
		}
		VarDeclaration v = var.isVarDeclaration();
		if (v != null) {
			v.checkNestedReference(sc, loc, context);
		}
		return this;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		if (!offset.equals(0)) {
			buf.writestring("(& ");
			buf.writestring(var.toChars(context));
			buf.writestring("+");
			buf.writestring(offset);
			buf.writestring(")");
		} else {
			buf.writestring("& " + var.toChars(context));
		}
	}
	
	@Override
	public boolean isConst() {
		return true;
	}

}
