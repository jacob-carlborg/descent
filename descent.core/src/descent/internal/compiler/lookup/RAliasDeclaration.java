package descent.internal.compiler.lookup;

import descent.core.IField;
import descent.internal.compiler.parser.IAliasDeclaration;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Type;

public class RAliasDeclaration extends RDeclaration implements IAliasDeclaration {
	
	private Type type;

	public RAliasDeclaration(IField element, SemanticContext context) {
		super(element, context);
	}
	
	@Override
	public Type getType() {
		return type();
	}
	
	@Override
	public Type type() {
		if (type == null) {
			type = getTypeFromField();
		}
		return type;
	}
	
	@Override
	public IAliasDeclaration isAliasDeclaration() {
		return this;
	}

}
