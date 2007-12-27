package descent.internal.compiler.lookup;

import descent.core.IField;
import descent.internal.compiler.parser.IInitializer;
import descent.internal.compiler.parser.ITypedefDeclaration;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeTypedef;

public class RTypedefDeclaration extends RDeclaration implements ITypedefDeclaration {
	
	private Type type;
	private Type basetype;

	public RTypedefDeclaration(IField element, SemanticContext context) {
		super(element, context);
	}
	
	public Type basetype() {
		if (basetype == null) {
			basetype = getTypeFromField();
		}
		return basetype;
	}

	public void basetype(Type basetype) {
		throw new IllegalStateException("Should not be called");
	}

	public IInitializer init() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean inuse() {
		// TODO Auto-generated method stub
		return false;
	}

	public void inuse(boolean inuse) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Type getType() {
		return type();
	}
	
	@Override
	public Type type() {
		if (type == null) {
			type = new TypeTypedef(this);
			type.deco = getTypeDeco();
		}
		return type;
	}
	
	public int sem() {
		return 3; // semantic already run
	}
	
	public ITypedefDeclaration isTypedefDeclaration() {
		return this;
	}

}
