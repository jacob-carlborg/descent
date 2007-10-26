package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.STC.STCfield;
import static descent.internal.compiler.parser.STC.STClazy;
import static descent.internal.compiler.parser.STC.STCvariadic;
import static descent.internal.compiler.parser.TOK.TOKvar;
import static descent.internal.compiler.parser.TY.Tarray;
import static descent.internal.compiler.parser.TY.Tclass;
import static descent.internal.compiler.parser.TY.Tsarray;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class VarExp extends Expression {

	public Declaration var;

	public VarExp(Loc loc, Declaration var) {
		super(loc, TOK.TOKvar);
		this.var = var;
		this.type = var.type;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

	@Override
	public void checkEscape(SemanticContext context) {
		VarDeclaration v = var.isVarDeclaration();
		if (v != null) {
			Type tb = v.type.toBasetype(context);
			// if reference type
			if (tb.ty == Tarray || tb.ty == Tsarray || tb.ty == Tclass) {
				if ((v.isAuto() || v.isScope()) && !v.noauto) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.EscapingReferenceToAutoLocal, 0, start,
							length, new String[] { v.toChars(context) }));
				} else if ((v.storage_class & STCvariadic) != 0) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.EscapingReferenceToVariadicParameter, 0, start,
							length, new String[] { v.toChars(context) }));
				}
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o instanceof Expression) {
			if (((Expression) o).op == TOKvar) {
				VarExp ne = (VarExp) o;
				// TODO semantic check if this is OK
				return type.equals(ne.type) && var.equals(ne.var);
			}
		}

		return false;
	}

	@Override
	public int getNodeType() {
		return VAR_EXP;
	}

	@Override
	public Expression interpret(InterState istate, SemanticContext context)
	{
	    return getVarExp(loc, istate, var, context);
	}

	@Override
	public Expression modifiableLvalue(Scope sc, Expression e,
			SemanticContext context) {
		if (sc.incontract != 0 && var.isParameter()) {
			// TODO the start and length of the problem should be different (should be passed by parameter to this function)
			context.acceptProblem(Problem.newSemanticTypeError(IProblem.CannotModifyParameterInContract, 0, start, length, new String[] { var.toChars(context) }));
		}

		if (type != null && type.toBasetype(context).ty == Tsarray) {
			// TODO the start and length of the problem should be different (should be passed by parameter to this function)
			context.acceptProblem(Problem.newSemanticTypeError(IProblem.CannotChangeReferenceToStaticArray, 0, start, length, new String[] { var.toChars(context) }));
		}

		VarDeclaration v = var.isVarDeclaration();
		if (v != null
				&& v.canassign == 0
				&& (var.isConst() || (context.global.params.Dversion > 1 && var
						.isFinal()))) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.CannotModifyFinalVariable, 0, start,
					length, new String[] { var.toChars(context) }));
		}

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
						&& fd.toParent() == var.toParent()) {
					VarDeclaration v2 = var.isVarDeclaration();
					Assert.isNotNull(v2);
					v2.ctorinit = true;
				} else {
					if (s != null) {
						s = s.toParent2();
						continue;
					} else {
						/* TODO semantic
						 const char *p = var.isStatic() ? "static " : "";
						 error("can only initialize %sconst %s inside %sconstructor",
						 p, var.toChars(), p);
						 */
					}
				}
				break;
			}
		}

		// See if this expression is a modifiable lvalue (i.e. not const)
		return toLvalue(sc, e, context);
	}

	@Override
	public Expression optimize(int result, SemanticContext context)
	{
		if((result & WANTinterpret) > 0)
		{
			return fromConstInitializer(this, context);
		}
		return this;
	}

	@Override
	public void scanForNestedRef(Scope sc, SemanticContext context)
	{
		//printf("VarExp.scanForNestedRef(%s)\n", toChars());
		VarDeclaration v = var.isVarDeclaration();
		if(null != v)
			v.checkNestedReference(sc, Loc.ZERO, context);
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		if (type == null) {
			type = var.type;
		}

		VarDeclaration v = var.isVarDeclaration();
		if (v != null) {
			if (v.isConst() && type.toBasetype(context).ty != TY.Tsarray
					&& v.init != null) {
				ExpInitializer ei = v.init.isExpInitializer();
				if (ei != null) {
					return ei.exp.implicitCastTo(sc, type, context);
				}
			}
			v.checkNestedReference(sc, loc, context);
		}
		return this;
	}
	
	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring(var.toChars(context));
	}

	@Override
	public String toChars(SemanticContext context) {
		return var.toChars(context);
	}

	@Override
	public Expression toLvalue(Scope sc, Expression e, SemanticContext context) {
		if ((var.storage_class & STClazy) != 0) {
			context.acceptProblem(Problem.newSemanticTypeError(IProblem.LazyVariablesCannotBeLvalues, 0, start, length));
		}
		return this;
	}
	
	//PERHAPS void dump(int indent);
	//PERHAPS int inlineCost(InlineCostState *ics);
	//PERHAPS Expression *doInline(InlineDoState *ids);
}
