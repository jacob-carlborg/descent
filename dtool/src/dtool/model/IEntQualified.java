package dtool.model;

import dtool.dom.base.EntitySingle;
import dtool.dom.definitions.DefUnit;

public interface IEntQualified {

	IScopeBinding getRoot();
	EntitySingle getSubEnt();
	
	DefUnit getTargetDefUnit();
	
}
