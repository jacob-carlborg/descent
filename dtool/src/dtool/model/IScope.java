package dtool.model;

import java.util.List;

import util.tree.IElement;

import dtool.dom.definitions.DefUnit;

public interface IScope extends IElement {

	List<DefUnit> getDefUnits();

	IScope getSuperScope();

}
