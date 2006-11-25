package descent.internal.core.dom;

import descent.core.dom.IDElementVisitor;
import descent.core.dom.IStrongType;

public class StrongType extends AbstractElement implements IStrongType {
	
	private int strongType;

	public StrongType(Token token) {
		switch(token.value) {
		case TOKtypedef: strongType = TYPEDEF; break;
		case TOKstruct: strongType = STRUCT; break;
		case TOKunion: strongType = UNION; break;
		case TOKclass: strongType = CLASS; break;
		case TOKsuper: strongType = SUPER; break;
		case TOKenum: strongType = ENUM; break;
		case TOKinterface: strongType = INTERFACE; break;
		case TOKfunction: strongType = FUNCTION; break;
		case TOKdelegate: strongType = DELEGATE; break;
		case TOKreturn: strongType = RETURN; break;
		}
		this.start = token.ptr;
		this.length = token.len;
	}

	public void accept(IDElementVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

	public int getElementType() {
		return STRONG_TYPE;
	}

	public int getStrongTypeType() {
		return strongType;
	}

}
