package descent.internal.compiler.parser;


public class TypeBasic extends Type {
	
	public TypeBasic(TY ty) {
		super(ty, null);
	}
	
	@Override
	public int kind() {
		return TYPE_BASIC;
	}

}
