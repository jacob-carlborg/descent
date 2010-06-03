package descent.internal.compiler.problem;

import java.io.IOException;

import descent.core.compiler.IProblem;
import descent.internal.compiler.CompilationResult;

/*
 * Special unchecked exception type used 
 * to abort from the compilation process
 *
 * should only be thrown from within problem handlers.
 */
public class AbortCompilationUnit extends AbortCompilation {

	private static final long serialVersionUID = -4253893529982226734L; // backward compatible
	
	public String encoding;
	
public AbortCompilationUnit(CompilationResult compilationResult, IProblem problem) {
	super(compilationResult, problem);
}

/**
 * Used to surface encoding issues when reading sources
 */
public AbortCompilationUnit(CompilationResult compilationResult, IOException exception, String encoding) {
	super(compilationResult, exception);
	this.encoding = encoding;
}
}
