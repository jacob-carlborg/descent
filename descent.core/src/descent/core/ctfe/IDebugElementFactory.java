package descent.core.ctfe;

import descent.core.ICompilationUnit;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.Initializer;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.TupleDeclaration;
import descent.internal.compiler.parser.Type;

public interface IDebugElementFactory {

	IDescentVariable newVariable(int stackFrame, String name, Expression value);
	
	IDescentVariable newVariable(int stackFrame, String name, Type value);
	
	IDescentVariable newVariable(int stackFrame, String name, String value);
	
	IDescentVariable newVariable(int stackFrame, String name, TupleDeclaration value);
	
	IDescentVariable newVariable(int stackFrame, String name, Initializer value);

	IDescentStackFrame newStackFrame(String name, int number,
			ICompilationUnit unit, int line, Scope scope, InterState is);

}
