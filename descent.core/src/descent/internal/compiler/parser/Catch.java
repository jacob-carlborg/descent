package descent.internal.compiler.parser;

public class Catch extends ASTNode {

	public Type type;
	public IdentifierExp id;
	public Statement handler;

	public Catch(Type type, IdentifierExp id, Statement handler) {
		this.type = type;
		this.id = id;
		this.handler = handler;		
	}
	
	@Override
    public int kind() {
    	return CATCH;
    }

}
