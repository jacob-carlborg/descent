package descent.internal.compiler.parser;

public class BaseClass extends ASTNode {
	
	public Modifier modifier;
	public Type type;
	public PROT protection;
	
	public BaseClass(Type type, Modifier modifier, PROT protection) {
		this.type = type;
		this.modifier = modifier;
		this.protection = protection;
	}
	
	@Override
	public int kind() {
		return BASE_CLASS;
	}

}
