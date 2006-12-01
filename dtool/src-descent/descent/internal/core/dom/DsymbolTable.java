package descent.internal.core.dom;

public class DsymbolTable {
	
	StringTable tab;
	
	public DsymbolTable() {
		tab = new StringTable();
	}

	public Dsymbol insert(Dsymbol s) {
		StringValue sv;
	    Identifier ident;

	    //printf("DsymbolTable::insert(this = %p, '%s')\n", this, s->ident->toChars());
	    ident = s.ident;
	    sv = tab.insert(ident.toString());
	    if (sv == null)
	    	return null;		// already in table
	    sv.ptrvalue = s;
	    return s;
	}

	public Dsymbol lookup(Identifier ident) {
		StringValue sv;

	    sv = tab.lookup(ident.string);
	    return sv != null ? (Dsymbol) sv.ptrvalue : null;
	}

}
