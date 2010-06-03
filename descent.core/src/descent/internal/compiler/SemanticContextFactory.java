package descent.internal.compiler;

import descent.core.IJavaProject;
import descent.core.IProblemRequestor;
import descent.core.JavaModelException;
import descent.core.WorkingCopyOwner;
import descent.core.ctfe.IDebugger;
import descent.core.dom.AST;
import descent.internal.compiler.env.IModuleFinder;
import descent.internal.compiler.env.INameEnvironment;
import descent.internal.compiler.lookup.DescentModuleFinder;
import descent.internal.compiler.parser.ASTNodeEncoder;
import descent.internal.compiler.parser.Global;
import descent.internal.compiler.parser.IStringTableHolder;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.core.CancelableNameEnvironment;
import descent.internal.core.CompilerConfiguration;
import descent.internal.core.JavaProject;
import descent.internal.core.ctfe.CompileTimeModuleFinder;
import descent.internal.core.ctfe.dom.CompileTimeSemanticContext;
import descent.internal.core.util.Util;

public class SemanticContextFactory {
	
	public static SemanticContext createSemanticContext(IProblemRequestor problemRequestor, Module module,
			IJavaProject project, WorkingCopyOwner owner, Global global, CompilerConfiguration config,
			ASTNodeEncoder encoder, IStringTableHolder stringTableHolder) throws JavaModelException {
		return new SemanticContext(problemRequestor, module, owner, global, config, encoder, stringTableHolder, 
				newModuleFinder(new CancelableNameEnvironment((JavaProject) project, owner, null), config, encoder), getAPILevel(project));
	}
	
	
	public static CompileTimeSemanticContext createCompileTimeSemanticContext(IProblemRequestor problemRequestor,
			Module module, IJavaProject project, WorkingCopyOwner owner, Global global, CompilerConfiguration config,
			ASTNodeEncoder encoder, IStringTableHolder holder, IDebugger debugger) throws JavaModelException {
		return new CompileTimeSemanticContext(problemRequestor, module, owner, global, config, encoder, holder,
				newModuleFinder_CT(new CancelableNameEnvironment((JavaProject) project, owner, null), config, encoder), getAPILevel(project), debugger);
	}
	
	private static int getAPILevel(IJavaProject project) {
		return (project == null) ? AST.D1 : Util.getApiLevel(project);
	}
	
	
	public static IModuleFinder newModuleFinder(INameEnvironment env, CompilerConfiguration config, ASTNodeEncoder encoder) {
		return new DescentModuleFinder(env, config, encoder);
	}
	
	public static IModuleFinder newModuleFinder_CT(INameEnvironment env, CompilerConfiguration config, ASTNodeEncoder encoder2) {
		return new CompileTimeModuleFinder(env, config, encoder2);
	}
	
}
