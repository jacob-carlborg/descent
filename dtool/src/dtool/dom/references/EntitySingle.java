package dtool.dom.references;

import java.util.Collection;

import melnorme.miscutil.Assert;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.definitions.DefUnit;
import dtool.refmodel.EntitySearch;
import dtool.refmodel.EntityResolver;
import dtool.refmodel.IEntQualified;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.NodeUtil;

public abstract class EntitySingle extends Entity {

	public String name;
	
	public static EntitySingle convert(descent.internal.core.dom.Identifier elem) {
		return (EntitySingle) DescentASTConverter.convertElem(elem);
	}

	
	
	public Collection<DefUnit> findTargetDefUnits(boolean findOneOnly) {

		if(getParent() instanceof IEntQualified) {
			IEntQualified parent = (IEntQualified) getParent();
			if(parent.getSubEnt() == this) {
				//Assert.isTrue(!subent.isIntrinsic());
				return parent.findTargetDefUnits(findOneOnly);
			} else {
				Assert.isTrue(parent.getRoot() == this);
			}
		}
		EntitySearch search = EntitySearch.newSearch(name, this);
		IScopeNode scope = NodeUtil.getOuterScope(this);
		EntityResolver.findDefUnitInExtendedScope(scope, search);
		return search.getDefUnits();
	}

}
