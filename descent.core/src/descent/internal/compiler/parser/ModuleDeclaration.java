package descent.internal.compiler.parser;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class ModuleDeclaration extends ASTDmdNode {
	
	public IdentifierExp id;
	public List<IdentifierExp> packages;
	
	public ModuleDeclaration(List<IdentifierExp> packages, IdentifierExp id) {
		this.packages = packages;
		this.id = id;
	}
	
	@Override
	public int getNodeType() {
		return MODULE_DECLARATION;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, packages);
			TreeVisitor.acceptChildren(visitor, id);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("module ");
		if(packages != null)
			for(IdentifierExp pack : packages) {
				sb.append(pack);
				sb.append('.');
			}
		sb.append(id);
		sb.append(';');
		return sb.toString();
	}
	
}
