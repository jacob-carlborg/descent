package descent.internal.core.dom;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.dom.IEnumMember;
import descent.core.dom.IExpression;
import descent.core.dom.IName;
import descent.core.domX.IASTVisitor;

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
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, ident);
			TreeVisitor.acceptChild(visitor, value);
		}
		visitor.endVisit(this);
	}
	
}
