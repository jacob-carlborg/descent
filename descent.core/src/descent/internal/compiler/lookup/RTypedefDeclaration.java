package descent.internal.compiler.lookup;

import descent.core.IField;
import descent.internal.compiler.parser.ITypedefDeclaration;
import descent.internal.compiler.parser.SemanticContext;

public class RTypedefDeclaration extends RDeclaration implements ITypedefDeclaration {

	public RTypedefDeclaration(IField element, SemanticContext context) {
		super(element, context);
	}
	
	public ITypedefDeclaration isTypedefDeclaration() {
		return this;
	}

}
