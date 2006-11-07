package descent.internal.core.dom;

import java.util.List;

public class ClassDeclaration extends AggregateDeclaration {
	
	public ClassDeclaration(Loc loc, Identifier id, List<BaseClass> baseClasses) {
		super(id, baseClasses);
	}
	
	public int getAggregateDeclarationType() {
		return CLASS_DECLARATION;
	}

}
