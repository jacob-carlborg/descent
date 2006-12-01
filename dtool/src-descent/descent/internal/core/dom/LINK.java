package descent.internal.core.dom;

import descent.core.dom.ILinkDeclaration;

public enum LINK {
	
    LINKdefault,
    LINKd,
    LINKc,
    LINKcpp,
    LINKwindows,
    LINKpascal;
    
    public int getLinkage() {
    	switch(this) {
    	case LINKd: return ILinkDeclaration.LINKAGE_D;
    	case LINKc: return ILinkDeclaration.LINKAGE_C;
    	case LINKcpp: return ILinkDeclaration.LINKAGE_CPP;
    	case LINKwindows: return ILinkDeclaration.LINKAGE_WINDOWS;
    	case LINKpascal: return ILinkDeclaration.LINKAGE_PASCAL;
    	default: throw new RuntimeException("Can't happen?");
    	}
    }

}
