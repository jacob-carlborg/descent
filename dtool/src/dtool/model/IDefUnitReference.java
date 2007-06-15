package dtool.model;

import dtool.dom.definitions.DefUnit;

public interface IDefUnitReference {
	
	public DefUnit getTargetDefUnit();
	
	public IScope getTargetScope();

}
