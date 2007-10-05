package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

import static descent.internal.compiler.parser.MATCH.MATCHexact;
import static descent.internal.compiler.parser.MATCH.MATCHnomatch;

import static descent.internal.compiler.parser.STC.STCconst;
import static descent.internal.compiler.parser.STC.STCtemplateparameter;

// DMD 1.020
public class TemplateValueParameter extends TemplateParameter {

	public static Expression edummy = null;

	public Type valType, sourceValType;
	public Expression specValue, sourceSpecValue;
	public Expression defaultValue, sourceDefaultValue;

	public TemplateValueParameter(Loc loc, IdentifierExp ident, Type valType,
			Expression specValue, Expression defaultValue) {
		super(loc, ident);
		this.valType = valType;
		this.specValue = specValue;
		this.defaultValue = defaultValue;
		
		this.sourceValType = valType;
		this.sourceSpecValue = specValue;
		this.sourceDefaultValue = defaultValue;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, valType);
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, specValue);
			TreeVisitor.acceptChildren(visitor, defaultValue);
		}
		visitor.endVisit(this);
	}

	@Override
	public void declareParameter(Scope sc, SemanticContext context) {
		VarDeclaration v = new VarDeclaration(loc, valType, ident, null);
		v.storage_class = STCtemplateparameter;
		if (null == sc.insert(v)) {
			context.acceptProblem(Problem.newSemanticTypeError(IProblem.ParameterMultiplyDefined, 0, ident.start, ident.length, new String[] { new String(ident.ident) }));
		}
		sparam = v;
	}

	@Override
	public ASTDmdNode defaultArg(Scope sc, SemanticContext context) {
		Expression e;

		e = defaultValue;
		if (e != null) {
			e = e.syntaxCopy();
			e = e.semantic(sc, context);
		}
		return e;
	}

	@Override
	public ASTDmdNode dummyArg(SemanticContext context) {
		Expression e;

		e = specValue;
		if (null == e) {
			// Create a dummy value
			if (null == edummy) {
				edummy = valType.defaultInit(context);
			}
			e = edummy;
		}
		return e;
	}

	@Override
	public int getNodeType() {
		return TEMPLATE_VALUE_PARAMETER;
	}

	@Override
	public TemplateValueParameter isTemplateValueParameter() {
		return this;
	}

	@Override
	public MATCH matchArg(Scope sc, Objects tiargs, int i,
			TemplateParameters parameters, Objects dedtypes,
			Declaration[] psparam, SemanticContext context) {
		Initializer init;
		Declaration sparam;
		MATCH m = MATCHexact;
		Expression ei;
		ASTDmdNode oarg;

		if (i < tiargs.size()) {
			oarg = tiargs.get(i);
		} else { // Get default argument instead
			oarg = defaultArg(sc, context);
			if (null == oarg) {
				if (!(i < dedtypes.size())) {
					throw new IllegalStateException("assert(i < dedtypes.dim);");
				}
				// It might have already been deduced
				oarg = dedtypes.get(i);
				if (null == oarg) {
					// goto Lnomatch;
					psparam = null;
					return MATCHnomatch;
				}
			}
		}

		ei = isExpression(oarg);
		Type vt;

		if (null == ei && oarg != null) {
			// goto Lnomatch;
			psparam = null;
			return MATCHnomatch;
		}

		if (specValue != null) {
			if (null == ei || ei == edummy) {
				// goto Lnomatch;
				psparam = null;
				return MATCHnomatch;
			}

			Expression e = specValue;

			e = e.semantic(sc, context);
			e = e.implicitCastTo(sc, valType, context);
			e = e.optimize(WANTvalue | WANTinterpret, context);

			ei = ei.syntaxCopy();
			ei = ei.semantic(sc, context);
			ei = ei.optimize(WANTvalue | WANTinterpret, context);
			if (!ei.equals(e)) {
				// goto Lnomatch;
				psparam = null;
				return MATCHnomatch;
			}
		} else if (dedtypes.get(i) != null) { // Must match already deduced value
			Expression e = (Expression) dedtypes.get(i);

			if (null == ei || !ei.equals(e)) {
				// goto Lnomatch;
				psparam = null;
				return MATCHnomatch;
			}
		}
		vt = valType.semantic(Loc.ZERO, sc, context);
		if (ei.type != null) {
			m = ei.implicitConvTo(vt, context);
			if (null == m) {
				// goto Lnomatch;
				psparam = null;
				return MATCHnomatch;
			}
		}
		dedtypes.set(i, ei);

		init = new ExpInitializer(loc, ei);
		sparam = new VarDeclaration(loc, vt, ident, init);
		sparam.storage_class = STCconst;
		psparam[0] = sparam;
		return m;
	}

	@Override
	public int overloadMatch(TemplateParameter tp) {
		TemplateValueParameter tvp = tp.isTemplateValueParameter();

		if (tvp != null) {
			if (valType != tvp.valType) {
				return 0;
			}

			if (valType != null && !valType.equals(tvp.valType)) {
				return 0;
			}

			if (specValue != tvp.specValue) {
				return 0;
			}

			return 1; // match
		}

		return 0;
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		sparam.semantic(sc, context);
		valType = valType.semantic(loc, sc, context);
		if (!(valType.isintegral() || valType.isfloating() || valType
				.isString(context))
				&& valType.ty != TY.Tident) {
			error(
					"arithmetic/string type expected for value-parameter, not %s",
					valType.toChars(context));
		}

		if (specValue != null) {
			Expression e = specValue;

			e = e.semantic(sc, context);
			e = e.implicitCastTo(sc, valType, context);
			e = e.optimize(ASTDmdNode.WANTvalue | ASTDmdNode.WANTinterpret,
					context);
			if (e.op == TOK.TOKint64 || e.op == TOK.TOKfloat64
					|| e.op == TOK.TOKcomplex80 || e.op == TOK.TOKnull
					|| e.op == TOK.TOKstring) {
				specValue = e;
			}
		}
	}

	@Override
	public ASTDmdNode specialization() {
		return specValue;
	}

	@Override
	public TemplateParameter syntaxCopy() {
		TemplateValueParameter tp = new TemplateValueParameter(loc, ident,
				valType, specValue, defaultValue);
		tp.valType = valType.syntaxCopy();
		if (specValue != null) {
			tp.specValue = specValue.syntaxCopy();
		}
		if (defaultValue != null) {
			tp.defaultValue = defaultValue.syntaxCopy();
		}
		return tp;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		valType.toCBuffer(buf, ident, hgs, context);
		if (specValue != null) {
			buf.writestring(" : ");
			specValue.toCBuffer(buf, hgs, context);
		}
		if (defaultValue != null) {
			buf.writestring(" = ");
			defaultValue.toCBuffer(buf, hgs, context);
		}
	}

}
