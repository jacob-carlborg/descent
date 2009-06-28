package descent.internal.compiler.parser;

import descent.core.IJavaElement;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.DYNCAST.DYNCAST_IDENTIFIER;

import static descent.internal.compiler.parser.MATCH.MATCHnomatch;

import static descent.internal.compiler.parser.TOK.TOKdotexp;
import static descent.internal.compiler.parser.TOK.TOKimport;
import static descent.internal.compiler.parser.TOK.TOKtype;

import static descent.internal.compiler.parser.TY.Tinstance;
import static descent.internal.compiler.parser.TY.*;


public class TypeStruct extends Type {

	public StructDeclaration sym;

	public TypeStruct(StructDeclaration sym) {
		super(TY.Tstruct, null);
		this.sym = sym;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

	@Override
	public int alignsize(SemanticContext context) {
		int sz;

		sym.size(context); // give error for forward references
		sz = sym.alignsize;
		if (sz > sym.structalign)
			sz = sym.structalign;
		return sz;
	}

	@Override
	public boolean checkBoolean(SemanticContext context) {
		return false;
	}

	@Override
	public MATCH deduceType(Scope sc, Type tparam,
			TemplateParameters parameters, Objects dedtypes,
			SemanticContext context) {

		/* If this struct is a template struct, and we're matching
		 * it against a template instance, convert the struct type
		 * to a template instance, too, and try again.
		 */
		TemplateInstance ti = sym.parent.isTemplateInstance();

		if (null != tparam && tparam.ty == Tinstance) {
			if (null != ti && ti.toAlias(context) == sym) {
				TypeInstance t = new TypeInstance(Loc.ZERO, ti);
				return t.deduceType(sc, tparam, parameters, dedtypes, context);
			}

			/* Match things like:
			 *  S!(T).foo
			 */
			TypeInstance tpi = (TypeInstance) tparam;
			if (tpi.idents.size() > 0) {
				IdentifierExp id = (IdentifierExp) tpi.idents.get(tpi.idents
						.size() - 1);
				if (id.dyncast() == DYNCAST_IDENTIFIER && equals(sym.ident, id)) {
					Type tparent = sym.parent.getType(context);
					if (null != tparent) {
						/* Slice off the .foo in S!(T).foo
						 */
						/* TODO semantic
						 tpi.idents.dim--;
						 MATCH m = tparent.deduceType(sc, tpi, parameters, dedtypes);
						 tpi.idents.dim++;
						 return m;
						 */
						return MATCHnomatch;
					}
				}
			}
		}

		// Extra check
		if (null != tparam && tparam.ty == Tstruct) {
			TypeStruct tp = (TypeStruct) tparam;

			if (sym != tp.sym)
				return MATCHnomatch;
		}
		return super.deduceType(sc, tparam, parameters, dedtypes, context);
	}

	@Override
	public Expression defaultInit(Loc loc, SemanticContext context) {
		 Symbol s;
		 Declaration d;

		 s = sym.toInitializer();
		 d = new SymbolDeclaration(sym.loc, s, sym);
		 d.type = this;
		 return new VarExp(sym.loc, d);
	}

	@Override
	public Expression dotExp(Scope sc, Expression e, IdentifierExp ident,
			SemanticContext context) {
		// int offset;

		Expression b;
		VarDeclaration v = null;
		Dsymbol s;
		DotVarExp de;
		Declaration d;

		if (null == sym.members) {
			if (context.acceptsErrors()) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.StructIsForwardReferenced, this, new String[] { sym.toChars(context) }));
			}
			return new IntegerExp(e.loc, 0, Type.tint32);
		}

		if (equals(ident, Id.tupleof)) {
			/* Create a TupleExp
			 */
			e = e.semantic(sc, context);	// do this before turning on noaccesscheck
			
			// Added for Descent
			sym = (StructDeclaration) sym.unlazy(context);
			
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

		if (e.op == TOKdotexp) {
			DotExp de_ = (DotExp) e;

			if (de_.e1.op == TOKimport) {
				ScopeExp se = (ScopeExp) de_.e1;

				s = se.sds.search(e.loc, ident, 0, context);
				e = de_.e1;
				//goto L1;
			} else {
				s = sym.search(e.loc, ident, 0, context);
			}
		} else {
			// Ident may be null if completing (Foo*).|
			if (ident == null) {
				return e;
			}
			
			s = sym.search(e.loc, ident, 0, context);
		}
		
		// Descent: for binding resolution
		ident.setResolvedSymbol(s, context);

		boolean continueInL1 = true;
	// L1:
		while(continueInL1) {
			continueInL1 = false;
			if (null == s) {
				return super.dotExp(sc, e, ident, context);
			}
	
			if (null == s.isFuncDeclaration()) {	// because of overloading
				s.checkDeprecated(sc, context, this); // TODO check this for reference
			}
			s = s.toAlias(context);
	
			v = s.isVarDeclaration();
			if (null != v && v.isConst() && v.type.toBasetype(context).ty != Tsarray) {
				ExpInitializer ei = v.getExpInitializer(context);
	
				if (null != ei) {
					e = ei.exp.copy(); // need to copy it if it's a StringExp
					e = e.semantic(sc, context);
					return e;
				}
			}
	
			if (null != s.getType(context)) {
				//return new DotTypeExp(e.loc, e, s);
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
				if (0 == ti.semanticdone)
					ti.semantic(sc, context);
				
				// Added for Descent
				if ((ti == null || ti.inst == null) && context.global.errors > 0) {
					return new IntegerExp(0);
				}
				
				s = ti.inst.toAlias(context);
				if (null == s.isTemplateInstance()) {
					// goto L1;
					continueInL1 = true;
					continue;
				}
				Expression de2 = new DotExp(e.loc, e, new ScopeExp(e.loc, ti));
				de2.type = e.type;
				return de2;
			}
		}
		
	    Import timp = s.isImport();
		if (timp != null) {
			e = new DsymbolExp(e.loc, s);
			e = e.semantic(sc, context);
			return e;
		}

		d = s.isDeclaration();
		
		if (d == null) {
			throw new IllegalStateException("assert(d);");
		}

		if (e.op == TOKtype) {
			FuncDeclaration fd = sc.func;

			if (d.needThis() && null != fd && null != fd.vthis) {
				e = new DotVarExp(e.loc, new ThisExp(e.loc), d);
				e = e.semantic(sc, context);
				return e;
			}
			if (null != d.isTupleDeclaration()) {
				e = new TupleExp(e.loc, d.isTupleDeclaration(), context);
				e = e.semantic(sc, context);
				return e;
			}
			return new VarExp(e.loc, d);
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

		if (null != v) {
			if (v.toParent() != sym) {
				if (context.acceptsErrors()) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.SymbolIsNotAMember, this, new String[] { v.toChars(context) }));
				}
			}

			// *(&e + offset)
			accessCheck(sc, e, d, context);
//			b = new AddrExp(e.loc, e);
//			b.type = e.type.pointerTo(context);
//			b = new AddExp(e.loc, b, new IntegerExp(e.loc, v.offset(),
//					Type.tint32));
//			b.type = v.type.pointerTo(context);
//			e = new PtrExp(e.loc, b);
//			e.type = v.type;
//			return e;
		}

		de = new DotVarExp(e.loc, e, d);
		return de.semantic(sc, context);
	}

	@Override
	public int getNodeType() {
		return TYPE_STRUCT;
	}

	@Override
	public TypeInfoDeclaration getTypeInfoDeclaration(SemanticContext context) {
		return new TypeInfoStructDeclaration(this, context);
	}

	@Override
	public boolean hasPointers(SemanticContext context) {
		StructDeclaration s = sym;

		sym.size(context); // give error for forward references
		if (null != s.members) {
			for (int i = 0; i < s.members.size(); i++) {
				Dsymbol sm = s.members.get(i);
				if (sm.hasPointers(context))
					return true;
			}
		}
		return false;
	}

	@Override
	public boolean isZeroInit(Loc loc, SemanticContext context) {
		return sym.zeroInit;
	}

	@Override
	public int memalign(int salign, SemanticContext context) {
		sym.size(context); // give error for forward references
		return sym.structalign;
	}

	@Override
	public Type semantic(Loc loc, Scope sc, SemanticContext context) {
		return merge(context);
	}

	@Override
	public int size(Loc loc, SemanticContext context) {
		return sym.size(context);
	}

	@Override
	public void toCBuffer2(OutBuffer buf, HdrGenState hgs, int mod, SemanticContext context) {
	    if (mod != this.mod) {
			toCBuffer3(buf, hgs, mod, context);
			return;
		}
		TemplateInstance ti = sym.parent.isTemplateInstance();
		if (ti != null && ti.toAlias(context) == sym) {
			buf.writestring(ti.toChars(context));
		} else {
			buf.writestring(sym.toChars(context));
		}
	}

	@Override
	public String toChars(SemanticContext context) {
		TemplateInstance ti = sym.parent.isTemplateInstance();
		if (ti != null && ti.toAlias(context) == sym) {
			return ti.toChars(context);
		}
		return sym.toChars(context);
	}

	@Override
	public void toDecoBuffer(OutBuffer buf, SemanticContext context) {
		String name = sym.mangle(context);
		buf.printf(ty.mangleChar + name);
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
	
	//PERHAPS dt_t **toDt(dt_t **pdt);
	//PERHAPS type *toCtype();
}
