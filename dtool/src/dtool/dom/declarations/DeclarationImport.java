package dtool.dom.declarations;

import java.util.List;

import util.StringUtil;
import util.tree.TreeVisitor;
import descent.internal.core.dom.Import;
import descent.internal.core.dom.ImportDeclaration;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;

/**
 * TODO An Import Declaration 
 */
public class DeclarationImport extends ASTNeoNode {

	public List<Import> imports;
	public boolean isStatic;
	
	public DeclarationImport(ImportDeclaration elem) {
		setSourceRange(elem);
		this.imports = elem.imports;
		this.imports.get(0).getSelectiveImports();
	}

	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, imports);
		}
		visitor.endVisit(this);
	}
	
	public String toString() {
		return StringUtil.collToString(imports, ",");
	}
}
