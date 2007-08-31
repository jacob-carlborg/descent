package descent.internal.compiler.parser;

import java.util.List;

public class InterState
{
	InterState caller;		// calling function's InterState
    FuncDeclaration fd;	// function being interpreted
    List<Dsymbol> vars;		// variables used in this function
    Statement start;		// if !=null, start execution at this statement
    Statement gotoTarget;	// target of EXP_GOTO_INTERPRET result
}
