package descent.internal.core.ctfe;

import descent.core.IJavaProject;
import descent.core.IProblemRequestor;
import descent.internal.compiler.env.IModuleFinder;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.ASTNodeEncoder;
import descent.internal.compiler.parser.Global;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.core.CompilerConfiguration;

public class CompileTimeSemanticContext extends SemanticContext {
	
	private final IDebugger debugger;

	public CompileTimeSemanticContext(IProblemRequestor problemRequestor,
			Module module, IJavaProject project, IModuleFinder moduleFinder,
			Global global, CompilerConfiguration config, ASTNodeEncoder encoder, IDebugger debugger) {
		super(problemRequestor, module, project, moduleFinder, global, config, encoder);
		this.debugger = debugger;
	}
	
	public void stepBegin(ASTDmdNode node, Scope sc) {
		debugger.stepBegin(node, sc);
	}
	
	public void stepEnd(ASTDmdNode node, Scope sc) {
		debugger.stepEnd(node, sc);
	}

}
