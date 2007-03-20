package descent.internal.compiler.parser;

import java.util.List;

public class AnonDeclaration extends AttribDeclaration {
	
	public boolean isunion;
	
	public AnonDeclaration(boolean isunion, List<Dsymbol> decl) {
		super(decl);
		this.isunion = isunion;
	}
	
	/* TODO semantic
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		Scope scx = null;
	    if (scope != null)
	    {   sc = scope;
		scx = scope;
		scope = NULL;
	    }

	    assert(sc.parent);

	    Dsymbol *parent = sc.parent.pastMixin();
	    AggregateDeclaration *ad = parent.isAggregateDeclaration();

	    if (!ad || (!ad.isStructDeclaration() && !ad.isClassDeclaration()))
	    {
		error("can only be a part of an aggregate");
		return;
	    }

	    if (decl)
	    {
		AnonymousAggregateDeclaration aad;
		int adisunion;

		if (sc.anonAgg)
		{   ad = sc.anonAgg;
		    adisunion = sc.inunion;
		}
		else
		    adisunion = ad.isUnionDeclaration() != NULL;

//		printf("\tsc.anonAgg = %p\n", sc.anonAgg);
//		printf("\tad  = %p\n", ad);
//		printf("\taad = %p\n", &aad);

		sc = sc.push();
		sc.anonAgg = &aad;
		sc.stc &= ~(STCauto | STCscope | STCstatic);
		sc.inunion = isunion;
		sc.offset = 0;
		sc.flags = 0;
		aad.structalign = sc.structalign;
		aad.parent = ad;

		for (unsigned i = 0; i < decl.dim; i++)
		{
		    Dsymbol *s = (Dsymbol *)decl.data[i];

		    s.semantic(sc);
		    if (isunion)
			sc.offset = 0;
		    if (aad.sizeok == 2)
		    {
			break;
		    }
		}
		sc = sc.pop();

		// If failed due to forward references, unwind and try again later
		if (aad.sizeok == 2)
		{
		    ad.sizeok = 2;
		    //printf("\tsetting ad.sizeok %p to 2\n", ad);
		    if (!sc.anonAgg)
		    {
			scope = scx ? scx : new Scope(*sc);
			scope.setNoFree();
			scope.module.addDeferredSemantic(this);
		    }
		    //printf("\tforward reference %p\n", this);
		    return;
		}
		if (sem == 0)
		{   Module::dprogress++;
		    sem = 1;
		    //printf("\tcompleted %p\n", this);
		}
		else
		    ;//printf("\talready completed %p\n", this);

		// 0 sized structs are set to 1 byte
		if (aad.structsize == 0)
		{
		    aad.structsize = 1;
		    aad.alignsize = 1;
		}

		// Align size of anonymous aggregate
//	printf("aad.structalign = %d, aad.alignsize = %d, sc.offset = %d\n", aad.structalign, aad.alignsize, sc.offset);
		ad.alignmember(aad.structalign, aad.alignsize, &sc.offset);
		//ad.structsize = sc.offset;
//	printf("sc.offset = %d\n", sc.offset);

		// Add members of aad to ad
		//printf("\tadding members of aad to '%s'\n", ad.toChars());
		for (unsigned i = 0; i < aad.fields.dim; i++)
		{
		    VarDeclaration *v = (VarDeclaration *)aad.fields.data[i];

		    v.offset += sc.offset;
		    ad.fields.push(v);
		}

		// Add size of aad to ad
		if (adisunion)
		{
		    if (aad.structsize > ad.structsize)
			ad.structsize = aad.structsize;
		    sc.offset = 0;
		}
		else
		{
		    ad.structsize = sc.offset + aad.structsize;
		    sc.offset = ad.structsize;
		}

		if (ad.alignsize < aad.alignsize)
		    ad.alignsize = aad.alignsize;
	    }
	}
	*/
	
	@Override
	public int getNodeType() {
		return ANON_DECLARATION;
	}

}
