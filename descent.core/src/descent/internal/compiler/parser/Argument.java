package descent.internal.compiler.parser;

import descent.core.dom.Argument.PassageMode;

public class Argument extends ASTNode {
	
	public PassageMode inout;
	public Type type;
	public IdentifierExp ident;
	public Expression defaultArg;
    
    public Argument(PassageMode inout, Type type, IdentifierExp ident, Expression defaultArg) {
		this.inout = inout;
		this.type = type;
		this.ident = ident;
		this.defaultArg = defaultArg;
	}
    
    @Override
    public int kind() {
    	return ARGUMENT;
    }

}
