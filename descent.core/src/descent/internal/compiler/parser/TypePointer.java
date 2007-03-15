package descent.internal.compiler.parser;

public class TypePointer extends Type {
	
	public TypePointer(Type next) {
		super(TY.Tpointer, next);
	}
	
	@Override
	public int kind() {
		return TYPE_POINTER;
	}

}
