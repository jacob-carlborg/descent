package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class IftypeExp extends Expression {

	public Type targ;
	public IdentifierExp ident;
	public TOK tok;
	public Type tspec;
	public TOK tok2;

	public IftypeExp(Loc loc, Type targ, IdentifierExp ident, TOK tok, Type tspec, TOK tok2) {
		super(loc, TOK.TOKis);
		this.targ = targ;
		this.ident = ident;
		this.tok = tok;
		this.tspec = tspec;
		this.tok2 = tok2;		
	}
	
	@Override
	public int getNodeType() {
		return IFTYPE_EXP;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, targ);
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, tspec);
		}
		visitor.endVisit(this);
	}

}
