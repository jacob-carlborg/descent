package descent.internal.compiler.parser;

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
	
	public TY ty;
	public Type next;
	public String deco;
	
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
	
	public Expression toExpression() {
		return null;
	}

}
