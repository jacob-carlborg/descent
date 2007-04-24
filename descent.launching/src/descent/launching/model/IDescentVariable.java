package descent.launching.model;

import org.eclipse.debug.core.model.IVariable;

public interface IDescentVariable extends IVariable {
	
	void addChild(IDescentVariable variable);
	
	IDescentVariable getParent();

}
