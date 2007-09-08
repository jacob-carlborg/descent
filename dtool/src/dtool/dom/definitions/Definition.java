package dtool.dom.definitions;

import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.Modifier;
import descent.internal.compiler.parser.PROT;
import descent.internal.compiler.parser.STC;
import dtool.descentadapter.DescentASTConverter;

/**
 * Abstract classe for all *free standing* definitions. 
 */
public abstract class Definition extends DefUnit  {
	
	private static final Modifier[] NOMODIFIERS = new Modifier[0];
	
	public final Modifier[] modifiers;
	public PROT protection; // fixme, should be node
	public int effectiveModifiers;

	public Definition(Dsymbol elem) {
		super(elem);
		this.protection = Def_EProtection.adaptFromDescent(elem.prot());
		if(elem.modifiers != null && elem.modifiers.size() != 0) {
			this.modifiers = elem.modifiers.toArray(
					new Modifier[elem.modifiers.size()]);
			for (int i = 0; i < this.modifiers.length; i++) {
				effectiveModifiers |= STC.fromTOK(modifiers[i].tok);
			}
		} else {
			this.modifiers = NOMODIFIERS;
		}
	}
	
	public static Definition convert(Dsymbol elem) {
		return (Definition) DescentASTConverter.convertElem(elem);
	}
}
