package descent.core.dom;

import descent.internal.core.dom.BaseClass;
import descent.internal.core.dom.Expression;

public interface INewAnonymousClassExpression extends IExpression {
	
	Expression[] getCallArguments();
	
	Expression[] getConstructorArguments();
	
	BaseClass[] getBaseClasses();
	
	IDeclaration[] getDeclarationDefinitions();

}
