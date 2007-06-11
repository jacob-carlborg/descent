package dtool.dom.base;

import dtool.descentadapter.DescentASTConverter;
import dtool.dom.definitions.DefUnit;
import dtool.model.EntityResolver;

public abstract class EntitySingle extends Entity {

	public String name;
	
	public static EntitySingle convert(descent.internal.core.dom.Identifier elem) {
		return (EntitySingle) DescentASTConverter.convertElem(elem);
	}


	public DefUnit getTargetDefUnitAsRoot() {
		return EntityResolver.getDefUnitFromSurroundingScope(this);
	}

}
