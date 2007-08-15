package descent.core.dom;

import java.util.List;

public interface IFunctionDeclaration
{
	public List<Argument> arguments();
	public boolean isVariadic();
	public Block getPrecondition();
	public Block getPostcondition();
	public SimpleName getPostconditionVariableName();
	public Block getBody();
}
