package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class ModuleDeclaration extends ASTDmdNode implements IModuleDeclaration {

	public IdentifierExp id;
	public Identifiers packages;

	public ModuleDeclaration(Identifiers packages, IdentifierExp id) {
		this.packages = packages;
		this.id = id;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, packages);
			TreeVisitor.acceptChildren(visitor, id);
		}
		visitor.endVisit(this);
	}

	@Override
	public int getNodeType() {
		return MODULE_DECLARATION;
	}

	@Override
	public String toChars(SemanticContext context) {
		return SemanticMixin.toChars(this, context);
	}
	
	public char[] getFQN() {
		return getFQN(packages, id);
	}
	
	public IdentifierExp id() {
		return id;
	}
	
	public Identifiers packages() {
		return packages;
	}

}
