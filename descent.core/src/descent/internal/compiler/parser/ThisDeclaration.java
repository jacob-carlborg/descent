package descent.internal.compiler.parser;

public class ThisDeclaration extends VarDeclaration {

	public ThisDeclaration(Type type) {
		super(type, Id.This, null);
	}

}
