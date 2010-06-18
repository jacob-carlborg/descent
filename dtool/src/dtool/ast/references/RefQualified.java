package dtool.ast.references;

import java.util.Collection;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.ast.IASTNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.refmodel.IDefUnitReferenceNode;

/**
 * A normal qualified reference.
 */
public class RefQualified extends CommonRefQualified {
	

	public IDefUnitReferenceNode root; //Entity or Expression

	protected RefQualified() {
	}
	
	public RefQualified(List<IdentifierExp> packages, IdentifierExp id) {
		if(packages == null || packages.size() == 0) {
		}
		
		if(packages.size() == 1) {
			IdentifierExp rootId = packages.get(0);
			setSourceRange(rootId.start, id.getEndPos());
			this.root = CommonRefSingle.convertToSingleRef(rootId);
			this.subref = CommonRefSingle.convertToSingleRef(id);
		} else {
			setSourceRange(packages.get(0).start, id.getEndPos());
			packages.remove(packages.size()-1);
			this.root = new RefQualified(packages, id); 
			this.subref = CommonRefSingle.convertToSingleRef(id);
		}
	}
	
	public RefQualified(Reference rootRef, CommonRefSingle subRef) {
		setSourceRange(rootRef.getStartPos(), subRef.getEndPos());
		this.root = rootRef;
		this.subref = subRef;
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, root);
			TreeVisitor.acceptChildren(visitor, subref);
		}
		visitor.endVisit(this);
	}

	@Override
	public String toStringAsElement() {
		return root.toStringAsElement() + "." + subref.toStringAsElement();
	}
	
	public IASTNode getRootAsNode() {
		return (IASTNode) this.root;
	}

	@Override
	public IDefUnitReferenceNode getRoot() {
		return root;
	}

	@Override
	public Collection<DefUnit> findRootDefUnits() {
		return root.findTargetDefUnits(false);
	}

}