package descent.internal.core.dom;

import descent.internal.core.dom.ExternDeclaration.Linkage;

public enum LINK {
	
    LINKdefault,
    LINKd,
    LINKc,
    LINKcpp,
    LINKwindows,
    LINKpascal;
    
    public Linkage getLinkage() {
    	switch(this) {
    	case LINKdefault: return Linkage.DEFAULT;
    	case LINKd: return Linkage.D;
    	case LINKc: return Linkage.C;
    	case LINKcpp: return Linkage.CPP;
    	case LINKwindows: return Linkage.WINDOWS;
    	case LINKpascal: return Linkage.PASCAL;
    	default: throw new RuntimeException("Can't happen?");
    	}
    }

}
