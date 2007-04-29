package dtool.dom.declarations;

import descent.internal.core.dom.TOK;

public final class Def_Modifiers {
	
    public static final int MODnone    = 0;
    public static final int MODstatic	    = 1;
    public static final int MODextern	    = 2;
    public static final int MODconst	    = 4;
    public static final int MODfinal	    = 8;
    public static final int MODabstract     = 0x10;
    public static final int MODparameter    = 0x20;
    public static final int MODfield	    = 0x40;
    public static final int MODoverride	    = 0x80;
    public static final int MODauto         = 0x100;
    public static final int MODsynchronized = 0x200;
    public static final int MODdeprecated   = 0x400;
    public static final int MODin           = 0x800;		// in parameter
    public static final int MODout          = 0x1000;		// out parameter
    public static final int MODlazy	    = 0x2000;		// lazy parameter
    public static final int MODforeach      = 0x4000;		// variable for foreach loop
    public static final int MODcomdat       = 0x8000;		// should go into COMDAT record
    public static final int MODvariadic     = 0x10000;		// variadic function argument
    public static final int MODctorinit     = 0x20000;		// can only be set inside constructor
    public static final int MODtemplateparameter = 0x40000;	// template parameter
    public static final int MODscope = 0x80000;	// RAII
    
        
    /*public static int getModifiers(int MOD) {
    	int r = 0;
    	if ((MOD & MODstatic) != 0) r |= IModifier.STATIC;
    	if ((MOD & MODextern) != 0) r |= IModifier.EXTERN;
    	if ((MOD & MODconst) != 0) r |= IModifier.CONST;
    	if ((MOD & MODfinal) != 0) r |= IModifier.FINAL;
    	if ((MOD & MODabstract) != 0) r |= IModifier.ABSTRACT;
    	if ((MOD & MODoverride) != 0) r |= IModifier.OVERRIDE;
    	if ((MOD & MODauto) != 0) r |= IModifier.AUTO;
    	if ((MOD & MODsynchronized) != 0) r |= IModifier.SYNCHRONIZED;
    	if ((MOD & MODdeprecated) != 0) r |= IModifier.DEPRECATED;
    	if ((MOD & MODscope) != 0) r |= IModifier.SCOPE;
    	return r;
    }*/


	public static int adaptFromDescent(int modifiers) {
		return modifiers;
	}


	public static String toString(int modifiers) {
		String str = "";
    	if ((modifiers & MODstatic) != 0) str += TOK.TOKstatic + " ";
    	if ((modifiers & MODauto) != 0) str += TOK.TOKauto + " ";
    	if ((modifiers & MODscope) != 0) str += TOK.TOKscope + " ";
    	if ((modifiers & MODfinal) != 0) str += TOK.TOKfinal + " ";
    	if ((modifiers & MODconst) != 0) str += TOK.TOKconst + " ";
    	
    	if(str.length() > 1) 
    		str = str.substring(0, str.length()-1);
		return str;
	}

}
