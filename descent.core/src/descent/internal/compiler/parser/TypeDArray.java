package descent.internal.compiler.parser;

public class TypeDArray extends Type {
	
	public TypeDArray(Type next) {
		super(TY.Tarray, next);
	}
	
	@Override
	public int kind() {
		return TYPE_D_ARRAY;
	}

}
