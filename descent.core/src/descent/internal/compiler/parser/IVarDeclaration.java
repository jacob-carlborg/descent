package descent.internal.compiler.parser;

public interface IVarDeclaration extends IDeclaration {
	
	boolean isConst();
	
	IInitializer init();
	
	int inuse();
	
	boolean ctorinit();
	
	void ctorinit(boolean c);
	
	boolean noauto();
	
	Expression value();
	
	void value(Expression value);
	
	boolean isDataseg(SemanticContext context);
	
	int offset();
	
	void offset(int offset);
	
	IExpInitializer getExpInitializer(SemanticContext context);
	
	int canassign();

	void checkNestedReference(Scope sc, Loc loc, SemanticContext context);

	void init(IInitializer init);
	
	Expression callAutoDtor();

}
