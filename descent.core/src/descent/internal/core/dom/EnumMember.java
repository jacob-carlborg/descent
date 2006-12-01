package descent.internal.core.dom;

import descent.core.dom.ElementVisitor;
import descent.core.dom.IEnumMember;
import descent.core.dom.IExpression;
import descent.core.dom.IName;

public class EnumMember extends Dsymbol implements IEnumMember {
	
	public Expression value;

	public EnumMember(Identifier id, Expression value) {
		this.ident = id;
		this.value = value;
		this.startPosition = id.startPosition;
		if (value == null) {
			this.length = this.ident.length;
		} else {
			this.length = value.startPosition + value.length - this.startPosition;
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
	
	public void accept0(ElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, ident);
			acceptChild(visitor, value);
		}
		visitor.endVisit(this);
	}
	
}
