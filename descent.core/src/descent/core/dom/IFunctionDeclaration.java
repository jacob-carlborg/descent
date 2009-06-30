package descent.core.dom;

import java.util.List;

public interface IFunctionDeclaration
{
	List<Argument> arguments();
	boolean isVariadic();
	Block getPrecondition();
	Block getPostcondition();
	SimpleName getPostconditionVariableName();
	Block getBody();
}
