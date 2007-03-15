package descent.internal.compiler.parser;

public class TypeDelegate extends Type {
	
	public TypeDelegate(Type next) {
		super(TY.Tdelegate, next);
	}
	
	@Override
	public int kind() {
		return TYPE_DELEGATE;
	}

}
