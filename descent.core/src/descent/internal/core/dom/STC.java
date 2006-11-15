package descent.internal.core.dom;

import descent.core.dom.IModifier;

public final class STC {
	
    public static final int STCundefined    = 0;
    public static final int STCstatic	    = 1;
    public static final int STCextern	    = 2;
    public static final int STCconst	    = 4;
    public static final int STCfinal	    = 8;
    public static final int STCabstract     = 0x10;
    public static final int STCparameter    = 0x20;
    public static final int STCfield	    = 0x40;
    public static final int STCoverride	    = 0x80;
    public static final int STCauto         = 0x100;
    public static final int STCsynchronized = 0x200;
    public static final int STCdeprecated   = 0x400;
    public static final int STCin           = 0x800;		// in parameter
    public static final int STCout          = 0x1000;		// out parameter
    public static final int STClazy	    = 0x2000;		// lazy parameter
    public static final int STCforeach      = 0x4000;		// variable for foreach loop
    public static final int STCcomdat       = 0x8000;		// should go into COMDAT record
    public static final int STCvariadic     = 0x10000;		// variadic function argument
    public static final int STCctorinit     = 0x20000;		// can only be set inside constructor
    public static final int STCtemplateparameter = 0x40000;	// template parameter
    
    public static int fromToken(TOK tok) {
    	switch(tok) {
    	case TOKstatic:
			return STCstatic;
		case TOKextern:
			return STCextern;
		case TOKconst:
			return STCconst;
		case TOKfinal:
			return STCfinal;
		case TOKabstract:
			return STCabstract;
		case TOKoverride:
			return STCoverride;
		case TOKauto:
			return STCauto;
		case TOKsynchronized:
			return STCsynchronized;
		case TOKdeprecated:
			return STCdeprecated;
		case TOKin:
			return STCin;
		case TOKout:
			return STCout;
		case TOKlazy:
			return STClazy;
		case TOKforeach:
			return STCforeach;
    	}
    	return STCundefined;
    }
    
    public static int getModifiers(int stc) {
    	int r = 0;
    	if ((stc & STCstatic) != 0) r |= IModifier.STATIC;
    	if ((stc & STCextern) != 0) r |= IModifier.EXTERN;
    	if ((stc & STCconst) != 0) r |= IModifier.CONST;
    	if ((stc & STCfinal) != 0) r |= IModifier.FINAL;
    	if ((stc & STCabstract) != 0) r |= IModifier.ABSTRACT;
    	if ((stc & STCoverride) != 0) r |= IModifier.OVERRIDE;
    	if ((stc & STCauto) != 0) r |= IModifier.AUTO;
    	if ((stc & STCsynchronized) != 0) r |= IModifier.SYNCHRONIZED;
    	if ((stc & STCdeprecated) != 0) r |= IModifier.DEPRECATED;
    	return r;
    }

}
