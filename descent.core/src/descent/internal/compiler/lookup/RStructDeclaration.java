package descent.internal.compiler.lookup;

import descent.core.IType;
import descent.internal.compiler.parser.IStructDeclaration;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeStruct;

public class RStructDeclaration extends RAggregateDeclaration implements IStructDeclaration {
	
	private TypeStruct type;

	public RStructDeclaration(IType element) {
		super(element);
	}
	
	@Override
	public Type getType() {
		return type();
	}
	
	@Override
	public Type type() {
		if (type == null) {
			type = new TypeStruct(this);
			if (type != null) {
				type.deco = "S" + getTypeDeco();
			}
		}
		return type;
	}
	
	@Override
	public IStructDeclaration isStructDeclaration() {
		return this;
	}
	
	public boolean zeroInit() {
		// TODO Auto-generated method stub
		return false;
	}

}
