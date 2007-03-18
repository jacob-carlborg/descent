package descent.internal.compiler.parser;

public class TypeClass extends Type {
	
	public ClassDeclaration sym;
	
	public TypeClass(ClassDeclaration sym) {
		super(TY.Tclass, null);
		this.sym = sym;
	}
	
	@Override
	public boolean isauto() {
		return sym.isauto;
	}
	
	@Override
	public int kind() {
		return TYPE_CLASS;
	}

}
