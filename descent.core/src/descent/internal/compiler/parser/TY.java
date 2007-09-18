package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.TypeBasic.*;

// DMD 1.020
public enum TY {
	
    Tarray('A'),		// dynamic array
    Tsarray('G'),		// static array
    Taarray('H'),		// associative array
    Tpointer('P'),
    Treference('R'),
    Tfunction('F'),
    Tident('I'),
    Tclass('C'),
    Tstruct('S'),
    Tenum('E'),
    Ttypedef('T'),
    Tdelegate('D'),

    Tnone('n'),
    Tvoid('v', "void", 0),
    Tint8('g', "byte", TFLAGSintegral),
    Tuns8('h', "ubyte", TFLAGSintegral | TFLAGSunsigned),
    Tint16('s', "short", TFLAGSintegral),
    Tuns16('t', "ushort", TFLAGSintegral | TFLAGSunsigned),
    Tint32('i', "int", TFLAGSintegral),
    Tuns32('k', "uint", TFLAGSintegral | TFLAGSunsigned),
    Tint64('l', "long", TFLAGSintegral),
    Tuns64('m', "ulong", TFLAGSintegral | TFLAGSunsigned),
    Tfloat32('f', "float", TFLAGSfloating | TFLAGSreal),
    Tfloat64('d', "double", TFLAGSfloating | TFLAGSreal),
    Tfloat80('e', "real", TFLAGSfloating | TFLAGSreal),

    Timaginary32('o', "ifloat", TFLAGSfloating | TFLAGSimaginary),
    Timaginary64('p', "idouble", TFLAGSfloating | TFLAGSimaginary),
    Timaginary80('j', "ireal", TFLAGSfloating | TFLAGSimaginary),
    Tcomplex32('q', "cfloat", TFLAGSfloating | TFLAGScomplex),
    Tcomplex64('r', "cdouble", TFLAGSfloating | TFLAGScomplex),
    Tcomplex80('c', "creal", TFLAGSfloating | TFLAGScomplex),

    Tbool('b', "bool", TFLAGSintegral | TFLAGSunsigned),
    Tchar('a', "char", TFLAGSintegral | TFLAGSunsigned),
    Twchar('u', "wchar", TFLAGSintegral | TFLAGSunsigned),
    Tdchar('w', "dchar", TFLAGSintegral | TFLAGSunsigned),
    
    Tbit('@', "bit", TFLAGSintegral | TFLAGSunsigned),
    Tinstance('@'),
    Terror('@'),    
    Ttypeof('@'),
    Ttuple('B'),
    Tslice('@'),
    
    ;
    
    public int flags;
    public String name;
    public char mangleChar;
    
    TY(char mangleChar) {
    	this.mangleChar = mangleChar;
    }
    
    TY(char mangleChar, String name, int flags) {
    	this.mangleChar = mangleChar;
    	this.name = name;
    	this.flags = flags;
    }

}