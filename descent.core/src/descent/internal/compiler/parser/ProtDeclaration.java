package descent.internal.compiler.parser;

import java.util.List;

import descent.core.IProblemRequestor;

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
	public void semantic(Scope sc, IProblemRequestor problemRequestor) {
		List<Dsymbol> d = include(sc, null);

	    //printf("\tAttribDeclaration::semantic '%s'\n",toChars());
	    if (d)
	    {
		for (unsigned i = 0; i < d->dim; i++)
		{
		    Dsymbol *s = (Dsymbol *)d->data[i];

		    s->semantic(sc);
		}
	    }
	}

	@Override
	public int kind() {
		return PROT_DECLARATION;
	}

}
