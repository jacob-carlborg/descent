package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class UnionDeclaration extends StructDeclaration {

	public UnionDeclaration(Loc loc, IdentifierExp id) {
		super(loc, id);
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

	@Override
	public int getNodeType() {
		return UNION_DECLARATION;
	}

	@Override
	public UnionDeclaration isUnionDeclaration() {
		return this;
	}

	@Override
	public String kind() {
		return "union";
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s) {
		UnionDeclaration ud;

		if (s != null) {
			ud = (UnionDeclaration) s;
		} else {
			ud = new UnionDeclaration(loc, ident);
		}
		super.syntaxCopy(ud);
		return ud;
	}

}