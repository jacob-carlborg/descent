package descent.internal.compiler.parser;

public interface IDeclaration extends IDsymbol {
	
	Type type();
	
	boolean isFinal();
	
	boolean isCtorinit();
	
	int storage_class();
	
	void storage_class(int storage_class);
	
	boolean isAuto();
	
	boolean isScope();
	
	boolean isParameter();
	
	LINK linkage();
	
	void linkage(LINK linkage);
	
	PROT protection();
	
	void protection(PROT protection);
	
	boolean isDataseg(SemanticContext context);
	
	boolean isStatic();
	
	boolean isConst();
	
	boolean isOut();
	
	boolean isRef();

}
