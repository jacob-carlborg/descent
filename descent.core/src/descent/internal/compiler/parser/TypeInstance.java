package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

import static descent.internal.compiler.parser.MATCH.MATCHnomatch;

import static descent.internal.compiler.parser.TY.Tident;
import static descent.internal.compiler.parser.TY.Tinstance;

// DMD 1.020
public class TypeInstance extends TypeQualified {

	public TemplateInstance tempinst;

	public TypeInstance(Loc loc, TemplateInstance tempinst) {
		super(loc, TY.Tinstance);
		this.tempinst = tempinst;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, tempinst);
		}
		visitor.endVisit(this);
	}

	@Override
	public MATCH deduceType(Scope sc, Type tparam,
			TemplateParameters parameters, Objects dedtypes,
			SemanticContext context) {
		// Extra check
		if (tparam != null && tparam.ty == Tinstance) {
			TypeInstance tp = (TypeInstance) tparam;

			if (null == tp.tempinst.tempdecl) {
				if (!tp.tempinst.name.equals(tempinst.name)) {
					return MATCHnomatch;
				}
			} else if (tempinst.tempdecl != tp.tempinst.tempdecl) {
				return MATCHnomatch;
			}

			for (int i = 0; i < tempinst.tiargs.size(); i++) {
				ASTDmdNode o1 = tempinst.tiargs.get(i);
				ASTDmdNode o2 = tp.tempinst.tiargs.get(i);

				Type t1 = isType(o1);
				Type t2 = isType(o2);

				Expression e1 = isExpression(o1);
				Expression e2 = isExpression(o2);

				if (t1 != null && t2 != null) {
					if (t1.deduceType(sc, t2, parameters, dedtypes, context) == MATCHnomatch) {
						return MATCHnomatch;
					}
				} else if (e1 != null && e2 != null) {
					if (!e1.equals(e2)) {
						return MATCHnomatch;
					}
				} else if (e1 != null && t2 != null && t2.ty == Tident) {
					int j = templateParameterLookup(t2, parameters);
					if (j == -1) {
						return MATCHnomatch;
					}
					TemplateParameter tp2 = parameters.get(j);
					// BUG: use tp2.matchArg() instead of the following
					TemplateValueParameter tv = tp2.isTemplateValueParameter();
					if (null == tv) {
						return MATCHnomatch;
					}
					Expression e = (Expression) dedtypes.get(j);
					if (e != null) {
						if (!e1.equals(e)) {
							return MATCHnomatch;
						}
					} else {
						Type vt = tv.valType.semantic(Loc.ZERO, sc, context);
						MATCH m = e1.implicitConvTo(vt, context);
						if (m == MATCHnomatch) {
							return MATCHnomatch;
						}
						dedtypes.set(j, e1);
					}
				}
				// BUG: Need to handle alias and tuple parameters
				else {
					return MATCHnomatch;
				}
			}
		}
		return super.deduceType(sc, tparam, parameters, dedtypes, context);
	}

	@Override
	public int getNodeType() {
		return TYPE_INSTANCE;
	}

	@Override
	public void resolve(Loc loc, Scope sc, Expression[] pe, Type[] pt,
			Dsymbol[] ps, SemanticContext context) {
		// Note close similarity to TypeIdentifier::resolve()

		Dsymbol s;

		pe[0] = null;
		pt[0] = null;
		ps[0] = null;

		s = tempinst;
		if (s != null) {
			s.semantic(sc, context);
		}
		resolveHelper(loc, sc, s, null, pe, pt, ps, context);
	}

	@Override
	public Type semantic(Loc loc, Scope sc, SemanticContext context) {
		Type[] t = { null };
		Expression[] e = { null };
		Dsymbol[] s = { null };

		if (sc.parameterSpecialization != 0) {
			int errors = context.global.errors;
			context.global.gag++;

			resolve(loc, sc, e, t, s, context);

			context.global.gag--;
			if (errors != context.global.errors) {
				if (context.global.gag == 0) {
					context.global.errors = errors;
				}
				return this;
			}
		} else {
			resolve(loc, sc, e, t, s, context);
		}

		if (null == t[0]) {
			context.acceptProblem(Problem.newSemanticTypeError(IProblem.UsedAsAType, this, new String[] { toChars(context) }));
			t[0] = tvoid;
		}
		return t[0];
	}

	@Override
	public Type syntaxCopy() {
		TypeInstance t;

		t = new TypeInstance(loc, (TemplateInstance) tempinst.syntaxCopy(null));
		t.syntaxCopyHelper(this);
		return t;
	}

	@Override
	public void toCBuffer2(OutBuffer buf, IdentifierExp ident, HdrGenState hgs,
			SemanticContext context) {
		OutBuffer tmp = new OutBuffer();

		tempinst.toCBuffer(tmp, hgs, context);
		toCBuffer2Helper(tmp, null, hgs, context);
		buf.prependstring(tmp.toChars());
		if (ident != null) {
			buf.writeByte(' ');
			buf.writestring(ident.toChars());
		}
	}

}
