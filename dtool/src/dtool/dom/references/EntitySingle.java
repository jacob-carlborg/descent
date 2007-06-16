package dtool.dom.references;

import util.Assert;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.definitions.DefUnit;
import dtool.refmodel.EntityResolver;
import dtool.refmodel.IEntQualified;

public abstract class EntitySingle extends Entity {

	public String name;
	
	public static EntitySingle convert(descent.internal.core.dom.Identifier elem) {
		return (EntitySingle) DescentASTConverter.convertElem(elem);
	}

	
	public DefUnit getTargetDefUnit() {
		
		if(getParent() instanceof IEntQualified) {
			IEntQualified parent = (IEntQualified) getParent();
			if(parent.getSubEnt() == this) {
				//Assert.isTrue(!subent.isIntrinsic());
				return parent.getTargetDefUnit();
			} else {
				Assert.isTrue(parent.getRoot() == this);
			}
		}
		return EntityResolver.getDefUnitFromSurroundingScope(this);
	}

}
