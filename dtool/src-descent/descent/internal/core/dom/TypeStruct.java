package descent.internal.core.dom;

import descent.core.domX.ASTVisitor;

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
	
	@Override
	public void accept0(ASTVisitor visitor) {
		// TODO Auto-generated method stub
		
	}

}