package dtool.refmodel;

import dtool.dom.references.EntitySingle;

public interface IEntQualified extends IDefUnitReference {

	IDefUnitReference getRoot();
	EntitySingle getSubEnt();
	
}
