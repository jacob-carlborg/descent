package descent.internal.core.dom;

import descent.core.dom.IEnumMember;
import descent.core.dom.IExpression;
import descent.core.dom.IName;
import descent.core.dom.IElement.ElementTypes;
import descent.core.domX.ASTVisitor;

public class EnumMember extends Dsymbol implements IEnumMember {
	
	public Expression value;

	public EnumMember(Identifier id, Expression value) {
		this.ident = id;
		this.value = value;
		this.startPos = id.startPos;
		if (value == null) {
			this.length = this.ident.length;
		} else {
			this.length = value.startPos + value.length - this.startPos;
		}
	}
	
	public IName getName() {
		return ident;
	}
	
	public IExpression getValue() {
		return value;
	}
	
	public int getElementType() {
		return ElementTypes.ENUM_MEMBER;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, ident);
			acceptChild(visitor, value);
		}
		visitor.endVisit(this);
	}
	
}
