package descent.internal.compiler.parser.ast;

import org.eclipse.core.resources.IMarker;

import descent.core.compiler.IProblem;

// XXX: DLTK interface adapter
public interface IProblemReporter {
	IMarker reportProblem(IProblem problem) /*throws CoreException */;
}