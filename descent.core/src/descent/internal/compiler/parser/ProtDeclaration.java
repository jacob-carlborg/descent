package descent.internal.compiler.parser;

import java.util.List;

public class ProtDeclaration extends AttribDeclaration {

	public Modifier modifier;
	public boolean single;
	public PROT protection;

	public ProtDeclaration(PROT p, List<Dsymbol> decl, Modifier modifier, boolean single) {
		super(decl);
		this.protection = p;		
		this.modifier = modifier;
		this.single = single;
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		if (decl != null && decl.size() > 0) {	
			PROT protection_save = sc.protection;
			int explicitProtection_save = sc.explicitProtection;

			sc.protection = protection;
			sc.explicitProtection = 1;
			
			for(Dsymbol s : decl) {
	    		s.semantic(sc, context);
	    	}
			
			sc.protection = protection_save;
			sc.explicitProtection = explicitProtection_save;
	    } else {	
	    	sc.protection = protection;
	    	sc.explicitProtection = 1;
	    }
	}

	@Override
	public int kind() {
		return PROT_DECLARATION;
	}

}
