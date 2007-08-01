package dtool.refmodel;

import dtool.dom.ast.ASTNeoNode;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Module;

public class PartialEntitySearh extends EntitySearch {

	private IScope refScope;

	public PartialEntitySearh(String name, IScope refScope,
			boolean findOneOnly) {
		super(name, null, findOneOnly);
		this.refScope = refScope;
	}
	
	public Module getReferenceModule() {
		if(searchRefModule == null)
			searchRefModule = ((ASTNeoNode) refScope).getModule();
		return searchRefModule;
	}

	
	@Override
	public boolean matches(DefUnit defUnit) {
		return defUnit.getName().startsWith(searchName);
	}
	
}
