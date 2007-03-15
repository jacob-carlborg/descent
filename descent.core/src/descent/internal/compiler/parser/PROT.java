package descent.internal.compiler.parser;

public enum PROT {
	
	PROTundefined,
    PROTnone,		// no access
    PROTprivate,
    PROTpackage,
    PROTprotected,
    PROTpublic,
    PROTexport,
    
    ;
	
	public static PROT fromTOK(TOK tok) {
		switch(tok) {
		case TOKprivate: return PROTprivate;
		case TOKpackage: return PROTpackage;
		case TOKprotected: return PROTprotected;
		case TOKpublic: return PROTpublic;
		case TOKexport: return PROTexport;
		}
		return PROTundefined;
	}

}
