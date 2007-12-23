package descent.internal.compiler.parser;

public interface IInitializer {
	
	IExpInitializer isExpInitializer();
	
	Expression toExpression(SemanticContext context);

}
