package descent.internal.core.dom;

import descent.core.dom.IModifier;

public enum PROT {

	PROTundefined(IModifier.UNDEFINED),
    PROTnone(IModifier.NONE),		// no access
    PROTprivate(IModifier.PRIVATE),
    PROTpackage(IModifier.PACKAGE),
    PROTprotected(IModifier.PROTECTED),
    PROTpublic(IModifier.PUBLIC),
    PROTexport(IModifier.EXPORT);
	
	PROT(int p) {
		this.protection = p;
	}
	
	private int protection;
	
	public int getModifiers() {
		return protection;
	}
	
	static PROT fromToken(TOK tok) {
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
