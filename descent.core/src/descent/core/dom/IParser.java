package descent.core.dom;


public interface IParser {
	
	CompilationUnit parseCompilationUnit(String source);
	
	Expression parseExpression(String source);
	
	Statement parseStatement(String source);
	
	Initializer parseInitializer(String source);

}
