package descent.core.dom;

public interface IParser {
	
	ICompilationUnit parseCompilationUnit(String source);
	
	IExpression parseExpression(String source);
	
	IStatement parseStatement(String source);

}
