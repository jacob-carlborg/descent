package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IDeclaration;
import descent.core.dom.IStorageClassDeclaration;

public class StorageClassDeclaration extends Declaration implements IStorageClassDeclaration {
	
	public int stc;
	public IDeclaration[] decl;

	public StorageClassDeclaration(int stc, List<Declaration> declDefs) {
		this.stc = stc;
		if (declDefs != null) {
			this.decl = declDefs.toArray(new IDeclaration[declDefs.size()]);
		}
	}
	
	public IDeclaration[] getDeclarationDefinitions() {
		if (decl == null) return ASTNode.NO_DECLARATIONS;
		return decl;
	}
	
	public int getNodeType0() {
		return STORAGE_CLASS_DECLARATION;
	}
	
	public void accept0(ASTVisitor visitor) {
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
