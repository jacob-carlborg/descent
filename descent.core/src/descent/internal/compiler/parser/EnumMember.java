package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.IField;
import descent.core.Signature;
import descent.internal.compiler.parser.ast.IASTVisitor;


public class EnumMember extends Dsymbol {

	public Expression value, sourceValue;
	public Type type, sourceType;
	
	private IField javaElement;

	public EnumMember(Loc loc, IdentifierExp id, Expression value) {
		this(loc, id, value, null);
	}
	
	public EnumMember(Loc loc, IdentifierExp id, Expression value, Type type) {
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
		this.type = type;
		this.sourceType = type;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, modifiers);
			TreeVisitor.acceptChildren(visitor, sourceType);
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
	public Dsymbol syntaxCopy(Dsymbol s, SemanticContext context) {
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
		
		em.copySourceRange(this);
		em.javaElement = javaElement;
		
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
		return Signature.C_ENUM_MEMBER;
	}
	
	public void setJavaElement(IField field) {
		this.javaElement = field;
	}
	
	@Override
	public IField getJavaElement() {
		return javaElement;
	}

}
