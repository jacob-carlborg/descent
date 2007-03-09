package dtool.dom.base;

import java.util.List;

/**
 * Abstract classe for all *free standing* definitions. TODO
 */
public abstract class Definition extends DefUnit {
	
	public String comments;
	public Def_EProtection protection;
	public int modifiers;

	public List<DefUnit> getDefUnits() {
		// TODO Auto-generated method stub
		return null;
	}
}
