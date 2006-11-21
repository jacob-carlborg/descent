package descent.core.dom;

public interface INewAnonymousClassExpression extends IExpression {
	
	IExpression[] getCallArguments();
	
	IExpression[] getConstructorArguments();
	
	IBaseClass[] getBaseClasses();
	
	IDElement[] getDeclarationDefinitions();

}
