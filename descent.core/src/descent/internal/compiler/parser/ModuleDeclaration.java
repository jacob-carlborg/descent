package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class ModuleDeclaration extends ASTDmdNode {

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
		OutBuffer buf = new OutBuffer();
		if (packages != null && packages.size() > 0) {
			for (int i = 0; i < packages.size(); i++) {
				IdentifierExp pid = packages.get(i);
				buf.writestring(pid.toChars());
				buf.writeByte('.');
			}
		}
		buf.writestring(id.toChars());
		return buf.extractData();
	}

}
