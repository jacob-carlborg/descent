package descent.internal.compiler.parser;

import java.math.BigInteger;

public class TypeBasic extends Type {
	
	private final static BigInteger N_0xFF = new BigInteger("FF", 16);
	private final static BigInteger N_0xFFFF = new BigInteger("FFFF", 16); 
	
	public TypeBasic(TY ty) {
		super(ty, null);
		
		// HACK to get deco ready
		OutBuffer out = new OutBuffer();
		toDecoBuffer(out);
		deco = out.extractData();
	}
	
	@Override
	public boolean isintegral() {
		switch(ty) {
		case Tint8:
		case Tuns8:
		case Tint16:
		case Tuns16:
		case Tint32:
		case Tuns32:
		case Tint64:
		case Tuns64:
		case Tbit:
		case Tbool:
		case Tchar:
		case Twchar:
		case Tdchar:
			return true;
		default:
			return false;
		}
	}
	
	@Override
	public boolean isunsigned() {
		switch(ty) {
		case Tuns8:
		case Tuns16:
		case Tuns32:
		case Tuns64:
		case Tbit:
		case Tbool:
		case Tchar:
		case Twchar:
		case Tdchar:
			return true;
		default:
			return false;
		}
	}
	
	@Override
	public Expression defaultInit(SemanticContext context) {
		BigInteger value = BigInteger.ZERO;

		switch (ty) {
		case Tchar:
			value = N_0xFF;
			break;

		case Twchar:
		case Tdchar:
			value = N_0xFFFF;
			break;

		case Timaginary32:
		case Timaginary64:
		case Timaginary80:
		case Tfloat32:
		case Tfloat64:
		case Tfloat80:
		case Tcomplex32:
		case Tcomplex64:
		case Tcomplex80:
			return getProperty(Id.nan, context);
		}
		return new IntegerExp(value.toString(), value, this);
	}
	
	@Override
	public int getNodeType() {
		return TYPE_BASIC;
	}
	
	@Override
	public String toString() {
		switch(ty) {
		case Tbit: return "bit";
		case Tbool: return "bool";
		case Tchar: return "char";
		case Tdchar: return "dchar";
		case Tcomplex32: return "cfloat";
		case Tcomplex64: return "cdouble";
		case Tcomplex80: return "creal";
		case Tfloat32: return "float";
		case Tfloat64: return "double";
		case Tfloat80: return "real";
		case Timaginary32: return "ifloat";
		case Timaginary64: return "idouble";
		case Timaginary80: return "ireal";
		case Tint8: return "byte";
		case Tint16: return "short";
		case Tint32: return "int";
		case Tint64: return "long";
		case Tuns8: return "ubyte";
		case Tuns16: return "ushort";
		case Tuns32: return "uint";
		case Tuns64: return "ulong";
		case Tvoid: return "void";
		case Twchar: return "wchar";
		}
		return "?";
	}

}
