package descent.internal.core.dom;

import java.util.List;

public class InterfaceDeclaration extends AggregateDeclaration {

	public InterfaceDeclaration(Identifier id, List<BaseClass> baseclasses) {
		super(id, baseclasses);
	}

	public int getAggregateDeclarationType() {
		return INTERFACE_DECLARATION;
	}

}
