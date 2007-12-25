package descent.internal.compiler.lookup;

import descent.core.IType;
import descent.internal.compiler.parser.BaseClass;
import descent.internal.compiler.parser.IClassDeclaration;
import descent.internal.compiler.parser.IInterfaceDeclaration;
import descent.internal.compiler.parser.SemanticMixin;
import descent.internal.compiler.parser.SemanticContext;

public class RInterfaceDeclaration extends RClassDeclaration implements IInterfaceDeclaration {

	public RInterfaceDeclaration(IType element, SemanticContext context) {
		super(element, context);
	}
	
	@Override
	public boolean isBaseOf(IClassDeclaration cd, int[] poffset, SemanticContext context) {
		return SemanticMixin.isBaseOf(this, cd, poffset, context);
	}
	
	public boolean isBaseOf(BaseClass bc, int[] poffset) {
		return SemanticMixin.isBaseOf(this, bc, poffset);
	}
	
	@Override
	public IInterfaceDeclaration isInterfaceDeclaration() {
		return this;
	}

}
