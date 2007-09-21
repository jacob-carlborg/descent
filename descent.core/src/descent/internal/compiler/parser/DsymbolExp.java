package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.TOK.TOKstring;

import static descent.internal.compiler.parser.TY.Tsarray;

// DMD 1.020
public class DsymbolExp extends Expression {

	public Dsymbol s;

	public DsymbolExp(Loc loc, Dsymbol s) {
		super(loc, TOK.TOKdsymbol);
		this.s = s;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, s);
		}
		visitor.endVisit(this);
	}

	@Override
	public int getNodeType() {
		return DSYMBOL_EXP;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		// Lagain:
		EnumMember em;
		Expression e;
		VarDeclaration v;
		FuncDeclaration f;
		FuncLiteralDeclaration fld;
		// Declaration d;
		ClassDeclaration cd;
		ClassDeclaration thiscd = null;
		Import imp;
		Package pkg;
		Type t;

		boolean loop = true;
		Lagain: while (loop) {
			loop = false;

			if (type != null) {
				return this;
			}
			if (s.isFuncDeclaration() == null) {
				checkDeprecated(sc, s, context);
			}
			s = s.toAlias(context);
			if (s.isFuncDeclaration() == null) {
				checkDeprecated(sc, s, context);
			}

			if (sc.func != null) {
				thiscd = sc.func.parent.isClassDeclaration();
			}

			// BUG: This should happen after overload resolution for functions, not before
			if (s.needThis()) {
				if (hasThis(sc) != null /*&& !s.isFuncDeclaration()*/) {
					// Supply an implicit 'this', as in
					//	  this.ident

					DotVarExp de;

					de = new DotVarExp(loc, new ThisExp(loc), s.isDeclaration());
					return de.semantic(sc, context);
				}
			}

			em = s.isEnumMember();
			if (em != null) {
				e = em.value;
				e = e.semantic(sc, context);
				return e;
			}
			v = s.isVarDeclaration();
			if (v != null) {
				if (type == null) {
					type = v.type;
					if (v.type == null) {
						context.acceptProblem(Problem.newSemanticTypeError(
								IProblem.ForwardReference, 0, start, length,
								new String[] { v.toString() }));
						type = Type.terror;
					}
				}
				if (v.isConst() && type.toBasetype(context).ty != Tsarray) {
					if (v.init != null) {
						if (v.inuse != 0) {
							error("circular reference to '%s'", v
									.toChars(context));
							type = Type.tint32;
							return this;
						}
						ExpInitializer ei = v.init.isExpInitializer();
						if (ei != null) {
							e = ei.exp.copy(); // make copy so we can change loc
							if (e.op == TOKstring || e.type == null) {
								e = e.semantic(sc, context);
							}
							e = e.implicitCastTo(sc, type, context);
							e.loc = loc;
							return e;
						}
					} else {
						e = type.defaultInit(context);
						e.loc = loc;
						return e;
					}
				}
				e = new VarExp(loc, v);
				e.start = start;
				e.length = length;
				e.type = type;
				e = e.semantic(sc, context);
				return e.deref();
			}
			fld = s.isFuncLiteralDeclaration();
			if (fld != null) {
				e = new FuncExp(loc, fld);
				return e.semantic(sc, context);
			}
			f = s.isFuncDeclaration();
			if (f != null) {
				return new VarExp(loc, f);
			}
			cd = s.isClassDeclaration();
			if (cd != null && thiscd != null
					&& cd.isBaseOf(thiscd, null, context) && sc.func.needThis()) {
				// We need to add an implicit 'this' if cd is this class or a base class.
				DotTypeExp dte;

				dte = new DotTypeExp(loc, new ThisExp(loc), s);
				return dte.semantic(sc, context);
			}
			imp = s.isImport();
			if (imp != null) {
				ScopeExp ie;

				ie = new ScopeExp(loc, imp.pkg);
				return ie.semantic(sc, context);
			}
			pkg = s.isPackage();
			if (pkg != null) {
				ScopeExp ie;

				ie = new ScopeExp(loc, pkg);
				return ie.semantic(sc, context);
			}
			Module mod = s.isModule();
			if (mod != null) {
				ScopeExp ie;

				ie = new ScopeExp(loc, mod);
				return ie.semantic(sc, context);
			}

			t = s.getType();
			if (t != null) {
				return new TypeExp(loc, t);
			}

			TupleDeclaration tup = s.isTupleDeclaration();
			if (tup != null) {
				Expressions exps = new Expressions(tup.objects
						.size());
				for (int i = 0; i < tup.objects.size(); i++) {
					ASTDmdNode o = tup.objects.get(i);
					if (o.dyncast() != DYNCAST.DYNCAST_EXPRESSION) {
						context.acceptProblem(Problem.newSemanticTypeWarning(IProblem.SymbolNotAnExpression, 0, o.start, o.length, new String[] { o.toChars(context) }));
					} else {
						Expression e2 = (Expression) o;
						e2 = e2.syntaxCopy();
						exps.add(e2);
					}
				}
				e = new TupleExp(loc, exps);
				e = e.semantic(sc, context);
				return e;
			}

			TemplateInstance ti = s.isTemplateInstance();
			if (ti != null && context.global.errors == 0) {
				if (0 == ti.semanticdone) {
					ti.semantic(sc, context);
				}
				s = ti.inst.toAlias(context);
				if (s.isTemplateInstance() == null) {
					// goto Lagain;
					loop = true;
					continue Lagain;
				}
				e = new ScopeExp(loc, ti);
				e = e.semantic(sc, context);
				return e;
			}

			TemplateDeclaration td = s.isTemplateDeclaration();
			if (td != null) {
				e = new TemplateExp(loc, td);
				e = e.semantic(sc, context);
				return e;
			}

		}

		// Lerr:
		error("%s '%s' is not a variable", s.kind(), s.toChars(context));
		type = Type.terror;
		return this;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring(s.toChars(context));
	}

	@Override
	public String toChars(SemanticContext context) {
		return s.toChars(context);
	}

	@Override
	public Expression toLvalue(Scope sc, Expression e, SemanticContext context) {
		return this;
	}

}
