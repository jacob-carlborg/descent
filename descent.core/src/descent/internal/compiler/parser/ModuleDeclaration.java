package descent.internal.compiler.parser;

import java.util.List;

public class ModuleDeclaration extends ASTNode {
	
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
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("module ");
		for(IdentifierExp pack : packages) {
			sb.append(pack);
			sb.append('.');
		}
		sb.append(id);
		sb.append(';');
		return sb.toString();
	}
	
}
