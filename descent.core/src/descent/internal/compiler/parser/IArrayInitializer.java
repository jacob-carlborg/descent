package descent.internal.compiler.parser;

public interface IArrayInitializer extends IInitializer {
	
	IInitializer toAssocArrayInitializer(SemanticContext context);

}
