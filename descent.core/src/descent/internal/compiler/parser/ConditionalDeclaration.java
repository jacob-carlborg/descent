package descent.internal.compiler.parser;

import java.util.List;

public class ConditionalDeclaration extends AttribDeclaration {
	
	public Condition condition;
	public List<Dsymbol> elsedecl;
	
	public ConditionalDeclaration(Condition condition, List<Dsymbol> decl, List<Dsymbol> elsedecl) {
		super(decl);
		this.condition = condition;
		this.elsedecl = elsedecl;
	}
	
	@Override
	public int kind() {
		return CONDITIONAL_DECLARATION;
	}

}
