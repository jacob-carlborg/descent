package descent.internal.compiler.parser;

import melnorme.miscutil.Assert;
import descent.core.compiler.CharOperation;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.TOK.TOKdotexp;
import static descent.internal.compiler.parser.TOK.TOKimport;
import static descent.internal.compiler.parser.TOK.TOKtype;

public class TypeStruct extends Type {

	public StructDeclaration sym;

	public TypeStruct(StructDeclaration sym) {
		super(TY.Tstruct, null);
		this.sym = sym;
	}

	public void accept0(IASTVisitor visitor) {
		Assert.fail("Accept0 on fake class");
	}

	@Override
	public Expression defaultInit(SemanticContext context) {
		return super.defaultInit(context);
		/* TODO semantic
		 Symbol s;
		 Declaration d;

		 s = sym.toInitializer();
		 d = new SymbolDeclaration(sym.loc, s, sym);
		 assert(d);
		 d.type = this;
		 return new VarExp(sym.loc, d);
		 */
	}

	@Override
	public int getNodeType() {
		return TYPE_STRUCT;
	}

	@Override
	public Type semantic(Loc loc, Scope sc, SemanticContext context) {
		return merge(context);
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
	public Expression dotExp(Scope sc, Expression e, IdentifierExp ident,
			SemanticContext context) {
		int offset;

		Expression b;
		VarDeclaration v;
		Dsymbol s;
		DotVarExp de;
		Declaration d;

		if (null == sym.members) {
			error("struct %s is forward referenced", sym.toChars(context));
			return new IntegerExp(e.loc, 0, Type.tint32);
		}

		if (CharOperation.equals(ident.ident, Id.tupleof)) {
			/* Create a TupleExp
			 */
			Expressions exps = new Expressions(sym.fields.size());
			for (VarDeclaration v_ : sym.fields) {
				Expression fe = new DotVarExp(e.loc, e, v_);
				exps.add(fe);
			}
			e = new TupleExp(e.loc, exps);
			e = e.semantic(sc, context);
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
			s = sym.search(e.loc, ident, 0, context);
		}

		//L1:
		if (null == s) {
			return super.dotExp(sc, e, ident, context);
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

		if (null != s.getType()) {
			//return new DotTypeExp(e.loc, e, s);
			return new TypeExp(e.loc, s.getType());
		}

		EnumMember em = s.isEnumMember();
		if (null != em) {
			assert (null != em.value);
			return em.value.copy();
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

		d = s.isDeclaration();
		assert (null != d);

		if (e.op == TOKtype) {
			FuncDeclaration fd = sc.func;

			if (d.needThis() && null != fd && null != fd.vthis) {
				e = new DotVarExp(e.loc, new ThisExp(e.loc), d);
				e = e.semantic(sc, context);
				return e;
			}
			if (null != d.isTupleDeclaration()) {
				e = null; /* TODO new TupleExp(e.loc, d.isTupleDeclaration()); */
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
			if (v.toParent() != sym)
				sym.error("'%s' is not a member", v.toChars(context));

			// *(&e + offset)
			accessCheck(sc, e, d, context);
			b = new AddrExp(e.loc, e);
			b.type = e.type.pointerTo(context);
			b = new AddExp(e.loc, b, new IntegerExp(e.loc, v.offset,
					Type.tint32));
			b.type = v.type.pointerTo(context);
			e = new PtrExp(e.loc, b);
			e.type = v.type;
			return e;
		}

		de = new DotVarExp(e.loc, e, d);
		return de.semantic(sc, context);
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
	public boolean isZeroInit(SemanticContext context) {
		return sym.zeroInit;
	}

	@Override
	public int memalign(int salign, SemanticContext context) {
		sym.size(context); // give error for forward references
		return sym.structalign;
	}

	@Override
	public int size(Loc loc, SemanticContext context) {
		return sym.size(context);
	}

	@Override
	public Dsymbol toDsymbol(Scope sc, SemanticContext context) {
		return sym;
	}

	public void toTypeInfoBuffer(OutBuffer buf, SemanticContext context) {
		// TODO Auto-generated method stub
	}

	public MATCH deduceType(Scope sc, Type tparam,
			TemplateParameters parameters, Objects dedtypes) {
		// TODO Auto-generated method stub
		return null;
	}

	TypeInfoDeclaration getTypeInfoDeclaration() {
		// TODO Auto-generated method stub
		return null;
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
	public void toCBuffer2(OutBuffer buf, IdentifierExp ident, HdrGenState hgs, SemanticContext context) {
		buf.prependbyte(' ');
	    buf.prependstring(toChars(context));
	    if (ident != null)
		buf.writestring(ident.toChars());
	}

}
