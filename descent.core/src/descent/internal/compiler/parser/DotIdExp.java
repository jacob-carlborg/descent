package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.CharOperation;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.TOK.TOKdotexp;
import static descent.internal.compiler.parser.TOK.TOKimport;
import static descent.internal.compiler.parser.TOK.TOKsuper;
import static descent.internal.compiler.parser.TOK.TOKthis;
import static descent.internal.compiler.parser.TOK.TOKtuple;

import static descent.internal.compiler.parser.TY.Tpointer;

// DMD 1.020
public class DotIdExp extends UnaExp {

	public IdentifierExp ident;

	public DotIdExp(Loc loc, Expression e, IdentifierExp id) {
		super(loc, TOK.TOKdot, e);
		this.ident = id;
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
		return DOT_ID_EXP;
	}
	
	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		Expression e;
		Expression eleft;
		Expression eright;

		/* Special case: rewrite this.id and super.id
		 * to be classtype.id and baseclasstype.id
		 * if we have no this pointer.
		 */
		if ((e1.op == TOKthis || e1.op == TOKsuper) && hasThis(sc) == null) {
			ClassDeclaration cd;
			StructDeclaration sd;
			AggregateDeclaration ad;

			ad = sc.getStructClassScope();
			if (ad != null) {
				cd = ad.isClassDeclaration();
				if (cd != null) {
					if (e1.op == TOKthis) {
						e = new TypeDotIdExp(loc, cd.type, ident);
						return e.semantic(sc, context);
					} else if (cd.baseClass != null && e1.op == TOKsuper) {
						e = new TypeDotIdExp(loc, cd.baseClass.type, ident);
						return e.semantic(sc, context);
					}
				} else {
					sd = ad.isStructDeclaration();
					if (sd != null) {
						if (e1.op == TOKthis) {
							e = new TypeDotIdExp(loc, sd.type, ident);
							return e.semantic(sc, context);
						}
					}
				}
			}
		}

		super.semantic(sc, context);

		if (e1.op == TOKdotexp) {
			DotExp de = (DotExp) e1;
			eleft = de.e1;
			eright = de.e2;
		} else {
			e1 = resolveProperties(sc, e1, context);
			eleft = null;
			eright = e1;
		}

		if (e1.op == TOKtuple && !CharOperation.equals(ident.ident, Id.length)) {
			TupleExp te = (TupleExp) e1;
			e = new IntegerExp(loc, te.exps.size(), Type.tsize_t);
			return e;
		}
		
		assignBinding();

		if (eright.op == TOKimport) // also used for template alias's
		{
			Dsymbol s;
			ScopeExp ie = (ScopeExp) eright;

			s = ie.sds.search(loc, ident, 0, context);
			if (s != null) {
				s = s.toAlias(context);
				checkDeprecated(sc, s, context);

				EnumMember em = s.isEnumMember();
				if (em != null) {
					e = em.value;
					e = e.semantic(sc, context);
					return e;
				}

				VarDeclaration v = s.isVarDeclaration();
				if (v != null) {
					if (v.inuse != 0) {
						context.acceptProblem(Problem.newSemanticTypeError(IProblem.CircularReferenceTo, 0, start, length, new String[] { v.toChars(context) }));
						type = Type.tint32;
						return this;
					}
					type = v.type;
					if (v.isConst()) {
						if (v.init != null) {
							ExpInitializer ei = v.init.isExpInitializer();
							if (ei != null) {
								if (ei.exp.type == type) {
									e = ei.exp.copy(); // make copy so we can change loc
									e.loc = loc;
									return e;
								}
							}
						} else if (type.isscalar(context)) {
							e = type.defaultInit(context);
							e.loc = loc;
							return e;
						}
					}
					if (v.needThis()) {
						if (eleft == null) {
							eleft = new ThisExp(loc);
						}
						e = new DotVarExp(loc, eleft, v);
						e = e.semantic(sc, context);
					} else {
						e = new VarExp(loc, v);
						if (eleft != null) {
							e = new CommaExp(loc, eleft, e);
							e.type = v.type;
						}
					}
					return e.deref();
				}

				FuncDeclaration f = s.isFuncDeclaration();
				if (f != null) {
					if (f.needThis()) {
						if (eleft == null) {
							eleft = new ThisExp(loc);
						}
						e = new DotVarExp(loc, eleft, f);
						e = e.semantic(sc, context);
					} else {
						e = new VarExp(loc, f);
						if (eleft != null) {
							e = new CommaExp(loc, eleft, e);
							e.type = f.type;
						}
					}
					return e;
				}

				Type t = s.getType();
				if (t != null) {
					return new TypeExp(loc, t);
				}

				ScopeDsymbol sds = s.isScopeDsymbol();
				if (sds != null) {
					e = new ScopeExp(loc, sds);
					e = e.semantic(sc, context);
					if (eleft != null) {
						e = new DotExp(loc, eleft, e);
					}
					return e;
				}

				Import imp = s.isImport();
				if (imp != null) {
					ScopeExp ie2;

					ie2 = new ScopeExp(loc, imp.pkg);
					return ie2.semantic(sc, context);
				}

				throw new IllegalStateException("assert(0);");
			}
			else if (CharOperation.equals(ident.ident, Id.stringof))
			{   String s2 = ie.toChars(context);
			    e = new StringExp(loc, s2.toCharArray(), 'c');
			    e = e.semantic(sc, context);
			    return e;
			}

			error("undefined identifier %s", toChars(context));
			type = Type.tvoid;
			return this;
		} else if (e1.type.ty == Tpointer && !CharOperation.equals(ident.ident, Id.init)
				&& !CharOperation.equals(ident.ident, Id.__sizeof) && !CharOperation.equals(ident.ident, Id.alignof)
				&& !CharOperation.equals(ident.ident, Id.offsetof) && !CharOperation.equals(ident.ident, Id.mangleof)
				&& !CharOperation.equals(ident.ident, Id.stringof)) {
			e = new PtrExp(loc, e1);
			e.type = e1.type.next;
			return e.type.dotExp(sc, e, ident, context);
		} else {
			e = e1.type.dotExp(sc, e1, ident, context);			
			e = e.semantic(sc, context);
			ident.setBinding(e.getBinding());
			return e;
		}
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		expToCBuffer(buf, hgs, e1, PREC.PREC_primary, context);
	    buf.writeByte('.');
	    buf.writestring(ident.toChars());
	}

}
