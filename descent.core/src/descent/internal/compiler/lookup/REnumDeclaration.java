package descent.internal.compiler.lookup;

import descent.core.IType;
import descent.internal.compiler.parser.IEnumDeclaration;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeEnum;
import descent.internal.compiler.parser.integer_t;

public class REnumDeclaration extends RScopeDsymbol implements IEnumDeclaration {
	
	private TypeEnum type;

	public REnumDeclaration(IType element) {
		super(element);
	}
	
	@Override
	public IEnumDeclaration isEnumDeclaration() {
		return this;
	}
	
	@Override
	public Type getType() {
		if (type == null) {
			type = new TypeEnum(this);
		}
		return type;
	}

	public integer_t defaultval() {
		// TODO Auto-generated method stub
		return integer_t.ZERO;
	}

	public integer_t maxval() {
		// TODO Auto-generated method stub
		return integer_t.ZERO;
	}

	public Type memtype() {
		// TODO Auto-generated method stub
		return Type.tint32;
	}

	public integer_t minval() {
		// TODO Auto-generated method stub
		return integer_t.ZERO;
	}

}
