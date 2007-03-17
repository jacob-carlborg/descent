package descent.internal.compiler.parser;

import java.math.BigInteger;

import org.eclipse.core.runtime.Assert;

public abstract class Type extends ASTNode {
	
	public final static Type tvoid = new TypeBasic(TY.Tvoid);
	public final static Type tint8 = new TypeBasic(TY.Tint8);
	public final static Type tuns8 = new TypeBasic(TY.Tuns8);
	public final static Type tint16 = new TypeBasic(TY.Tint16);
	public final static Type tuns16 = new TypeBasic(TY.Tuns16);
	public final static Type tint32 = new TypeBasic(TY.Tint32);
	public final static Type tuns32 = new TypeBasic(TY.Tuns32);
	public final static Type tint64 = new TypeBasic(TY.Tint64);
	public final static Type tuns64 = new TypeBasic(TY.Tuns64);
	public final static Type tfloat32 = new TypeBasic(TY.Tfloat32);
	public final static Type tfloat64 = new TypeBasic(TY.Tfloat64);
	public final static Type tfloat80 = new TypeBasic(TY.Tfloat80);
	public final static Type timaginary32 = new TypeBasic(TY.Timaginary32);
	public final static Type timaginary64 = new TypeBasic(TY.Timaginary64);
	public final static Type timaginary80 = new TypeBasic(TY.Timaginary80);
	public final static Type tcomplex32 = new TypeBasic(TY.Tcomplex32);
	public final static Type tcomplex64 = new TypeBasic(TY.Tcomplex64);
	public final static Type tcomplex80 = new TypeBasic(TY.Tcomplex80);
	public final static Type tbit = new TypeBasic(TY.Tbit);
	public final static Type tbool = new TypeBasic(TY.Tbool);
	public final static Type tchar = new TypeBasic(TY.Tchar);
	public final static Type twchar = new TypeBasic(TY.Twchar);
	public final static Type tdchar = new TypeBasic(TY.Tdchar);
	public final static Type terror = new TypeBasic(TY.Terror); // for error recovery
	
	public TY ty;
	public Type next;
	public String deco;
	public Type pto;		// merged pointer to this type
	public Type rto;		// reference to this type
	public Type arrayof;	// array of this type
	
	public Type(TY ty, Type next) {
		this.ty = ty;
		this.next = next;
	}
	
	public Type semantic(Scope sc, SemanticContext context) {
		if (next != null) {
			next = next.semantic(sc, context);
		}
		return merge(context);
	}
	
	public Type merge(SemanticContext context) {
		Type t;

	    //printf("merge(%s)\n", toChars());
	    t = this;
	    if (deco == null) {
			OutBuffer buf = new OutBuffer();
			StringValue sv;
	
			if (next != null) {
			    next = next.merge(context);
			}
			toDecoBuffer(buf);
			sv = context.typeStringTable.update(buf.toString());
			if (sv.ptrvalue != null)
			{   
				t = (Type ) sv.ptrvalue;
			    assert t.deco != null;
			}
			else
			{
			    sv.ptrvalue = this;
			    deco = sv.lstring;
			}
	    }
	    return t;
	}
	
	public void toDecoBuffer(OutBuffer buf) {
		buf.writeByte(ty.mangleChar);
	    if (next != null)
	    {
	    	assert next != this;
	    	next.toDecoBuffer(buf);
	    }
	}
	
	public void resolve(Scope sc, Expression[] pe, Type[] pt, Dsymbol[] ps, SemanticContext context) {
		Type t;

	    t = semantic(sc, context);
	    pt[0] = t;
	    pe[0] = null;
	    ps[0] = null;
	}
	
	public Expression toExpression() {
		return null;
	}
	
	public Type toBasetype(SemanticContext context) {
		return this;
	}

	public Dsymbol toDsymbol(Scope sc, SemanticContext context) {
		return null;
	}

	public boolean isintegral() {
		return false;
	}

	public boolean isunsigned() {
		return false;
	}

	public Type pointerTo(SemanticContext context) {
		if (pto == null) {
			Type t;

			t = new TypePointer(this);
			pto = t.merge(context);
		}
		return pto;
	}
	
	public Type referenceTo() {
		return null;
	}
	
	public Type arrayOf() {
		return null;
	}
	
	public Expression defaultInit() {
		return null;
	}
	
	public Expression getProperty(Identifier ident) {
		Expression e = null;

	    if (ident == Id.__sizeof)
	    {
	    	/* TODO semantic
	    	e = new IntegerExp(loc, size(loc), Type.tsize_t);
	    	*/
	    }
	    else if (ident == Id.size)
	    {
	    	/* TODO semantic
	    	error(loc, ".size property should be replaced with .sizeof");
	    	e = new IntegerExp(loc, size(loc), Type.tsize_t);
	    	*/
	    }
	    else if (ident == Id.alignof)
	    {
	    	/* TODO semantic
	    	e = new IntegerExp(loc, alignsize(), Type.tsize_t);
	    	*/
	    }
	    else if (ident == Id.typeinfo)
	    {
	    	/* TODO semantic
			if (!global.params.useDeprecated)
			    error(loc, ".typeinfo deprecated, use typeid(type)");
			e = getTypeInfo(NULL);
			*/
	    }
	    else if (ident == Id.init)
	    {
	    	e = defaultInit();
	    }
	    else if (ident == Id.mangleof)
	    {
	    	Assert.isNotNull(deco);
	    	e = new StringExp(deco, 'c');
	    	/* TODO semantic
			Scope sc;
			e = e.semantic(&sc);
			*/
	    }
	    else if (ident == Id.stringof)
	    {	
	    	/* TODO semantic
	    	char *s = toChars();
			e = new StringExp(loc, s, strlen(s), 'c');
			Scope sc;
			e = e.semantic(&sc);
			*/
	    }
	    else
	    {
	    	/* TODO semantic
			error(loc, "no property '%s' for type '%s'", ident.toChars(), toChars());
			*/
			e = new IntegerExp("1", BigInteger.ONE, Type.tint32);
	    }
		return e;
	}
	
	public Type reliesOnTident() {
		if (next == null) {
			return null;
		} else {
			return next.reliesOnTident();
		}
	}
	
	public Expression dotExp(Scope sc, Expression e, IdentifierExp ident,
			SemanticContext context) {
		VarDeclaration v = null;

		if (e.op == TOK.TOKdotvar) {
			DotVarExp dv = (DotVarExp) e;
			v = dv.var.isVarDeclaration();
		} else if (e.op == TOK.TOKvar) {
			VarExp ve = (VarExp) e;
			v = ve.var.isVarDeclaration();
		}
		if (v != null) {
			if (ident.ident == Id.offset) {
				/* TODO semantic
				 if (!global.params.useDeprecated)
				 error(e.loc, ".offset deprecated, use .offsetof");
				 goto Loffset;
				 */
			} else if (ident.ident == Id.offsetof) {
				/* TODO semantic
				 Loffset:
				 if (v.storage_class & STC.STCfield)
				 {
				 e = new IntegerExp(e.loc, v.offset, Type.tint32);
				 return e;
				 }
				 */
			} else if (ident.ident == Id.init) {
				if (v.init != null) {
					if (v.init.isVoidInitializer() != null) {
						/* TODO semantic
						 error(e.loc, "%s.init is void", v.toChars());
						 */
					} else {
						e = v.init.toExpression();
						if (e.op == TOK.TOKassign || e.op == TOK.TOKconstruct) {
							e = ((AssignExp) e).e2;

							/*
							 * Take care of case where we used a 0 to initialize the
							 * struct.
							 */
							if (e.type == Type.tint32
									&& e.isBool(false)
									&& v.type.toBasetype(context).ty == TY.Tstruct) {
								e = v.type.defaultInit();
							}
						}
					}
					return e;
				}
			}
		}
		if (ident.ident == Id.typeinfo) {
			/* TODO semantic
			 if (!global.params.useDeprecated) {
			 error(e.loc, ".typeinfo deprecated, use typeid(type)");
			 }
			 e = getTypeInfo(sc);
			 return e;
			 */
		}
		if (ident.ident == Id.stringof) {
			/* TODO semantic
			 char s = e.toChars();
			 e = new StringExp(e.loc, s, strlen(s), 'c');
			 Scope sc;
			 e = e.semantic(&sc);
			 return e;
			 */
		}
		return getProperty(ident.ident);
	}

}
