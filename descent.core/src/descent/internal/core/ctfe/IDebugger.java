package descent.internal.core.ctfe;

import org.eclipse.core.resources.IResource;

import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.Scope;

public interface IDebugger {
	
	void start();

	void terminate();
	
	void addBreakpoint(IResource resource, int lineNumber);

	void removeBreakpoint(IResource resource, int lineNumber);

	void stepBegin(ASTDmdNode node, Scope sc);
	
	void stepEnd(ASTDmdNode node, Scope sc);

}
