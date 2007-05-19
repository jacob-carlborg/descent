package descent.core.dom;

import java.util.List;

public interface IFunctionDeclaration
{
	public List<Argument> arguments();
	public boolean isVariadic();
	public Statement getPrecondition();
	public Statement getPostcondition();
	public SimpleName getPostconditionVariableName();
	public Statement getBody();
}
