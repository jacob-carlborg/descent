package descent.internal.core.dom;

import descent.core.dom.ElementVisitor;

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
	void accept0(ElementVisitor visitor) {
		// TODO Auto-generated method stub
		
	}

}
