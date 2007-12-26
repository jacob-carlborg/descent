package descent.internal.compiler.parser;

public interface ITypedefDeclaration extends IDeclaration {
	
	Type basetype();
	
	void basetype(Type basetype);
	
	IInitializer init();
	
	boolean inuse();
	
	void inuse(boolean inuse);
	
	int sem();

}
