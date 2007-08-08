package dtool.dom.references;

import java.util.Collection;

import melnorme.miscutil.tree.TreeVisitor;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.refmodel.IDefUnitReference;

/**
 * 
 */
public class RefQualified extends CommonRefQualified {

	public IDefUnitReference root; //Entity or Expression

	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, root);
			TreeVisitor.acceptChildren(visitor, subref);
		}
		visitor.endVisit(this);
	}

	
	public String toString() {
		return root + "." + subref;
	}
	
	public ASTNode getRootAsNode() {
		return (ASTNode) this.root;
	}

	public IDefUnitReference getRoot() {
		return root;
	}

	public Collection<DefUnit> findRootDefUnits() {
		return root.findTargetDefUnits(false);
	}

}