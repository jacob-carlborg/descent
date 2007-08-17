package dtool.dom.references;

import java.util.Collection;
import java.util.Collections;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.IdentifierExp;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Module;
import dtool.refmodel.IDefUnitReferenceNode;
import dtool.refmodel.NodeUtil;

/** An entity reference starting at module scope. 
 * Example: "a = .foo;"
 */
public class RefModuleQualified extends CommonRefQualified {

	public RefModuleQualified(IdentifierExp elem) {
		convertNode(elem);
		subref = CommonRefSingle.convertToSingleRef(elem);
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

	public IDefUnitReferenceNode getRoot() {
		return null;
	}
		
	public Collection<DefUnit> findRootDefUnits() {
		final Module module = NodeUtil.getParentModule(this);
		return Collections.singletonList((DefUnit)module);
	}

}
