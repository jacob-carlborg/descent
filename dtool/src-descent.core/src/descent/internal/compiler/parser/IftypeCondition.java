package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.dom.IftypeDeclaration;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class IftypeCondition extends Condition {

	public Type targ;
	public IdentifierExp id;
	public TOK tok;
	public Type tspec;

	public IftypeCondition(Type targ, IdentifierExp ident, TOK tok, Type tspec) {
		this.targ = targ;
		this.id = ident;
		this.tok = tok;
		this.tspec = tspec;
	}

	public IftypeDeclaration.Kind getKind() {
		if (tok == TOK.TOKreserved)
			return IftypeDeclaration.Kind.NONE;
		if (tok == TOK.TOKcolon)
			return IftypeDeclaration.Kind.EXTENDS;
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
			TreeVisitor.acceptChildren(visitor, id);
			TreeVisitor.acceptChildren(visitor, tspec);
		}
		visitor.endVisit(this);
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("iftype(");
		targ.toCBuffer(buf, id, hgs, context);
		if (tspec != null) {
			if (tok == TOK.TOKcolon)
				buf.writestring(" : ");
			else
				buf.writestring(" == ");
			tspec.toCBuffer(buf, null, hgs, context);
		}
		buf.writeByte(')');
	}

}
