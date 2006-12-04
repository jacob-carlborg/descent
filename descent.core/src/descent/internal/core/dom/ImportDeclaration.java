package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IImport;
import descent.core.dom.IImportDeclaration;

public class ImportDeclaration extends Declaration implements IImportDeclaration {
	
	public List<IImport> imports;
	public boolean isStatic;

	public IImport[] getImports() {
		if (imports == null) return new IImport[0];
		// TODO: optimize?
		for(IImport imp : imports) {
			((ASTNode) imp).modifierFlags = this.modifierFlags;
		}
		return imports.toArray(new IImport[imports.size()]);
	}
	
	public int getNodeType0() {
		return IMPORT_DECLARATION;
	}
	
	public boolean isStatic() {
		return isStatic;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChildren(visitor, imports);
		}
		visitor.endVisit(this);
	}

}
