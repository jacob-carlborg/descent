package descent.internal.compiler.parser;

public class BaseClass extends ASTNode {
	
	public Modifier modifier;
	public Type type;
	public Type sourceType;
	public PROT protection;
	public ClassDeclaration base;
	
	public BaseClass(Type type, Modifier modifier, PROT protection) {
		this.type = type;
		this.sourceType = type;
		this.modifier = modifier;
		this.protection = protection;
	}
	
	public BaseClass(Type type, PROT protection) {
		this.type = type;
		this.protection = protection;
	}
	
	@Override
	public int getNodeType() {
		return BASE_CLASS;
	}

}
