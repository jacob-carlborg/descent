/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package descent.core;

/**
 * Markers used by the Java model.
 * <p>
 * This interface declares constants only; it is not intended to be implemented
 * or extended.
 * </p>
 */
public interface IJavaModelMarker {

	/**
	 * Java model problem marker type (value
	 * <code>"descent.core.problem"</code>). This can be used to
	 * recognize those markers in the workspace that flag problems detected by
	 * the Java tooling during compilation.
	 */
	String JAVA_MODEL_PROBLEM_MARKER = JavaCore.PLUGIN_ID + ".problem"; //$NON-NLS-1$

	/**
	 * Java model transient problem marker type (value
	 * <code>"descent.core.transient_problem"</code>). This can be
	 * used to recognize those markers in the workspace that flag transient
	 * problems detected by the Java tooling (such as a problem detected by the
	 * outliner, or a problem detected during a code completion). Since 1.0,
	 * transient problems are reported as <code>IProblem</code> through
	 * various API. Only the evaluation API is still producing markers for
	 * transient problems.
	 * 
	 * @see descent.core.compiler.IProblem
	 * @see descent.core.eval.ICodeSnippetRequestor#acceptProblem(org.eclipse.core.resources.IMarker,String,
	 *      int)
	 */
	String TRANSIENT_PROBLEM = JavaCore.PLUGIN_ID + ".transient_problem"; //$NON-NLS-1$

	/**
	 * Java model task marker type (value
	 * <code>"descent.core.task"</code>). This can be used to
	 * recognize task markers in the workspace that correspond to tasks
	 * specified in Java source comments and detected during compilation (for
	 * example, 'TO-DO: ...'). Tasks are identified by a task tag, which can be
	 * customized through <code>JavaCore</code> option
	 * <code>"descent.core.compiler.taskTag"</code>.
	 * 
	 * @since 2.1
	 */
	String TASK_MARKER = JavaCore.PLUGIN_ID + ".task"; //$NON-NLS-1$

	/**
	 * Id marker attribute (value <code>"arguments"</code>). Arguments are
	 * concatenated into one String, prefixed with an argument count (followed
	 * with colon separator) and separated with '#' characters. For example: {
	 * "foo", "bar" } is encoded as "2:foo#bar", { } is encoded as "0: "
	 * 
	 * @since 2.0
	 */
	String ARGUMENTS = "arguments"; //$NON-NLS-1$

	/**
	 * ID marker attribute (value <code>"id"</code>).
	 */
	String ID = "id"; //$NON-NLS-1$

	/**
	 * ID category marker attribute (value <code>"categoryId"</code>)
	 * @since 3.2
	 */
	String CATEGORY_ID = "categoryId"; //$NON-NLS-1$

	/**
	 * Flags marker attribute (value <code>"flags"</code>). Reserved for
	 * future use.
	 */
	String FLAGS = "flags"; //$NON-NLS-1$

	/**
	 * Cycle detected marker attribute (value <code>"cycleDetected"</code>).
	 * Used only on buildpath problem markers. The value of this attribute is
	 * either "true" or "false".
	 */
	String CYCLE_DETECTED = "cycleDetected"; //$NON-NLS-1$

	/**
	 * Build path problem marker type (value
	 * <code>"descent.core.buildpath_problem"</code>). This can be
	 * used to recognize those markers in the workspace that flag problems
	 * detected by the Java tooling during classpath setting.
	 */
	String BUILDPATH_PROBLEM_MARKER = JavaCore.PLUGIN_ID
			+ ".buildpath_problem"; //$NON-NLS-1$

	/**
	 * Classpath file format marker attribute (value
	 * <code>"classpathFileFormat"</code>). Used only on buildpath problem
	 * markers. The value of this attribute is either "true" or "false".
	 * 
	 * @since 2.0
	 */
	String CLASSPATH_FILE_FORMAT = "classpathFileFormat"; //$NON-NLS-1$
}
