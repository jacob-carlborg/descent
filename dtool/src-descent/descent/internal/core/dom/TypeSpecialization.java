package descent.internal.core.dom;

import descent.core.dom.ITypeSpecialization;
import descent.core.domX.ASTVisitor;
import descent.core.domX.AbstractElement;

public class TypeSpecialization extends AbstractElement implements ITypeSpecialization {
	
	private int keyword;

	public TypeSpecialization(Token token) {
		switch(token.value) {
		case TOKtypedef: keyword = TYPEDEF; break;
		case TOKstruct: keyword = STRUCT; break;
		case TOKunion: keyword = UNION; break;
		case TOKclass: keyword = CLASS; break;
		case TOKsuper: keyword = SUPER; break;
		case TOKenum: keyword = ENUM; break;
		case TOKinterface: keyword = INTERFACE; break;
		case TOKfunction: keyword = FUNCTION; break;
		case TOKdelegate: keyword = DELEGATE; break;
		case TOKreturn: keyword = RETURN; break;
		}
		this.startPos = token.ptr;
		this.length = token.len;
	}

	public void accept0(ASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

	public int getElementType() {
		return ElementTypes.TYPE_SPECIALIZATION;
	}

	public int getKeyword() {
		return keyword;
	}

}
