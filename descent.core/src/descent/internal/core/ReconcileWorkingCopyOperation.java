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
package descent.internal.core;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SafeRunner;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.IJavaModelStatus;
import descent.core.IJavaModelStatusConstants;
import descent.core.IJavaProject;
import descent.core.IProblemRequestor;
import descent.core.JavaModelException;
import descent.core.WorkingCopyOwner;
import descent.core.compiler.CompilationParticipant;
import descent.core.compiler.IProblem;
import descent.core.compiler.ReconcileContext;
import descent.core.dom.AST;
import descent.core.dom.CompilationUnitResolver;
import descent.core.dom.CompilationUnitResolver.ParseResult;
import descent.internal.compiler.parser.Module;
import descent.internal.core.util.Messages;
import descent.internal.core.util.Util;

/**
 * Reconcile a working copy and signal the changes through a delta.
 */
public class ReconcileWorkingCopyOperation extends JavaModelOperation {
	public static boolean PERF = false;
	
	public int astLevel;
	public boolean resolveBindings;
	public HashMap problems;
	boolean forceProblemDetection;
	boolean enableStatementsRecovery;
	WorkingCopyOwner workingCopyOwner;
	public descent.core.dom.CompilationUnit ast;
	public JavaElementDeltaBuilder deltaBuilder;
	
	public ReconcileWorkingCopyOperation(IJavaElement workingCopy, int astLevel, boolean forceProblemDetection, boolean enableStatementsRecovery, WorkingCopyOwner workingCopyOwner) {
		super(new IJavaElement[] {workingCopy});
		this.astLevel = astLevel;
		this.forceProblemDetection = forceProblemDetection;
		this.enableStatementsRecovery = enableStatementsRecovery;
		this.workingCopyOwner = workingCopyOwner;
	}
	
	/**
	 * @exception JavaModelException if setting the source
	 * 	of the original compilation unit fails
	 */
	@Override
	protected void executeOperation() throws JavaModelException {
		if (this.progressMonitor != null) {
			if (this.progressMonitor.isCanceled()) 
				throw new OperationCanceledException();
			this.progressMonitor.beginTask(Messages.element_reconciling, 2); 
		}
	
		CompilationUnit workingCopy = getWorkingCopy();
		boolean wasConsistent = workingCopy.isConsistent();
		IProblemRequestor problemRequestor = workingCopy.getPerWorkingCopyInfo();
		this.resolveBindings |= problemRequestor != null && problemRequestor.isActive();
		
		// create the delta builder (this remembers the current content of the cu)
		this.deltaBuilder = new JavaElementDeltaBuilder(workingCopy);
		
		// make working copy consistent if needed and compute AST if needed
		makeConsistent(workingCopy, problemRequestor);
		
		// notify reconcile participants
		notifyParticipants(workingCopy);
		
		// recreate ast if needed
		if (this.ast == null && (this.astLevel > ICompilationUnit.NO_AST || this.resolveBindings))
			makeConsistent(workingCopy, problemRequestor);
	
		// report problems
		if (this.problems != null && (this.forceProblemDetection || !wasConsistent)) {
			try {
				problemRequestor.beginReporting();
				for (Iterator iteraror = this.problems.values().iterator(); iteraror.hasNext();) {
					IProblem[] categorizedProblems = (IProblem[]) iteraror.next();
					if (categorizedProblems == null) continue;
					for (int i = 0, length = categorizedProblems.length; i < length; i++) {
						IProblem problem = categorizedProblems[i];
						if (JavaModelManager.VERBOSE){
							System.out.println("PROBLEM FOUND while reconciling : " + problem.getMessage());//$NON-NLS-1$
						}
						if (this.progressMonitor != null && this.progressMonitor.isCanceled()) break;
						problemRequestor.acceptProblem(problem);
					}
				}
			} finally {
				problemRequestor.endReporting();
			}
		}
		
		// report delta
		try {
			JavaElementDelta delta = this.deltaBuilder.delta;
			if (delta != null) {
				addReconcileDelta(workingCopy, delta);
			}
		} finally {
			if (this.progressMonitor != null) this.progressMonitor.done();
		}
	}
	/**
	 * Returns the working copy this operation is working on.
	 */
	protected CompilationUnit getWorkingCopy() {
		return (CompilationUnit)getElementToProcess();
	}
	/**
	 * @see JavaModelOperation#isReadOnly
	 */
	@Override
	public boolean isReadOnly() {
		return true;
	}
	/*
	 * Makes the given working copy consistent, computes the delta and computes an AST if needed.
	 * Returns the AST.
	 */
	public descent.core.dom.CompilationUnit makeConsistent(CompilationUnit workingCopy, IProblemRequestor problemRequestor) throws JavaModelException {
		if (!workingCopy.isConsistent()) {
			// make working copy consistent
			if (this.problems == null) this.problems = new HashMap();
			this.ast = workingCopy.makeConsistent(this.astLevel, this.resolveBindings, this.enableStatementsRecovery, this.problems, this.progressMonitor);
			this.deltaBuilder.buildDeltas();
			if (this.ast != null && this.deltaBuilder.delta != null)
				this.deltaBuilder.delta.changedAST(this.ast);
			return this.ast;
		} 
		if (this.ast != null) return this.ast; // no need to recompute AST if known already
		if (this.forceProblemDetection || this.resolveBindings) {
			if (JavaProject.hasJavaNature(workingCopy.getJavaProject().getProject())) {
				HashMap problemMap;
				if (this.problems == null) {
					problemMap = new HashMap();
					if (this.forceProblemDetection)
						this.problems = problemMap;
				} else {
					problemMap = this.problems;
				}
				
				// TODO JDT verify this
				ParseResult parseResult = CompilationUnitResolver.parse(this.astLevel == ICompilationUnit.NO_AST ? AST.D2 : this.astLevel, workingCopy.getContents(), workingCopy.getFileName(), null, true, true, false);
				Module module = parseResult.module;
				module.moduleName = workingCopy.getFullyQualifiedName();
				
			    //CompilationUnitDeclaration unit = null;
			    try {
			    	
			    	// find problems
			    	parseResult.context = CompilationUnitResolver.resolve(
			    			module, 
			    			workingCopy.getJavaProject(),
			    			this.workingCopyOwner, parseResult.encoder, parseResult.holder);
			    	
			    	/* TODO JDT problems
					char[] contents = workingCopy.getContents();
					unit = 
						CompilationUnitProblemFinder.process(
							workingCopy, 
							contents, 
							this.workingCopyOwner, 
							problemMap, 
							this.astLevel != ICompilationUnit.NO_AST, 
							this.enableStatementsRecovery,
							this.progressMonitor);
					*/
			    	problemMap.put(new Object(), module.problems.toArray(new IProblem[module.problems.size()]));
			    	
					if (this.progressMonitor != null) this.progressMonitor.worked(1);
					
					// create AST if needed
					if (this.astLevel != ICompilationUnit.NO_AST && module != null) {
						//Map options = workingCopy.getJavaProject().getOptions(true);
						// TODO check if need to resolve bindings
						this.ast = CompilationUnitResolver.convert(AST.newAST(this.astLevel == ICompilationUnit.NO_AST ? AST.D2 : this.astLevel), parseResult, workingCopy.getJavaProject(), workingCopy, workingCopyOwner, null);
						if (this.ast != null) {
							this.deltaBuilder.delta = new JavaElementDelta(workingCopy);
							this.deltaBuilder.delta.changedAST(this.ast);
						}
						if (this.progressMonitor != null) this.progressMonitor.worked(1);
					}
			    //} catch (JavaModelException e) {
				//	if (JavaProject.hasJavaNature(workingCopy.getJavaProject().getProject()))
				//		throw e;
			    	// else JavaProject has lost its nature (or most likely was closed/deleted) while reconciling -> ignore
			    	// (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=100919)
			    } finally {
			    	/*
			        if (unit != null) {
			            unit.cleanUp();
			        }
			        */
			    }
			} // else working copy not in a Java project
			return this.ast;
		} 
		return null;
	}
	private void notifyParticipants(final CompilationUnit workingCopy) {
		IJavaProject javaProject = getWorkingCopy().getJavaProject();
		CompilationParticipant[] participants = JavaModelManager.getJavaModelManager().compilationParticipants.getCompilationParticipants(javaProject);	
		if (participants == null) return;

		final ReconcileContext context = new ReconcileContext(this, workingCopy);
		for (int i = 0, length = participants.length; i < length; i++) {
			final CompilationParticipant participant = participants[i];
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable exception) {
					if (exception instanceof Error) {
						throw (Error) exception; // errors are not supposed to be caught
					} else if (exception instanceof OperationCanceledException)
						throw (OperationCanceledException) exception;
					else if (exception instanceof UnsupportedOperationException) {
						// might want to disable participant as it tried to modify the buffer of the working copy being reconciled
						Util.log(exception, "Reconcile participant attempted to modify the buffer of the working copy being reconciled"); //$NON-NLS-1$
					} else
						Util.log(exception, "Exception occurred in reconcile participant"); //$NON-NLS-1$
				}
				public void run() throws Exception {
					participant.reconcile(context);
				}
			});
		}
	}
	@Override
	protected IJavaModelStatus verify() {
		IJavaModelStatus status = super.verify();
		if (!status.isOK()) {
			return status;
		}
		CompilationUnit workingCopy = getWorkingCopy();
		if (!workingCopy.isWorkingCopy()) {
			return new JavaModelStatus(IJavaModelStatusConstants.ELEMENT_DOES_NOT_EXIST, workingCopy); //was destroyed
		}
		return status;
	}


}
