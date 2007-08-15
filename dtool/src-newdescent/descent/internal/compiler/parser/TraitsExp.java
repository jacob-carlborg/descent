package descent.internal.compiler.parser;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.IASTVisitor;

public class TraitsExp extends Expression {

	public IdentifierExp ident;
	public List<ASTDmdNode> args;

	public TraitsExp(Loc loc, IdentifierExp ident, List<ASTDmdNode> args) {
		super(loc, TOK.TOKtraits);
		this.ident = ident;
		this.args = args;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, args);
		}
		visitor.endVisit(this);
	}


	@Override
	public int getNodeType() {
		return TRAITS_EXP;
	}

}
