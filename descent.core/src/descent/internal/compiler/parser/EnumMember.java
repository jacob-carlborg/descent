package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class EnumMember extends Dsymbol implements IEnumMember {

	public Expression value, sourceValue;

	public EnumMember(Loc loc, IdentifierExp id, Expression value) {
		super(id);
		this.loc = loc;
		this.value = this.sourceValue = value;
		if (value == null) {
			start = id.start;
			length = id.length;
		} else {
			start = id.start;
			length = value.start + value.length - id.start;
		}
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, modifiers);
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, sourceValue);
		}
		visitor.endVisit(this);
	}

	@Override
	public int getNodeType() {
		return ENUM_MEMBER;
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
	public IDsymbol syntaxCopy(IDsymbol s, SemanticContext context) {
		Expression e = null;
		if (value != null) {
			e = value.syntaxCopy(context);
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
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring(ident.toChars());
		if (value != null) {
			buf.writestring(" = ");
			value.toCBuffer(buf, hgs, context);
		}
	}
	
	public Expression value() {
		return value;
	}
	
	public void value(Expression value) {
		this.value = value;
	}
	
	@Override
	public char getSignaturePrefix() {
		return ISignatureConstants.ENUM_MEMBER;
	}

}
