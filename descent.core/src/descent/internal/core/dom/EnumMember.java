package descent.internal.core.dom;

import descent.core.dom.IDElementVisitor;
import descent.core.dom.IEnumMember;
import descent.core.dom.IExpression;
import descent.core.dom.IName;

public class EnumMember extends Dsymbol implements IEnumMember {
	
	public Expression value;

	public EnumMember(Identifier id, Expression value) {
		this.ident = id;
		this.value = value;
		this.start = id.start;
		if (value == null) {
			this.length = this.ident.length;
		} else {
			this.length = value.start + value.length - this.start;
		}
	}
	
	public IName getName() {
		return ident;
	}
	
	public IExpression getValue() {
		return value;
	}
	
	public int getElementType() {
		return ENUM_MEMBER;
	}
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, ident);
			acceptChild(visitor, value);
		}
		visitor.endVisit(this);
	}
	
}
