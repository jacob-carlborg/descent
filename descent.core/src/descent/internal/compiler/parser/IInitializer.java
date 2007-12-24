package descent.internal.compiler.parser;

public interface IInitializer extends INode {
	
	IExpInitializer isExpInitializer();
	
	IArrayInitializer isArrayInitializer();
	
	IVoidInitializer isVoidInitializer();
	
	Expression toExpression(SemanticContext context);
	
	Type inferType(Scope sc, SemanticContext context);
	
	Loc loc();
	
	IInitializer semantic(Scope sc, Type t, SemanticContext context);
	
	IInitializer syntaxCopy(SemanticContext context);
	
	void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context);

}
