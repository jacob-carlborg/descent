package descent.internal.compiler.parser;

public class TypeStruct extends Type {
	
	public StructDeclaration sym;

	public TypeStruct(StructDeclaration sym) {
		super(TY.Tstruct, null);
		this.sym = sym;
	}

	@Override
	public int kind() {
		return TYPE_STRUCT;
	}

}
