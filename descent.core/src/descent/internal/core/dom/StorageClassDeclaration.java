package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IDElement;
import descent.core.dom.IDElementVisitor;
import descent.core.dom.IStorageClassDeclaration;

public class StorageClassDeclaration extends Dsymbol implements IStorageClassDeclaration {
	
	public int stc;
	public IDElement[] decl;

	public StorageClassDeclaration(int stc, List<IDElement> declDefs) {
		this.stc = stc;
		if (declDefs != null) {
			this.decl = declDefs.toArray(new IDElement[declDefs.size()]);
		}
	}
	
	public IDElement[] getDeclarationDefinitions() {
		if (decl == null) return AbstractElement.NO_ELEMENTS;
		return decl;
	}
	
	public int getElementType() {
		return STORAGE_CLASS_DECLARATION;
	}
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChildren(visitor, decl);
		}
		visitor.endVisit(this);
	}
	
	/*
	@Override
	public void semantic(Scope sc, IProblemCollector collector) {
		if (decl != null) {
			int stc_save = sc.stc;

			if ((stc & (STC.STCauto | STC.STCstatic | STC.STCextern)) != 0)
				sc.stc &= ~(STC.STCauto | STC.STCstatic | STC.STCextern);
			sc.stc |= stc;
			for (int i = 0; i < decl.length; i++) {
				Dsymbol s = (Dsymbol) decl[i];

				s.semantic(sc, collector);
			}
			sc.stc = stc_save;
		} else
			sc.stc = stc;
	}
	*/

}
