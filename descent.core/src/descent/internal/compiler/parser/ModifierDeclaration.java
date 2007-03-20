package descent.internal.compiler.parser;

import java.util.List;

public class ModifierDeclaration extends Dsymbol  {
	
	public final Modifier modifier;
	public final List<Dsymbol> declarations;

	public ModifierDeclaration(Modifier modifier, List<Dsymbol> declarations) {
		this.modifier = modifier;
		this.declarations = declarations;		
	}
	
	@Override
	public int getNodeType() {
		return MODIFIER_DECLARATION;
	}

}
