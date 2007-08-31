package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class EnumMember extends Dsymbol {

	public Expression value;

	public EnumMember(Loc loc, IdentifierExp id, Expression value) {
		super(loc, id);
		this.value = value;
		if (value == null) {
			start = id.start;
			length = id.length;
		} else {
			start = id.start;
			length = value.start + value.length - id.start;
		}
	}

	@Override
	public int getNodeType() {
		return ENUM_MEMBER;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, modifiers);
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, value);
		}
		visitor.endVisit(this);
	}


	@Override
	public EnumMember isEnumMember() {
		return this;
	}

	@Override
	public String kind() {
		return "enum member";
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s) {
		Expression e = null;
		if (value != null) {
			e = value.syntaxCopy();
		}

		EnumMember em;
		if (s != null) {
			em = (EnumMember) s;
			em.value = e;
		} else {
			em = new EnumMember(loc, ident, e);
		}
		return em;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		buf.writestring(ident.toChars(context));
		if (value != null) {
			buf.writestring(" = ");
			value.toCBuffer(buf, hgs, context);
		}
	}

}
