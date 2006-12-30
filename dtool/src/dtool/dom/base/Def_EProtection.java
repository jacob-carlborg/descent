package dtool.dom.base;

import descent.core.dom.IModifier;

public enum Def_EProtection {

	PROTundefined,
    PROTnone,
    PROTprivate,
    PROTpackage,
    PROTprotected,
    PROTpublic,
    PROTexport;
	
	public static Def_EProtection adaptFromDescent(descent.internal.core.dom.PROT prot) {
		//if(mod == null) return null;
		switch(prot.getModifiers()) {
		case descent.core.dom.IModifier.UNDEFINED: return PROTundefined; 
		case descent.core.dom.IModifier.NONE: return PROTnone; 
		case descent.core.dom.IModifier.PRIVATE: return PROTprivate; 
		case descent.core.dom.IModifier.PACKAGE: return PROTpackage; 
		case descent.core.dom.IModifier.PROTECTED: return PROTprotected; 
		case descent.core.dom.IModifier.PUBLIC: return PROTpublic; 
		case descent.core.dom.IModifier.EXPORT: return PROTexport; 
		default: assert false; return null;
		}
	}

	public static Def_EProtection adaptFromDescent(int modifiers) {

		if(util.BitFields.countActiveFlags(modifiers, IModifier.protModifiers) > 1)
			assert false;
		
    	if ((modifiers & IModifier.PUBLIC) != 0) return Def_EProtection.PROTpublic;
    	if ((modifiers & IModifier.PACKAGE) != 0) return Def_EProtection.PROTpackage;
    	if ((modifiers & IModifier.PRIVATE) != 0) return Def_EProtection.PROTprivate;
    	if ((modifiers & IModifier.PROTECTED) != 0) return Def_EProtection.PROTprotected;
    	if ((modifiers & IModifier.EXPORT) != 0) return Def_EProtection.PROTexport;
    	if ((modifiers & IModifier.UNDEFINED) != 0) return Def_EProtection.PROTundefined;
    	
    	return Def_EProtection.PROTnone;
	}	
	
	
	// TODO: link with tokens?
	public String toString() {
		switch(this){
		case PROTundefined: return "UNDEFINED??"; // XXX: what is PROTundefined??
		case PROTnone: return "";
		case PROTprivate: return "private";
		case PROTpackage: return "package";
		case PROTprotected: return "protected";
		case PROTpublic: return "public";
		case PROTexport: return "export"; 
		default: assert false; return null;
		}
	}
		
}
