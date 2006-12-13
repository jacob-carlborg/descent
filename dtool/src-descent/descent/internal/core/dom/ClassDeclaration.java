package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IAggregateDeclaration;
import descent.core.dom.IDeclaration;

public class ClassDeclaration extends AggregateDeclaration {
	
	public ClassDeclaration(Identifier id, List<BaseClass> baseClasses) {
		super(id, baseClasses);
	}
	
	public int getAggregateDeclarationType() {
		return CLASS_DECLARATION;
	}

}
