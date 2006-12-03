package descent.internal.core.dom;

import descent.core.dom.ASTVisitor;

public class TypeStruct extends Type {
	
	public StructDeclaration sym;

	public TypeStruct(StructDeclaration sym) {
		super(TY.Tstruct, null);
		
		this.sym = sym;
	}
	
	public int getNodeType0() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	void accept0(ASTVisitor visitor) {
		// TODO Auto-generated method stub
		
	}

}
