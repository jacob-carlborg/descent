package dtool.dom.declarations;

import java.util.List;

import descent.internal.core.dom.Dsymbol;

/**
 * Abstract classe for all *free standing* definitions. 
 */
public abstract class Definition extends DefUnit {
	
	public String comments;
	public Def_EProtection protection;
	public int modifiers;
	
	@Override
	protected void convertDsymbol(Dsymbol elem) {
		super.convertDsymbol(elem);
		this.protection = Def_EProtection.adaptFromDescent(elem.modifiers); 
		this.modifiers = Def_Modifiers.adaptFromDescent(elem.modifiers); 
	}
	
	public List<DefUnit> getDefUnits() {
		// TODO Auto-generated method stub
		return null;
	}
}
