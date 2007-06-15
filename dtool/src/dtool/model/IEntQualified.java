package dtool.model;

import dtool.dom.base.EntitySingle;

public interface IEntQualified extends IDefUnitReference {

	IDefUnitReference getRoot();
	EntitySingle getSubEnt();
	
}
