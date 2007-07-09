package dtool.dom.references;

import descent.internal.core.dom.QualifiedName;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Module;
import dtool.refmodel.EntityResolver;
import dtool.refmodel.NodeUtil;

/** A module reference (in import declarations only). */
public class EntModule extends Entity {
	public String packageName;
	public String moduleName;

	public EntModule(QualifiedName elem) {
		convertNode(elem);
		int sep = elem.name.lastIndexOf('.');
		packageName = sep == -1 ? "" : elem.name.substring(0, sep);
		moduleName = elem.name.substring(sep+1);
	}

	@Override
	public DefUnit getTargetDefUnit() {
		Module parentMod = NodeUtil.getParentModule(this);
		return EntityResolver.findModule(parentMod, packageName, moduleName);
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
