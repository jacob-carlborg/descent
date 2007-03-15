package descent.internal.compiler.parser;

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
    Tvoid('v'),
    Tint8('g'),
    Tuns8('h'),
    Tint16('s'),
    Tuns16('t'),
    Tint32('i'),
    Tuns32('k'),
    Tint64('l'),
    Tuns64('m'),
    Tfloat32('f'),
    Tfloat64('d'),
    Tfloat80('e'),

    Timaginary32('o'),
    Timaginary64('p'),
    Timaginary80('j'),
    Tcomplex32('q'),
    Tcomplex64('r'),
    Tcomplex80('c'),

    Tbool('b'),
    Tchar('a'),
    Twchar('u'),
    Tdchar('w'),
    
    Tbit('@'),
    Tinstance('@'),
    Terror('@'),    
    Ttypeof('@'),
    Ttuple('B'),
    Tslice('@'),
    
    ;
    
    public char mangleChar;
    
    TY(char mangleChar) {
    	this.mangleChar = mangleChar;
    }

}