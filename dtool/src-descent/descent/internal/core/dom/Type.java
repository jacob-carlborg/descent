package descent.internal.core.dom;

import descent.core.dom.IType;
import descent.core.domX.AbstractElement;


public abstract class Type extends AbstractElement implements IType {

	public static Type tvoid = new TypeBasic(TY.Tvoid);
	public static Type tint8 = new TypeBasic(TY.Tint8);
	public static Type tuns8 = new TypeBasic(TY.Tuns8);
	public static Type tint16 = new TypeBasic(TY.Tint16);
	public static Type tuns16 = new TypeBasic(TY.Tuns16);
	public static Type tint32 = new TypeBasic(TY.Tint32);
	public static Type tuns32 = new TypeBasic(TY.Tuns32);
	public static Type tint64 = new TypeBasic(TY.Tint64);
	public static Type tuns64 = new TypeBasic(TY.Tuns64);
	public static Type tfloat32 = new TypeBasic(TY.Tfloat32);
	public static Type tfloat64 = new TypeBasic(TY.Tfloat64);
	public static Type tfloat80 = new TypeBasic(TY.Tfloat80);
	public static Type timaginary32 = new TypeBasic(TY.Timaginary32);
	public static Type timaginary64 = new TypeBasic(TY.Timaginary64);
	public static Type timaginary80 = new TypeBasic(TY.Timaginary80);
	public static Type tcomplex32 = new TypeBasic(TY.Tcomplex32);
	public static Type tcomplex64 = new TypeBasic(TY.Tcomplex64);
	public static Type tcomplex80 = new TypeBasic(TY.Tcomplex80);
	public static Type tbit = new TypeBasic(TY.Tbit);
	public static Type tbool = new TypeBasic(TY.Tbool);
	public static Type tchar = new TypeBasic(TY.Tchar);
	public static Type twchar = new TypeBasic(TY.Twchar);
	public static Type tdchar = new TypeBasic(TY.Tdchar);
	
	public static StringTable stringtable = new StringTable();
	public static char[] mangleChar;
	
	public TY ty;
	public Type next;
	public Type pto;		// merged pointer to this type
	public String deco;
	
	public Type(TY ty, Type next) {
		this.ty = ty;
		this.next = next;
	}
	
	public static Type fromTOK(TOK t) {
		switch(t) {
			case TOKvoid:	 return new TypeBasic(TY.Tvoid);
			case TOKint8:	 return new TypeBasic(TY.Tint8);  
			case TOKuns8:	 return new TypeBasic(TY.Tuns8);  
			case TOKint16:	 return new TypeBasic(TY.Tint16); 
			case TOKuns16:	 return new TypeBasic(TY.Tuns16); 
			case TOKint32:	 return new TypeBasic(TY.Tint32); 
			case TOKuns32:	 return new TypeBasic(TY.Tuns32); 
			case TOKint64:	 return new TypeBasic(TY.Tint64); 
			case TOKuns64:	 return new TypeBasic(TY.Tuns64); 
			case TOKfloat32: return new TypeBasic(TY.Tfloat32); 
			case TOKfloat64: return new TypeBasic(TY.Tfloat64); 
			case TOKfloat80: return new TypeBasic(TY.Tfloat80); 
			case TOKimaginary32: return new TypeBasic(TY.Timaginary32); 
			case TOKimaginary64: return new TypeBasic(TY.Timaginary64); 
			case TOKimaginary80: return new TypeBasic(TY.Timaginary80); 
			case TOKcomplex32: return new TypeBasic(TY.Tcomplex32); 
			case TOKcomplex64: return new TypeBasic(TY.Tcomplex64); 
			case TOKcomplex80: return new TypeBasic(TY.Tcomplex80); 
			case TOKbit:	 return new TypeBasic(TY.Tbit);     
			case TOKbool:	 return new TypeBasic(TY.Tbool);    
			case TOKchar:	 return new TypeBasic(TY.Tchar);    
			case TOKwchar:	 return new TypeBasic(TY.Twchar); 
			case TOKdchar:	 return new TypeBasic(TY.Tdchar);
		}
		
		return null;
	}

	public int getTypeType() {
		return 0;
	}
	
	public Type pointerTo()
	{
	    if (pto == null)
	    {	Type t;

		t = new TypePointer(this);
		pto = t.merge();
	    }
	    return pto;
	}
	
	public Type merge()
	{   Type t;

	    //printf("merge(%s)\n", toChars());
	    t = this;
	    if (deco == null)
	    {
		OutBuffer buf = new OutBuffer();
		StringValue sv;

		if (next != null)
		    next = next.merge();
		toDecoBuffer(buf);
		sv = stringtable.update(buf.data.toString());
		if (sv.ptrvalue != null)
		{   t = (Type) sv.ptrvalue;
		    //printf("old value, deco = '%s' %p\n", t.deco, t.deco);
		}
		else
		{
		    sv.ptrvalue = this;
		    deco = sv.lstring;
		    //printf("new value, deco = '%s' %p\n", t.deco, t.deco);
		}
	    }
	    return t;
	}

	public void toDecoBuffer(OutBuffer buf) {
		buf.writeByte('A');
		// TODO: buf.writeByte(mangleChar[ty.ordinal()]);
	    if (next != null)
	    {
		next.toDecoBuffer(buf);
	    }
	}
	
	/********************************
	 * We've mistakenly parsed this as a type.
	 * Redo it as an Expression.
	 * NULL if cannot.
	 */
	public Expression toExpression() {
		return null;
	}
	
}
