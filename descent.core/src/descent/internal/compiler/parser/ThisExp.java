package descent.internal.compiler.parser;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class ThisExp extends Expression {

	public Declaration var;

	public ThisExp(Loc loc) {
		super(loc, TOK.TOKthis);
		this.var = null;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

	@Override
	public Expression doInline(InlineDoState ids) {
		if (ids.vthis == null) {
			return this;
		}

		VarExp ve = new VarExp(loc, ids.vthis);
		ve.type = type;
		return ve;
	}

	@Override
	public int getNodeType() {
		return THIS_EXP;
	}

	@Override
	public int inlineCost(InlineCostState ics, SemanticContext context) {
		FuncDeclaration fd = ics.fd;
		if (!ics.hdrscan) {
			if (fd.isNested() || !ics.hasthis) {
				return COST_MAX;
			}
		}
		return 1;
	}

	@Override
	public boolean isBool(boolean result) {
		return result ? true : false;
	}

	@Override
	public void scanForNestedRef(Scope sc, SemanticContext context) {
		if (var == null) {
			throw new IllegalStateException("assert(var);");
		}
		var.isVarDeclaration().checkNestedReference(sc, Loc.ZERO, context);
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		IFuncDeclaration fd;
		@SuppressWarnings("unused")
		IFuncDeclaration fdthis;
		@SuppressWarnings("unused")
		int nested = 0;

		if (type != null) { // assert(global.errors || var);
			return this;
		}

		/*
		 * Special case for typeof(this) and typeof(super) since both should
		 * work even if they are not inside a non-static member function
		 */
		if (sc.intypeof != 0) {
			// Find enclosing struct or class
			for (IDsymbol s = sc.parent; true; s = s.parent()) {
				IClassDeclaration cd;
				IStructDeclaration sd;

				if (s == null) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.ThisNotInClassOrStruct, this));
					// goto Lerr;
					return semantic_Lerr(sc, context);
				}
				cd = s.isClassDeclaration();
				if (cd != null) {
					type = cd.type();
					return this;
				}
				sd = s.isStructDeclaration();
				if (sd != null) {
					type = sd.type().pointerTo(context);
					return this;
				}
			}
		}

		fdthis = sc.parent.isFuncDeclaration();
		fd = hasThis(sc); // fd is the uplevel function with the 'this'
		// variable
		if (fd == null) {
			// goto Lerr;
			return semantic_Lerr(sc, context);
		}

		Assert.isNotNull(fd.vthis());
		var = fd.vthis();
		Assert.isNotNull(var.parent);
		type = var.type;
		var.isVarDeclaration().checkNestedReference(sc, loc, context);
		sc.callSuper |= Scope.CSXthis;
		return this;
	}

	public Expression semantic_Lerr(Scope sc, SemanticContext context) {
		context.acceptProblem(Problem.newSemanticTypeError(
				IProblem.ThisOnlyAllowedInNonStaticMemberFunctions, this));
		type = Type.tint32;
		return this;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("this");
	}

	@Override
	public Expression toLvalue(Scope sc, Expression e, SemanticContext context) {
		return this;
	}

}
