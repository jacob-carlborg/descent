package dtool.dom.definitions;

import descent.internal.compiler.parser.PROT;


public enum Def_EProtection {

	PROTundefined,
    PROTnone,
    PROTprivate,
    PROTpackage,
    PROTprotected,
    PROTpublic,
    PROTexport;
	
	public static PROT adaptFromDescent(descent.internal.compiler.parser.PROT prot) {
		return prot;
		//if(mod == null) return null;
		/*
		switch(prot.getModifiers()) {
		case descent.internal.compiler.parser.IModifier.UNDEFINED: return PROTundefined; 
		case descent.core.dom.IModifier.NONE: return PROTnone; 
		case descent.core.dom.IModifier.PRIVATE: return PROTprivate; 
		case descent.core.dom.IModifier.PACKAGE: return PROTpackage; 
		case descent.core.dom.IModifier.PROTECTED: return PROTprotected; 
		case descent.core.dom.IModifier.PUBLIC: return PROTpublic; 
		case descent.core.dom.IModifier.EXPORT: return PROTexport; 
		default: assert false; return null;
		}*/
	}

	
}
