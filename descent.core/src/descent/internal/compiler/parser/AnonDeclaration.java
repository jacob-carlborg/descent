package descent.internal.compiler.parser;

import java.util.List;

public class AnonDeclaration extends AttribDeclaration {
	
	public boolean isunion;
	
	public AnonDeclaration(boolean isunion, List<Dsymbol> decl) {
		super(decl);
		this.isunion = isunion;
	}
	
	@Override
	public int getNodeType() {
		return ANON_DECLARATION;
	}

}
