package dtool.dom.definitions;

import java.util.List;

import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.Modifier;
import descent.internal.compiler.parser.PROT;
import dtool.descentadapter.DescentASTConverter;

/**
 * Abstract classe for all *free standing* definitions. 
 */
public abstract class Definition extends DefUnit  {
	
	public PROT protection; // fixme, should be node
	public List<Modifier> modifiers;
	
	@Override
	protected void convertDsymbol(Dsymbol elem) {
		super.convertDsymbol(elem);
		this.protection = Def_EProtection.adaptFromDescent(elem.prot()); 
		this.modifiers = DescentASTConverter.convertManyL(elem.modifiers,
				this.modifiers); 
	}
	
	public static Definition convert(Dsymbol elem) {
		return (Definition) DescentASTConverter.convertElem(elem);
	}
}
