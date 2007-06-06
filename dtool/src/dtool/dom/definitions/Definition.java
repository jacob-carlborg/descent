package dtool.dom.definitions;

import descent.internal.core.dom.Dsymbol;
import dtool.descentadapter.DescentASTConverter;
import dtool.model.IScope;

/**
 * Abstract classe for all *free standing* definitions. 
 */
public abstract class Definition extends DefUnit implements IScope {
	
	public Def_EProtection protection;
	public int modifiers;
	
	@Override
	protected void convertDsymbol(Dsymbol elem) {
		super.convertDsymbol(elem);
		this.protection = Def_EProtection.adaptFromDescent(elem.modifiers); 
		this.modifiers = Def_Modifiers.adaptFromDescent(elem.modifiers); 
	}
	
	public static Definition convert(Dsymbol elem) {
		return (Definition) DescentASTConverter.convertElem(elem);
	}
}
