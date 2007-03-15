package descent.internal.compiler.parser;

import java.util.List;

public class AlignDeclaration extends AttribDeclaration {
	
	public int salign;

	public AlignDeclaration(int sa, List<Dsymbol> decl) {
		super(decl);
		this.salign = sa;
	}
	
	@Override
    public int kind() {
    	return ALIGN_DECLARATION;
    }

}
