package descent.internal.compiler.parser;

import java.util.List;

public abstract class AttribDeclaration extends Dsymbol {
	
	public List<Dsymbol> decl;
	
	public AttribDeclaration(List<Dsymbol> decl) {
		this.decl = decl;
	}

}
