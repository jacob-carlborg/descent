package descent.internal.compiler.parser;

// DMD 1.020
public class InterState
{
	InterState caller;		// calling function's InterState
    FuncDeclaration fd;	// function being interpreted
    Dsymbols vars;		// variables used in this function
    Statement start;		// if !=null, start execution at this statement
    Statement gotoTarget;	// target of EXP_GOTO_INTERPRET result
    
    boolean stackOverflow; // for Descent: tells if the evaluation resulted in a stack overflow
    
}
