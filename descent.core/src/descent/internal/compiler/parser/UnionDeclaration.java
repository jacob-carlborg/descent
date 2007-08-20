package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class UnionDeclaration extends StructDeclaration {

	public UnionDeclaration(Loc loc, IdentifierExp id) {
		super(loc, id);
	}
	
	@Override
	public int getNodeType() {
		return UNION_DECLARATION;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, modifiers);
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, members);
		}
		visitor.endVisit(this);
	}

}
