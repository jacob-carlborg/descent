package descent.internal.core.dom;

public class TypeStruct extends Type {
	
	public StructDeclaration sym;

	public TypeStruct(StructDeclaration sym) {
		super(TY.Tstruct, null);
		
		this.sym = sym;
	}
	
	public int getElementType() {
		// TODO Auto-generated method stub
		return 0;
	}

}
