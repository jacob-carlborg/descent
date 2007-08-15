package dtool.dom.declarations;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.StorageClassDeclaration;
import dtool.dom.ast.IASTNeoVisitor;

public class DeclarationStorageClass extends DeclarationAttrib {

	public int stclass;
	
	public DeclarationStorageClass(StorageClassDeclaration elem) {
		super(elem, elem.decl);
		this.stclass = elem.stc;
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			//TreeVisitor.acceptChildren(visitor, prot);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}

}
