package descent.internal.compiler.lookup;

import descent.core.IField;
import descent.internal.compiler.parser.IAliasDeclaration;

public class RAliasDeclaration extends RDeclaration implements IAliasDeclaration {

	public RAliasDeclaration(IField element) {
		super(element);
	}
	
	@Override
	public IAliasDeclaration isAliasDeclaration() {
		return this;
	}

}
