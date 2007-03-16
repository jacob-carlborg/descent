package descent.internal.compiler.parser;


public class TypeBasic extends Type {
	
	public TypeBasic(TY ty) {
		super(ty, null);
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
	public int kind() {
		return TYPE_BASIC;
	}

}
