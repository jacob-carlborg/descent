package descent.ui.text;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import descent.core.ICompilationUnit;
import descent.core.JavaModelException;
import descent.core.dom.AST;
import descent.core.dom.CompilationUnit;
import descent.internal.ui.JavaPlugin;
import descent.internal.ui.javaeditor.WorkingCopyManager;

public class JavaReconcilingStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension {
	
	private ITextEditor fEditor;
	private WorkingCopyManager fManager;
	private IDocumentProvider fDocumentProvider;
	private IProgressMonitor fProgressMonitor;
	private CompilationUnit ast;
	
	public JavaReconcilingStrategy(ITextEditor editor) {
		this.fEditor = editor;
		fManager= JavaPlugin.getDefault().getWorkingCopyManager();
		fDocumentProvider= JavaPlugin.getDefault().getCompilationUnitDocumentProvider();
	}
	
	/**
	 * Returns the compilation unit maintained by me.
	 */
	public CompilationUnit getCompilationUnit() {
		return ast;
	}

	public void reconcile(IRegion partition) {
		reconcile(false);
	}

	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		reconcile(false);
	}

	public void setDocument(IDocument document) {
		
	}
	
	/*
	private void parse() {
		// Clear all errors
		IEditorInput input = fEditor.getEditorInput();
		IFileEditorInput fileInput = (IFileEditorInput) input;
		IFile file = fileInput.getFile();
		try {
			file.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			DescentUI.log(e);
		}
		
		try {
			ASTParser parser = ASTParser.newParser(AST.D1);
			parser.setSource(fEditor.getDocumentProvider().getDocument(fEditor).get().toCharArray());
			ast = (CompilationUnit) parser.createAST(null);
		} catch (Throwable t) {
			DescentUI.log(t);
		}
		
		// and now mark the errors
		for(IProblem problem : ast.getProblems()) {
			try {
				IMarker marker = file.createMarker(IMarker.PROBLEM);
				marker.setAttribute(IMarker.MESSAGE, problem.getMessage());
				marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
				marker.setAttribute(IMarker.SEVERITY, problem.isError() ? IMarker.SEVERITY_ERROR : IMarker.SEVERITY_WARNING);
				marker.setAttribute(IMarker.CHAR_START, problem.getSourceStart());
				marker.setAttribute(IMarker.CHAR_END, problem.getSourceEnd());
			} catch (CoreException e) {
				DescentUI.log(e);
			}
		}
		
		// Update outline view
		Display.getDefault().syncExec(new Runnable() {
			public void run(){
				fEditor.updateCurrentOutlineElement();
				fEditor.updateOutlinePage();
			}
		});
	}
	 */

	public void initialReconcile() {
		reconcile(true);
	}
	
	private void reconcile(final boolean initialReconcile) {		
		final CompilationUnit[] ast= new CompilationUnit[1];
		final ICompilationUnit unit= fManager.getWorkingCopy(fEditor.getEditorInput(), false);
		if (unit != null) {
			SafeRunner.run(new ISafeRunnable() {
				public void run() {
					try {
						// TODO JDT copy from JavaReconcilingStrategy
						ast[0] = unit.reconcile(AST.D1, true, true, null, fProgressMonitor);							
					} catch (JavaModelException ex) {
						handleException(ex);
					}
				}
				public void handleException(Throwable ex) {
					//IStatus status= new Status(IStatus.ERROR, DescentUI.PLUGIN_ID, IStatus.OK, "Error in JDT Core during reconcile", ex);  //$NON-NLS-1$
					//JavaPlugin.getDefault().getLog().log(status);
				}
			});
		}
		this.ast = ast[0];
	}

	public void setProgressMonitor(IProgressMonitor monitor) {
		fProgressMonitor= monitor;
	}

}
