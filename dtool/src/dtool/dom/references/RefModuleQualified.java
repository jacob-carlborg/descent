package dtool.dom.references;

import java.util.Collection;
import java.util.Collections;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.core.dom.Identifier;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Module;
import dtool.refmodel.IDefUnitReference;
import dtool.refmodel.NodeUtil;

/** An entity reference starting at module scope. 
 * Example: "a = .foo;"
 */
public class RefModuleQualified extends CommonRefQualified {

	public RefModuleQualified(Identifier elem) {
		convertNode(elem);
		subref = CommonRefSingle.convert(elem);
	}

	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, subref);
		}
		visitor.endVisit(this);
	}

	public String toString() {
		return "." + subref;
	}

	public IDefUnitReference getRoot() {
		return null;
	}
		
	public Collection<DefUnit> findRootDefUnits() {
		final Module module = NodeUtil.getParentModule(this);
		return Collections.singletonList((DefUnit)module);
	}

}
