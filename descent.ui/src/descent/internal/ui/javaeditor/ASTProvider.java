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

package descent.internal.ui.javaeditor;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;

import org.eclipse.jface.text.Assert;

import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import descent.core.IClassFile;
import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.ISourceReference;
import descent.core.JavaModelException;
import descent.core.dom.AST;
import descent.core.dom.ASTNode;
import descent.core.dom.ASTParser;
import descent.core.dom.CompilationUnit;

import descent.internal.corext.dom.ASTNodes;

import descent.ui.JavaUI;

import descent.internal.ui.JavaPlugin;


/**
 * Provides a shared AST for clients. The shared AST is
 * the AST of the active Java editor's input element.
 *
 * @since 3.0
 */
public final class ASTProvider {

	/**
	 * Wait flag.
	 *
	 * @since 3.1
	 */
	public static final class WAIT_FLAG {

		String fName;

		private WAIT_FLAG(String name) {
			fName= name;
		}

		/*
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return fName;
		}
	}

	/**
	 * Wait flag indicating that a client requesting an AST
	 * wants to wait until an AST is ready.
	 * <p>
	 * An AST will be created by this AST provider if the shared
	 * AST is not for the given java element.
	 * </p>
	 *
	 * @since 3.1
	 */
	public static final WAIT_FLAG WAIT_YES= new WAIT_FLAG("wait yes"); //$NON-NLS-1$

	/**
	 * Wait flag indicating that a client requesting an AST
	 * only wants to wait for the shared AST of the active editor.
	 * <p>
	 * No AST will be created by the AST provider.
	 * </p>
	 *
	 * @since 3.1
	 */
	public static final WAIT_FLAG WAIT_ACTIVE_ONLY= new WAIT_FLAG("wait active only"); //$NON-NLS-1$

	/**
	 * Wait flag indicating that a client requesting an AST
	 * only wants the already available shared AST.
	 * <p>
	 * No AST will be created by the AST provider.
	 * </p>
	 *
	 * @since 3.1
	 */
	public static final WAIT_FLAG WAIT_NO= new WAIT_FLAG("don't wait"); //$NON-NLS-1$


	/**
	 * Tells whether this class is in debug mode.
	 * @since 3.0
	 */
	private static final boolean DEBUG= "true".equalsIgnoreCase(Platform.getDebugOption("descent.ui/debug/ASTProvider"));  //$NON-NLS-1$//$NON-NLS-2$


	/**
	 * Internal activation listener.
	 *
	 * @since 3.0
	 */
	private class ActivationListener implements IPartListener2, IWindowListener {


		/*
		 * @see org.eclipse.ui.IPartListener2#partActivated(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partActivated(IWorkbenchPartReference ref) {
			if (isJavaEditor(ref) && !isActiveEditor(ref))
				activeJavaEditorChanged(ref.getPart(true));
		}

		/*
		 * @see org.eclipse.ui.IPartListener2#partBroughtToTop(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partBroughtToTop(IWorkbenchPartReference ref) {
			if (isJavaEditor(ref) && !isActiveEditor(ref))
				activeJavaEditorChanged(ref.getPart(true));
		}

		/*
		 * @see org.eclipse.ui.IPartListener2#partClosed(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partClosed(IWorkbenchPartReference ref) {
			if (isActiveEditor(ref)) {
				if (DEBUG)
					System.out.println(getThreadName() + " - " + DEBUG_PREFIX + "closed active editor: " + ref.getTitle()); //$NON-NLS-1$ //$NON-NLS-2$

				activeJavaEditorChanged(null);
			}
		}

		/*
		 * @see org.eclipse.ui.IPartListener2#partDeactivated(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partDeactivated(IWorkbenchPartReference ref) {
		}

		/*
		 * @see org.eclipse.ui.IPartListener2#partOpened(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partOpened(IWorkbenchPartReference ref) {
			if (isJavaEditor(ref) && !isActiveEditor(ref))
				activeJavaEditorChanged(ref.getPart(true));
		}

		/*
		 * @see org.eclipse.ui.IPartListener2#partHidden(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partHidden(IWorkbenchPartReference ref) {
		}

		/*
		 * @see org.eclipse.ui.IPartListener2#partVisible(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partVisible(IWorkbenchPartReference ref) {
			if (isJavaEditor(ref) && !isActiveEditor(ref))
				activeJavaEditorChanged(ref.getPart(true));
		}

		/*
		 * @see org.eclipse.ui.IPartListener2#partInputChanged(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partInputChanged(IWorkbenchPartReference ref) {
			if (isJavaEditor(ref) && isActiveEditor(ref))
				activeJavaEditorChanged(ref.getPart(true));
		}

		/*
		 * @see org.eclipse.ui.IWindowListener#windowActivated(org.eclipse.ui.IWorkbenchWindow)
		 */
		public void windowActivated(IWorkbenchWindow window) {
			IWorkbenchPartReference ref= window.getPartService().getActivePartReference();
			if (isJavaEditor(ref) && !isActiveEditor(ref))
				activeJavaEditorChanged(ref.getPart(true));
		}

		/*
		 * @see org.eclipse.ui.IWindowListener#windowDeactivated(org.eclipse.ui.IWorkbenchWindow)
		 */
		public void windowDeactivated(IWorkbenchWindow window) {
		}

		/*
		 * @see org.eclipse.ui.IWindowListener#windowClosed(org.eclipse.ui.IWorkbenchWindow)
		 */
		public void windowClosed(IWorkbenchWindow window) {
			if (fActiveEditor != null && fActiveEditor.getSite() != null && window == fActiveEditor.getSite().getWorkbenchWindow()) {
				if (DEBUG)
					System.out.println(getThreadName() + " - " + DEBUG_PREFIX + "closed active editor: " + fActiveEditor.getTitle()); //$NON-NLS-1$ //$NON-NLS-2$

				activeJavaEditorChanged(null);
			}
			window.getPartService().removePartListener(this);
		}

		/*
		 * @see org.eclipse.ui.IWindowListener#windowOpened(org.eclipse.ui.IWorkbenchWindow)
		 */
		public void windowOpened(IWorkbenchWindow window) {
			window.getPartService().addPartListener(this);
		}

		private boolean isActiveEditor(IWorkbenchPartReference ref) {
			return ref != null && isActiveEditor(ref.getPart(false));
		}

		private boolean isActiveEditor(IWorkbenchPart part) {
			return part != null && (part == fActiveEditor);
		}

		private boolean isJavaEditor(IWorkbenchPartReference ref) {
			if (ref == null)
				return false;

			String id= ref.getId();

			// The instanceof check is not need but helps clients, see https://bugs.eclipse.org/bugs/show_bug.cgi?id=84862
			return JavaUI.ID_CF_EDITOR.equals(id) || JavaUI.ID_CU_EDITOR.equals(id) || ref.getPart(false) instanceof JavaEditor;
		}
	}

	public static final int SHARED_AST_LEVEL= AST.D2;
	public static final boolean SHARED_AST_STATEMENT_RECOVERY= true;

	private static final String DEBUG_PREFIX= "ASTProvider > "; //$NON-NLS-1$


	private IJavaElement fReconcilingJavaElement;
	private IJavaElement fActiveJavaElement;
	private CompilationUnit fAST;
	private ActivationListener fActivationListener;
	private Object fReconcileLock= new Object();
	private Object fWaitLock= new Object();
	private boolean fIsReconciling;
	private IWorkbenchPart fActiveEditor;

	
	/**
	 * Returns the Java plug-in's AST provider.
	 * 
	 * @return the AST provider
	 * @since 3.2
	 */
	public static ASTProvider getASTProvider() {
		return JavaPlugin.getDefault().getASTProvider();
	}
	
	/**
	 * Creates a new AST provider.
	 */
	public ASTProvider() {
		install();
	}

	/**
	 * Installs this AST provider.
	 */
	void install() {
		// Create and register activation listener
		fActivationListener= new ActivationListener();
		PlatformUI.getWorkbench().addWindowListener(fActivationListener);

		// Ensure existing windows get connected
		IWorkbenchWindow[] windows= PlatformUI.getWorkbench().getWorkbenchWindows();
		for (int i= 0, length= windows.length; i < length; i++)
			windows[i].getPartService().addPartListener(fActivationListener);
	}

	private void activeJavaEditorChanged(IWorkbenchPart editor) {

		IJavaElement javaElement= null;
		if (editor instanceof JavaEditor)
			javaElement= ((JavaEditor)editor).getInputJavaElement();

		synchronized (this) {
			fActiveEditor= editor;
			fActiveJavaElement= javaElement;
			cache(null, javaElement);
		}

		if (DEBUG)
			System.out.println(getThreadName() + " - " + DEBUG_PREFIX + "active editor is: " + toString(javaElement)); //$NON-NLS-1$ //$NON-NLS-2$

		synchronized (fReconcileLock) {
			if (fIsReconciling && (fReconcilingJavaElement == null || !fReconcilingJavaElement.equals(javaElement))) {
				fIsReconciling= false;
				fReconcilingJavaElement= null;
			} else if (javaElement == null) {
				fIsReconciling= false;
				fReconcilingJavaElement= null;
			}
		}
	}

	/**
	 * Returns whether the given compilation unit AST is
	 * cached by this AST provided.
	 *
	 * @param ast the compilation unit AST
	 * @return <code>true</code> if the given AST is the cached one
	 */
	public boolean isCached(CompilationUnit ast) {
		return ast != null && fAST == ast;
	}

	/**
	 * Returns whether this AST provider is active on the given
	 * compilation unit.
	 *
	 * @param cu the compilation unit
	 * @return <code>true</code> if the given compilation unit is the active one
	 * @since 3.1
	 */
	public boolean isActive(ICompilationUnit cu) {
		return cu != null && cu.equals(fActiveJavaElement);
	}

	/**
	 * Informs that reconciling for the given element is about to be started.
	 *
	 * @param javaElement the Java element
	 * @see descent.internal.ui.text.java.IJavaReconcilingListener#aboutToBeReconciled()
	 */
	void aboutToBeReconciled(IJavaElement javaElement) {

		if (javaElement == null)
			return;

		if (DEBUG)
			System.out.println(getThreadName() + " - " + DEBUG_PREFIX + "about to reconcile: " + toString(javaElement)); //$NON-NLS-1$ //$NON-NLS-2$

		synchronized (fReconcileLock) {
			fIsReconciling= true;
			fReconcilingJavaElement= javaElement;
		}
		cache(null, javaElement);
	}

	/**
	 * Disposes the cached AST.
	 */
	private synchronized void disposeAST() {

		if (fAST == null)
			return;

		if (DEBUG)
			System.out.println(getThreadName() + " - " + DEBUG_PREFIX + "disposing AST: " + toString(fAST) + " for: " + toString(fActiveJavaElement)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		fAST= null;

		cache(null, null);
	}

	/**
	 * Returns a string for the given Java element used for debugging.
	 *
	 * @param javaElement the compilation unit AST
	 * @return a string used for debugging
	 */
	private String toString(IJavaElement javaElement) {
		if (javaElement == null)
			return "null"; //$NON-NLS-1$
		else
			return javaElement.getElementName();

	}

	/**
	 * Returns a string for the given AST used for debugging.
	 *
	 * @param ast the compilation unit AST
	 * @return a string used for debugging
	 */
	private String toString(CompilationUnit ast) {
		if (ast == null)
			return "null"; //$NON-NLS-1$
		
		return ast.toString();
	}

	/**
	 * Caches the given compilation unit AST for the given Java element.
	 *
	 * @param ast
	 * @param javaElement
	 */
	private synchronized void cache(CompilationUnit ast, IJavaElement javaElement) {

		if (fActiveJavaElement != null && !fActiveJavaElement.equals(javaElement)) {
			if (DEBUG && javaElement != null) // don't report call from disposeAST()
				System.out.println(getThreadName() + " - " + DEBUG_PREFIX + "don't cache AST for inactive: " + toString(javaElement)); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}

		if (DEBUG && (javaElement != null || ast != null)) // don't report call from disposeAST()
			System.out.println(getThreadName() + " - " + DEBUG_PREFIX + "caching AST: " + toString(ast) + " for: " + toString(javaElement)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		if (fAST != null)
			disposeAST();

		fAST= ast;

		// Signal AST change
		synchronized (fWaitLock) {
			fWaitLock.notifyAll();
		}
	}

	/**
	 * Returns a shared compilation unit AST for the given
	 * Java element.
	 * <p>
	 * Clients are not allowed to modify the AST and must
	 * synchronize all access to its nodes.
	 * </p>
	 *
	 * @param je				the Java element
	 * @param waitFlag			{@link #WAIT_YES}, {@link #WAIT_NO} or {@link #WAIT_ACTIVE_ONLY}
	 * @param progressMonitor	the progress monitor or <code>null</code>
	 * @return					the AST or <code>null</code> if the AST is not available
	 */
	public CompilationUnit getAST(IJavaElement je, WAIT_FLAG waitFlag, IProgressMonitor progressMonitor) {
		if (je == null)
			return null;
		
		Assert.isTrue(je.getElementType() == IJavaElement.CLASS_FILE || je.getElementType() == IJavaElement.COMPILATION_UNIT);

		if (progressMonitor != null && progressMonitor.isCanceled())
			return null;

		boolean isActiveElement;
		synchronized (this) {
			isActiveElement= je.equals(fActiveJavaElement);
			if (isActiveElement) {
				if (fAST != null) {
					if (DEBUG)
						System.out.println(getThreadName() + " - " + DEBUG_PREFIX + "returning cached AST:" + toString(fAST) + " for: " + je.getElementName()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

					return fAST;
				}
				if (waitFlag == WAIT_NO) {
					if (DEBUG)
						System.out.println(getThreadName() + " - " + DEBUG_PREFIX + "returning null (WAIT_NO) for: " + je.getElementName()); //$NON-NLS-1$ //$NON-NLS-2$

					return null;

				}
			}
		}
		if (isActiveElement && isReconciling(je)) {
			try {
				final IJavaElement activeElement= fReconcilingJavaElement;

				// Wait for AST
				synchronized (fWaitLock) {
					if (DEBUG)
						System.out.println(getThreadName() + " - " + DEBUG_PREFIX + "waiting for AST for: " + je.getElementName()); //$NON-NLS-1$ //$NON-NLS-2$

					fWaitLock.wait();
				}

				// Check whether active element is still valid
				synchronized (this) {
					if (activeElement == fActiveJavaElement && fAST != null) {
						if (DEBUG)
							System.out.println(getThreadName() + " - " + DEBUG_PREFIX + "...got AST for: " + je.getElementName()); //$NON-NLS-1$ //$NON-NLS-2$

						return fAST;
					}
				}
				return getAST(je, waitFlag, progressMonitor);
			} catch (InterruptedException e) {
				return null; // thread has been interrupted don't compute AST
			}
		} else if (waitFlag == WAIT_NO || (waitFlag == WAIT_ACTIVE_ONLY && !(isActiveElement && fAST == null)))
			return null;

		if (isActiveElement)
			aboutToBeReconciled(je);

		CompilationUnit ast= null;
		try {
			ast= createAST(je, progressMonitor);
			if (progressMonitor != null && progressMonitor.isCanceled())
				ast= null;
			else if (DEBUG && ast != null)
				System.err.println(getThreadName() + " - " + DEBUG_PREFIX + "created AST for: " + je.getElementName()); //$NON-NLS-1$ //$NON-NLS-2$
		} finally {
			if (isActiveElement) {
				if (fAST != null) {
					if (DEBUG)
						System.out.println(getThreadName() + " - " + DEBUG_PREFIX + "Ignore created AST for " + je.getElementName() + "- AST from reconciler is newer"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					reconciled(fAST, je, null);
				} else
					reconciled(ast, je, null);
			}
		}

		return ast;
	}

	/**
	 * Returns a shared compilation unit AST for the given
	 * Java element.
	 * <p>
	 * Clients are not allowed to modify the AST and must
	 * synchronize all access to its nodes.
	 * </p>
	 *
	 * @param je				the Java element
	 * @param wait				<code>true</code> if the client wants to wait for the result,
	 * 								<code>null</code> will be returned if the AST is not ready and
	 * 								the client does not want to wait
	 * @param progressMonitor	the progress monitor or <code>null</code>
	 * @return					the AST or <code>null</code> if the AST is not available
	 * @deprecated As of 3.1, use {@link #getAST(IJavaElement, WAIT_FLAG, IProgressMonitor)}
	 */
	public CompilationUnit getAST(IJavaElement je, boolean wait, IProgressMonitor progressMonitor) {
		if (wait)
			return getAST(je, WAIT_YES, progressMonitor);
		else
			return getAST(je, WAIT_NO, progressMonitor);
	}

	/**
	 * Tells whether the given Java element is the one
	 * reported as currently being reconciled.
	 *
	 * @param javaElement the Java element
	 * @return <code>true</code> if reported as currently being reconciled
	 */
	private boolean isReconciling(IJavaElement javaElement) {
		synchronized (fReconcileLock) {
			return javaElement != null && javaElement.equals(fReconcilingJavaElement) && fIsReconciling;
		}
	}

	/**
	 * Creates a new compilation unit AST.
	 *
	 * @param je the Java element for which to create the AST
	 * @param progressMonitor the progress monitor
	 * @return AST
	 */
	private CompilationUnit createAST(IJavaElement je, final IProgressMonitor progressMonitor) {
		if (!hasSource(je))
			return null;
		
		if (progressMonitor != null && progressMonitor.isCanceled())
			return null;
		
		final ASTParser parser = ASTParser.newParser(SHARED_AST_LEVEL);
		parser.setResolveBindings(true);
		parser.setStatementsRecovery(SHARED_AST_STATEMENT_RECOVERY);

		if (progressMonitor != null && progressMonitor.isCanceled())
			return null;
		
		if (je.getElementType() == IJavaElement.COMPILATION_UNIT)
			parser.setSource((ICompilationUnit)je);
		else if (je.getElementType() == IJavaElement.CLASS_FILE)
			parser.setSource((IClassFile)je);

		if (progressMonitor != null && progressMonitor.isCanceled())
			return null;

		final CompilationUnit root[]= new CompilationUnit[1]; 
		
		SafeRunner.run(new ISafeRunnable() {
			public void run() {
				try {
					if (progressMonitor != null && progressMonitor.isCanceled())
						root[0]= null;
					root[0]= (CompilationUnit)parser.createAST(progressMonitor);
				} catch (OperationCanceledException ex) {
					root[0]= null;
				}
			}
			public void handleException(Throwable ex) {
				IStatus status= new Status(IStatus.ERROR, JavaUI.ID_PLUGIN, IStatus.OK, "Error in JDT Core during AST creation", ex);  //$NON-NLS-1$
				JavaPlugin.getDefault().getLog().log(status);
			}
		});
		
		// mark as unmodifiable
		if (root[0] != null)
			ASTNodes.setFlagsToAST(root[0], ASTNode.PROTECT);
		
		return root[0];
	}
	
	/**
	 * Checks whether the given Java element has accessible source.
	 * 
	 * @param je the Java element to test
	 * @return <code>true</code> if the element has source
	 * @since 3.2
	 */
	private boolean hasSource(IJavaElement je) {
		if (je == null || !je.exists())
			return false;
		
		try {
			return je instanceof ISourceReference && ((ISourceReference)je).getSource() != null;
		} catch (JavaModelException ex) {
			IStatus status= new Status(IStatus.ERROR, JavaUI.ID_PLUGIN, IStatus.OK, "Error in JDT Core during AST creation", ex);  //$NON-NLS-1$
			JavaPlugin.getDefault().getLog().log(status);
		}
		return false;
	}
	
	/**
	 * Disposes this AST provider.
	 */
	public void dispose() {

		// Dispose activation listener
		PlatformUI.getWorkbench().removeWindowListener(fActivationListener);
		fActivationListener= null;

		disposeAST();

		synchronized (fWaitLock) {
			fWaitLock.notifyAll();
		}
	}

	/*
	 * @see descent.internal.ui.text.java.IJavaReconcilingListener#reconciled(descent.core.dom.CompilationUnit)
	 */
	void reconciled(CompilationUnit ast, IJavaElement javaElement, IProgressMonitor progressMonitor) {

		if (DEBUG)
			System.out.println(getThreadName() + " - " + DEBUG_PREFIX + "reconciled: " + toString(javaElement) + ", AST: " + toString(ast)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		synchronized (fReconcileLock) {

			fIsReconciling= progressMonitor != null && progressMonitor.isCanceled();
			if (javaElement == null || !javaElement.equals(fReconcilingJavaElement)) {

				if (DEBUG)
					System.out.println(getThreadName() + " - " + DEBUG_PREFIX + "  ignoring AST of out-dated editor"); //$NON-NLS-1$ //$NON-NLS-2$

				// Signal - threads might wait for wrong element
				synchronized (fWaitLock) {
					fWaitLock.notifyAll();
				}

				return;
			}

			cache(ast, javaElement);
		}
	}

	private String getThreadName() {
		String name= Thread.currentThread().getName();
		if (name != null)
			return name;
		else
			return Thread.currentThread().toString();
	}
	
}

