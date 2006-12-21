package descent.core.dom;

import java.util.List;

import descent.internal.core.dom.BaseClass;
import descent.internal.core.dom.Declaration;
import descent.internal.core.dom.Expression;

public interface INewAnonymousClassExpression extends IExpression {
	
	Expression getExpression();
	
	List<Expression> newArguments();
	
	List<Expression> constructorArguments();
	
	List<BaseClass> baseClasses();
	
	List<Declaration> declarations();

}
