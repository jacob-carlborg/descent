package descent.internal.core.dom;

import java.util.List;

import descent.core.domX.ASTVisitor;


public class ScopeDsymbol extends Dsymbol {

	public DsymbolTable symtab;
	public List members;		// all Dsymbol's in this scope

	public void accept0(ASTVisitor visitor) {
	}

	public int getElementType() {
		return 0;
	}

	public void multiplyDefined(Dsymbol dsymbol, Dsymbol s2) {
		// TODO Auto-generated method stub
		
	}

}