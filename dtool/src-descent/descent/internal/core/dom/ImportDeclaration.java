package descent.internal.core.dom;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;


import descent.core.dom.IDeclaration;
import descent.core.dom.IModifiersContainer;
import descent.core.domX.IASTVisitor;
import descent.core.domX.AbstractElement;

public class ImportDeclaration extends Declaration implements IDeclaration, IModifiersContainer {
	
	public ImportDeclaration() {
		super(null);
	}

	public List<Import> imports;
	public boolean isStatic;

	public Import[] getImports() {
		if (imports == null) return new Import[0];
		// TODO: optimize?
		for(Import imp : imports) {
			((AbstractElement) imp).modifiers = this.modifiers;
		}
		return imports.toArray(new Import[imports.size()]);
	}
	
	public int getElementType() {
		return ElementTypes.IMPORT_DECLARATION;
	}
	
	public boolean isStatic() {
		return isStatic;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, imports);
		}
		visitor.endVisit(this);
	}

}
