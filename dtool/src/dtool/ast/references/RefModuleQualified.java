package dtool.ast.references;

import java.util.Collection;
import java.util.Collections;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.IdentifierExp;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.refmodel.IDefUnitReferenceNode;
import dtool.refmodel.NodeUtil;

/** An entity reference starting at module scope. 
 * Example: "a = .foo;"
 */
public class RefModuleQualified extends CommonRefQualified {

	public RefModuleQualified(IdentifierExp elem, ASTConversionContext convContext) {
		convertNode(elem);
		subref = CommonRefSingle.convertToSingleRef(elem, convContext);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, subref);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public String toStringAsElement() {
		return "." + subref.toStringAsElement();
	}
	
	@Override
	public IDefUnitReferenceNode getRoot() {
		return null;
	}
	
	@Override
	public Collection<DefUnit> findRootDefUnits() {
		final Module module = NodeUtil.getParentModule(this);
		return Collections.singletonList((DefUnit)module);
	}

}
