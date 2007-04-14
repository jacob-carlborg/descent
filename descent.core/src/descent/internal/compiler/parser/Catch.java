package descent.internal.compiler.parser;

public class Catch extends ASTNode {

	public Loc loc;
	public Type type;
	public IdentifierExp id;
	public Statement handler;

	public Catch(Loc loc, Type type, IdentifierExp id, Statement handler) {
		this.loc = loc;
		this.type = type;
		this.id = id;
		this.handler = handler;		
	}
	
	@Override
    public int getNodeType() {
    	return CATCH;
    }

}
