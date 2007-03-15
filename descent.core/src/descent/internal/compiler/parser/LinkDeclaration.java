package descent.internal.compiler.parser;

import java.util.List;


public class LinkDeclaration extends AttribDeclaration {
	
	public LINK linkage;

	public LinkDeclaration(LINK linkage, List<Dsymbol> decl) {
		super(decl);
		this.linkage = linkage;
	}
	
	@Override
	public int kind() {
		return LINK_DECLARATION;
	}

}
