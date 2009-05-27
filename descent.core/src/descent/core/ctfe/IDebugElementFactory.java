package descent.core.ctfe;

import descent.core.ICompilationUnit;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.Scope;

public interface IDebugElementFactory {

	IDescentVariable newVariable(int stackFrame, String name, Expression value);

	IDescentStackFrame newStackFrame(String name, int number,
			ICompilationUnit unit, int line, Scope scope, InterState is);

}
