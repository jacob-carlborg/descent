package descent.internal.core.dom;

import descent.core.dom.IBasicType;
import descent.core.dom.IDElementVisitor;

public class TypeBasic extends Type implements IBasicType {
	
	public TypeBasic(TY ty) {
		super(ty, null);
	}
	
	@Override
	public int getTypeType() {
		return TYPE_BASIC;
	}
	
	public int getBasicTypeKind() {
		switch(ty) {
			case Tvoid:	 return VOID;
			case Tint8:	 return INT8;  
			case Tuns8:	 return UNS8;  
			case Tint16:	 return INT16; 
			case Tuns16:	 return UNS16; 
			case Tint32:	 return INT32; 
			case Tuns32:	 return UNS32; 
			case Tint64:	 return INT64; 
			case Tuns64:	 return UNS64; 
			case Tfloat32: return FLOAT32; 
			case Tfloat64: return FLOAT64; 
			case Tfloat80: return FLOAT80; 
			case Timaginary32: return IMAGINARY32; 
			case Timaginary64: return IMAGINARY64; 
			case Timaginary80: return IMAGINARY80; 
			case Tcomplex32: return COMPLEX32; 
			case Tcomplex64: return COMPLEX64; 
			case Tcomplex80: return COMPLEX80; 
			case Tbit:	 return BIT;     
			case Tbool:	 return BOOL;    
			case Tchar:	 return CHAR;    
			case Twchar:	 return WCHAR; 
			case Tdchar:	 return DCHAR;
		}
		
		throw new RuntimeException("Can't happen");
	}
	
	@Override
	public String toString() {
		switch(ty) {
		case Tvoid:	 return "void";
		case Tint8:	 return "byte";  
		case Tuns8:	 return "ubyte";  
		case Tint16:	 return "short"; 
		case Tuns16:	 return "ushort"; 
		case Tint32:	 return "int"; 
		case Tuns32:	 return "uint"; 
		case Tint64:	 return "long"; 
		case Tuns64:	 return "ulong"; 
		case Tfloat32: return "float"; 
		case Tfloat64: return "double"; 
		case Tfloat80: return "real"; 
		case Timaginary32: return "ifloat"; 
		case Timaginary64: return "idouble"; 
		case Timaginary80: return "ireal"; 
		case Tcomplex32: return "cfloat"; 
		case Tcomplex64: return "cdouble"; 
		case Tcomplex80: return "creal"; 
		case Tbit:	 return "bit";
		case Tbool:	 return "bool";    
		case Tchar:	 return "char";    
		case Twchar:	 return "wchar"; 
		case Tdchar:	 return "dchar";
	}
	
	throw new RuntimeException("Can't happen");
	}
	
	@Override
	public void accept(IDElementVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

}
