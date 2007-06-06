package dtool.dom.base;

import dtool.descentadapter.DescentASTConverter;

public abstract class EntitySingle extends Entity {

	public String name;
	
	public static EntitySingle convert(descent.internal.core.dom.Identifier elem) {
		return (EntitySingle) DescentASTConverter.convertElem(elem);
	}
}
