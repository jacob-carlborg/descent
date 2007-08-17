package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.dom.IftypeDeclaration;
import descent.internal.compiler.parser.ast.IASTVisitor;


public class IftypeCondition extends Condition {

	public Type targ;
	public IdentifierExp ident;
	public TOK tok;
	public Type tspec;

	public IftypeCondition(Type targ, IdentifierExp ident, TOK tok, Type tspec) {
		this.targ = targ;
		this.ident = ident;
		this.tok = tok;
		this.tspec = tspec;
	}
	
	public IftypeDeclaration.Kind getKind() {
		if (tok == TOK.TOKreserved) return IftypeDeclaration.Kind.NONE;
		if (tok == TOK.TOKcolon) return IftypeDeclaration.Kind.EXTENDS;
		return IftypeDeclaration.Kind.EQUALS;
	}
	
	@Override
	public int getConditionType() {
		return IFTYPE;
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
