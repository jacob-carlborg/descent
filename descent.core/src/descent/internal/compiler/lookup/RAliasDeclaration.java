package descent.internal.compiler.lookup;

import descent.core.IField;
import descent.internal.compiler.parser.IAliasDeclaration;
import descent.internal.compiler.parser.SemanticContext;

public class RAliasDeclaration extends RDeclaration implements IAliasDeclaration {

	public RAliasDeclaration(IField element, SemanticContext context) {
		super(element, context);
	}
	
	@Override
	public IAliasDeclaration isAliasDeclaration() {
		return this;
	}

}
