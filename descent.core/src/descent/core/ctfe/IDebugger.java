package descent.core.ctfe;

import org.eclipse.core.resources.IResource;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IVariable;

import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.Scope;

public interface IDebugger {
	
	void initialize(IDebuggerListener listener, IDebugElementFactory elementFactory);
	
	void start();

	void terminate();
	
	void addBreakpoint(IResource resource, int lineNumber);

	void removeBreakpoint(IResource resource, int lineNumber);

	void stepBegin(ASTDmdNode node, Scope sc);
	
	void stepEnd(ASTDmdNode node, Scope sc);
	
	void stepBegin(ASTDmdNode node, InterState is);
	
	void stepEnd(ASTDmdNode node, InterState is);
	
	void enterStackFrame(ASTDmdNode node);
	
	void exitStackFrame();

	void stepInto();
	
	void stepInto(int stackFrame);

	void stepOver();
	
	void stepOver(int stackFrame);

	void stepReturn();
	
	void stepReturn(int stackFrame);

	void resume();
	
	void message(String message);
	
	IStackFrame[] getStackFrames();

	IVariable[] getVariables(int stackFrame);

	IVariable evaluateExpression(int stackFrame, String expression);

}
