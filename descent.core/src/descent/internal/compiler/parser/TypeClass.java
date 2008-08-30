package descent.internal.compiler.parser;

import descent.core.IJavaElement;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.DYNCAST.DYNCAST_IDENTIFIER;

import static descent.internal.compiler.parser.MATCH.MATCHconvert;
import static descent.internal.compiler.parser.MATCH.MATCHexact;
import static descent.internal.compiler.parser.MATCH.MATCHnomatch;

import static descent.internal.compiler.parser.TOK.TOKdotexp;
import static descent.internal.compiler.parser.TOK.TOKdottype;
import static descent.internal.compiler.parser.TOK.TOKimport;
import static descent.internal.compiler.parser.TOK.TOKtype;

import static descent.internal.compiler.parser.TY.Tclass;
import static descent.internal.compiler.parser.TY.Tinstance;
import static descent.internal.compiler.parser.TY.Tpointer;
import static descent.internal.compiler.parser.TY.Tvoid;


public class TypeClass extends Type {

	public ClassDeclaration sym;

	public TypeClass(ClassDeclaration sym) {
		super(TY.Tclass, null);
		this.sym = sym;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

	@Override
	public boolean checkBoolean(SemanticContext context) {
		return true;
	}

	@Override
	public MATCH deduceType(Scope sc, Type tparam,
			TemplateParameters parameters, Objects dedtypes,
			SemanticContext context) {
		/* If this class is a template class, and we're matching
		 * it against a template instance, convert the class type
		 * to a template instance, too, and try again.
		 */
		TemplateInstance ti = sym.parent.isTemplateInstance();

		if (tparam != null && tparam.ty == Tinstance) {
			if (ti != null && ti.toAlias(context) == sym) {
				TypeInstance t = new TypeInstance(Loc.ZERO, ti);
				return t.deduceType(sc, tparam, parameters, dedtypes, context);
			}

			/* Match things like:
			 *  S!(T).foo
			 */
			TypeInstance tpi = (TypeInstance) tparam;
			if (tpi.idents.size() != 0) {
				IdentifierExp id = tpi.idents.get(tpi.idents.size() - 1);
				if (id.dyncast() == DYNCAST_IDENTIFIER && equals(sym.ident, id)) {
					Type tparent = sym.parent.getType(context);
					if (tparent != null) {
						/* Slice off the .foo in S!(T).foo
						 */
						// TODO semantic
						// tpi.idents.size()--;
						MATCH m = tparent.deduceType(sc, tpi, parameters,
								dedtypes, context);
						// TODO semantic
						// tpi.idents.size()++;
						return m;
					}
				}
			}
		}

		// Extra check
		if (tparam != null && tparam.ty == Tclass) {
			TypeClass tp = (TypeClass) tparam;

			return implicitConvTo(tp, context);
		}
		return super.deduceType(sc, tparam, parameters, dedtypes, context);
	}

	@Override
	public Expression defaultInit(Loc loc, SemanticContext context) {
		Expression e;
		e = new NullExp(loc);
		e.type = this;
		return e;
	}

	@Override
	public Expression dotExp(Scope sc, Expression e, IdentifierExp ident,
			SemanticContext context) {
		VarDeclaration v;
		Dsymbol s;
		DotVarExp de;
		Declaration d;

		boolean gotoL1 = false;
		if (e.op == TOKdotexp) {
			DotExp de_ = (DotExp) e;

			if (de_.e1.op == TOKimport) {
				ScopeExp se = (ScopeExp) de_.e1;

				s = se.sds.search(e.loc, ident, 0, context);
				e = de_.e1;
				//goto L1;
				gotoL1 = true;
			} else {
				s = sym.search(e.loc, ident, 0, context);
			}
		} else {
			// Ident may be null if completing (Foo).|
			if (ident == null) {
				return e;
			}
			s = sym.search(e.loc, ident, 0, context);
		}

		if (equals(ident, Id.tupleof) && !gotoL1) {
			/* Create a TupleExp
			 */
			e = e.semantic(sc, context);	// do this before turning on noaccesscheck
			
			Expressions exps = new Expressions(sym.fields.size());
			for (VarDeclaration v_ : sym.fields) {
				Expression fe = new DotVarExp(e.loc, e, v_);
				exps.add(fe);
			}
			e = new TupleExp(e.loc, exps);
			sc = sc.push();
			sc.noaccesscheck = 1;
			e = e.semantic(sc, context);
			sc.pop();
			return e;
		}

		gotoL1 = true;
		// L1:
		while(gotoL1) {
			gotoL1 = false;
			
			if (null == s) {
				// See if it's a base class
				ClassDeclaration cbase;
				for (cbase = sym.baseClass; null != cbase; cbase = cbase.baseClass) {
					if (equals(ident, cbase.ident)) {
						e = new DotTypeExp(Loc.ZERO, e, cbase, context);
						return e;
					}
				}
	
				if (equals(ident, Id.classinfo)) {
					Type t;
	
					if (context.ClassDeclaration_classinfo == null) {
						throw new IllegalStateException(
								"assert(ClassDeclaration.classinfo);");
					}
					t = context.ClassDeclaration_classinfo.type;
					if (e.op == TOKtype || e.op == TOKdottype) {
						/* 
						 * For type.classinfo, we know the classinfo at compile time.
						 */
						if (sym.vclassinfo == null) {
							sym.vclassinfo = new ClassInfoDeclaration(sym, context);
						}
						e = new VarExp(e.loc, sym.vclassinfo);
						e = e.addressOf(sc, context);
						e.type = t; // do this so we don't get redundant dereference
					} else {
					    /* 
					     * For class objects, the classinfo reference is the first
						 * entry in the vtbl[]
						 */
						e = new PtrExp(e.loc, e);
						e.type = t.pointerTo(context);
						if (sym.isInterfaceDeclaration() != null) {
							if (sym.isCOMinterface()) {
							    /* COM interface vtbl[]s are different in that the
								 * first entry is always pointer to QueryInterface().
								 * We can't get a .classinfo for it.
								 */
								if (context.acceptsErrors()) {
									context.acceptProblem(Problem.newSemanticTypeError(
											IProblem.NoClassInfoForComInterfaceObjects, this));
								}
							}
						    /* 
						     * For an interface, the first entry in the vtbl[]
						     * is actually a pointer to an instance of struct Interface.
						     * The first member of Interface is the .classinfo,
						     * so add an extra pointer indirection.
						     */
							e.type = e.type.pointerTo(context);
							e = new PtrExp(e.loc, e);
							e.type = t.pointerTo(context);
						}
						e = new PtrExp(e.loc, e, t);
					}
					return e;
				}
				
				if (equals(ident, Id.__vptr))
				{   /* The pointer to the vtbl[]
				     * *cast(void***)e
				     */
				    e = e.castTo(sc, context.Type_tvoidptr.pointerTo(context).pointerTo(context), context);
				    e = new PtrExp(e.loc, e);
				    e = e.semantic(sc, context);
				    return e;
				}

				if (equals(ident, Id.__monitor))
				{   /* The handle to the monitor (call it a void*)
				     * *(cast(void**)e + 1)
				     */
				    e = e.castTo(sc, context.Type_tvoidptr.pointerTo(context), context);
				    e = new AddExp(e.loc, e, new IntegerExp(1));
				    e = new PtrExp(e.loc, e);
				    e = e.semantic(sc, context);
				    return e;
				}
	
				if (equals(ident, Id.typeinfo)) {
					if (!context.global.params.useDeprecated) {
						if (context.acceptsErrors()) {
							context.acceptProblem(Problem.newSemanticTypeError(
									IProblem.DeprecatedProperty, ident, new String[] { "typeinfo",
											".typeid(type)" }));
						}
					}
					return getTypeInfo(sc, context);
				}
	
				if (equals(ident, Id.outer)
						&& null != sym.vthis) {
					s = sym.vthis;
				} else {
					//return getProperty(e.loc, ident);
					return super.dotExp(sc, e, ident, context);
				}
			}
			
			// Descent: for binding resolution
			ident.resolvedSymbol = s;
	
			if (null == s.isFuncDeclaration()) {	// because of overloading
				s.checkDeprecated(sc, context, ident);
			}
			
			s = s.toAlias(context);
			v = s.isVarDeclaration();
			if (null != v && v.isConst()) {
				ExpInitializer ei = v.getExpInitializer(context);
	
				if (null != ei) {
					e = ei.exp.copy(); // need to copy it if it's a StringExp
					e = e.semantic(sc, context);
					return e;
				}
			}
	
			if (null != s.getType(context)) {
				return new TypeExp(e.loc, s.getType(context));
			}
	
			EnumMember em = s.isEnumMember();
			if (null != em) {
				assert (null != em.value());
				return em.value().copy();
			}
	
			TemplateMixin tm = s.isTemplateMixin();
			if (null != tm) {
				Expression de_;
	
				de_ = new DotExp(e.loc, e, new ScopeExp(e.loc, tm));
				de_.type = e.type;
				return de_;
			}
	
			TemplateDeclaration td = s.isTemplateDeclaration();
			if (null != td) {
				e = new DotTemplateExp(e.loc, e, td);
				e.semantic(sc, context);
				return e;
			}
			
		    TemplateInstance ti = s.isTemplateInstance();
			if (ti != null) {
				if (0 == ti.semanticdone) {
					ti.semantic(sc, context);
				}
				s = ti.inst.toAlias(context);
				if (null == s.isTemplateInstance()) {
					// goto L1;
					gotoL1 = true;
					continue;
				}
				Expression de2 = new DotExp(e.loc, e, new ScopeExp(e.loc, ti));
				de2.type = e.type;
				return de2;
			}
		}

		d = s.isDeclaration();
		if (null == d) {
			if (context.acceptsErrors()) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.SymbolDotSymbolIsNotADeclaration, this, new String[] { e.toChars(context), ident.toChars() }));
			}
			return new IntegerExp(e.loc, 1, Type.tint32);
		}

		if (e.op == TOKtype) {
			VarExp ve;

			if (d.needThis()
					&& (null != hasThis(sc) || null == d.isFuncDeclaration())) {
				if (null != sc.func) {
					ClassDeclaration thiscd;
					thiscd = sc.func.toParent().isClassDeclaration();

					if (null != thiscd) {
						ClassDeclaration cd = e.type.isClassHandle();

						if (cd == thiscd) {
							e = new ThisExp(e.loc);
							e = new DotTypeExp(e.loc, e, cd, context);
							de = new DotVarExp(e.loc, e, d);
							e = de.semantic(sc, context);
							return e;
						} else if ((null == cd || !cd.isBaseOf(thiscd, null,
								context))
								&& null == d.isFuncDeclaration()) {
							if (context.acceptsErrors()) {
								context.acceptProblem(Problem.newSemanticTypeError(
										IProblem.ThisIsRequiredButIsNotABaseClassOf, this, new String[] { e.type.toChars(context), thiscd.toChars(context) }));
							}
						}
					}
				}

				de = new DotVarExp(e.loc, new ThisExp(e.loc), d);
				e = de.semantic(sc, context);
				return e;
			} else if (null != d.isTupleDeclaration()) {
				e = new TupleExp(e.loc, d.isTupleDeclaration(), context);
				;
				e = e.semantic(sc, context);
				return e;
			} else {
				ve = new VarExp(e.loc, d);
			}
			return ve;
		}

		if (d.isDataseg(context)) {
			// (e, d)
			VarExp ve;

			accessCheck(sc, e, d, context);
			ve = new VarExp(e.loc, d);
			e = new CommaExp(e.loc, e, ve);
			e.type = d.type;
			return e;
		}

		if (null != d.parent && null != d.toParent().isModule()) {
			// (e, d)
			VarExp ve;

			ve = new VarExp(e.loc, d);
			e = new CommaExp(e.loc, e, ve);
			e.type = d.type;
			return e;
		}

		de = new DotVarExp(e.loc, e, d);
		de.ident = ident; // Descent: for better error reporting
		de.copySourceRange(e);
		return de.semantic(sc, context);
	}

	@Override
	public int getNodeType() {
		return TYPE_CLASS;
	}

	@Override
	public TypeInfoDeclaration getTypeInfoDeclaration(SemanticContext context) {
		if (sym.isInterfaceDeclaration() != null) {
			return new TypeInfoInterfaceDeclaration(this, context);
		} else {
			return new TypeInfoClassDeclaration(this, context);
		}
	}

	@Override
	public boolean hasPointers(SemanticContext context) {
		return true;
	}

	@Override
	public MATCH implicitConvTo(Type to, SemanticContext context) {
		if (same(this, to, context)) {
			return MATCHexact;
		}

		ClassDeclaration cdto = to.isClassHandle();
		if (cdto != null && cdto.isBaseOf(sym, null, context)) {
			return MATCHconvert;
		}

		if (context.global.params.Dversion == 1) {
			// Allow conversion to (void *)
			if (to.ty == Tpointer && to.next.ty == Tvoid) {
				return MATCHconvert;
			}
		}

		return MATCHnomatch;
	}

	@Override
	public boolean isauto() {
		return sym.isauto;
	}

	@Override
	public boolean isBaseOf(Type type, int[] poffset, SemanticContext context) {
		if (type.ty == Tclass) {
			ClassDeclaration cd = ((TypeClass) type).sym;
			if (sym.isBaseOf(cd, poffset, context)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ClassDeclaration isClassHandle() {
		return sym;
	}

	@Override
	public boolean isZeroInit(SemanticContext context) {
		return true;
	}

	@Override
	public Type semantic(Loc loc, Scope sc, SemanticContext context) {
		if (sym.scope != null) {
			sym.semantic(sym.scope, context);
		}
		return merge(context);
	}

	@Override
	public int size(Loc loc, SemanticContext context) {
		return PTRSIZE;
	}

	@Override
	public Type syntaxCopy(SemanticContext context) {
		return this;
	}

	@Override
	public void toCBuffer2(OutBuffer buf, HdrGenState hgs, int mod, SemanticContext context) {
	    if (mod != this.mod) {
			toCBuffer3(buf, hgs, mod, context);
			return;
		}
		buf.writestring(sym.toChars(context));
	}

	@Override
	public String toChars(SemanticContext context) {
		return sym.toPrettyChars(context);
	}

	@Override
	public void toDecoBuffer(OutBuffer buf, SemanticContext context) {
		String name = sym.mangle(context);
		buf.writestring(ty.mangleChar);
		buf.writestring(name);
	}

	@Override
	public Dsymbol toDsymbol(Scope sc, SemanticContext context) {
		return sym;
	}
	
	@Override
	public IJavaElement getJavaElement() {
		return sym.getJavaElement();
	}
	
	@Override
	public String getSignature0() {
		return sym.getSignature();
	}
	
	@Override
	protected void appendSignature0(StringBuilder sb) {
		sb.append(getSignature());
	}

}
