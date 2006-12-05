package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IDeclaration;
import descent.core.dom.IStorageClassDeclaration;
import descent.core.domX.ASTVisitor;
import descent.core.domX.AbstractElement;

public class StorageClassDeclaration extends Dsymbol implements IStorageClassDeclaration {
	
	public int stc;
	public IDeclaration[] decl;

	public StorageClassDeclaration(int stc, List<IDeclaration> declDefs) {
		this.stc = stc;
		if (declDefs != null) {
			this.decl = declDefs.toArray(new IDeclaration[declDefs.size()]);
		}
	}
	
	public IDeclaration[] getDeclarationDefinitions() {
		if (decl == null) return AbstractElement.NO_DECLARATIONS;
		return decl;
	}
	
	public int getElementType() {
		return STORAGE_CLASS_DECLARATION;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChildren(visitor, (AbstractElement[]) decl);
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
