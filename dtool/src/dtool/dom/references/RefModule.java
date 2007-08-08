package dtool.dom.references;

import java.util.Collection;

import descent.internal.core.dom.QualifiedName;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Module;
import dtool.refmodel.DefUnitSearch;
import dtool.refmodel.EntityResolver;
import dtool.refmodel.NodeUtil;

/** 
 * A module reference (in import declarations only).
 * XXX: transform this into a qualifed ref 
 */
public class RefModule extends Reference {
	public String packageName;
	public String moduleName;

	public RefModule(QualifiedName elem) {
		convertNode(elem);
		int sep = elem.name.lastIndexOf('.');
		packageName = sep == -1 ? "" : elem.name.substring(0, sep);
		moduleName = elem.name.substring(sep+1);
	}

	public Collection<DefUnit> findTargetDefUnits(boolean findOneOnly) {
		Module originMod = NodeUtil.getParentModule(this);
		Module targetMod = EntityResolver.findModule(originMod, packageName, moduleName);
		return DefUnitSearch.wrapResult(targetMod);
	}
	
	

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			//TreeVisitor.acceptChildren(visitor, root);
			//TreeVisitor.acceptChildren(visitor, subent);
		}
		visitor.endVisit(this);	
	}

}
