package descent.ui.text;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

import descent.core.compiler.IProblem;
import descent.core.dom.AST;
import descent.core.dom.ASTParser;
import descent.core.dom.CompilationUnit;
import descent.ui.DescentUI;

/**
 * Reconciling strategy for a d grammar. It parses
 * everything again on every change.
 */
public class DReconcilingStrategy implements IReconcilingStrategy {
	
	private DEditor editor;
	private CompilationUnit unit;
	
	public DReconcilingStrategy(DEditor editor) {
		this.editor = editor;
	}
	
	/**
	 * Returns the compilation unit maintained by me.
	 */
	public CompilationUnit getCompilationUnit() {
		return unit;
	}

	public void reconcile(IRegion partition) {
		parse();
	}

	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		parse();
	}

	public void setDocument(IDocument document) {
		parse();
	}
	
	private void parse() {
		// Clear all errors
		IEditorInput input = editor.getEditorInput();
		IFileEditorInput fileInput = (IFileEditorInput) input;
		IFile file = fileInput.getFile();
		try {
			file.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			DescentUI.log(e);
		}
		
		try {
			ASTParser parser = ASTParser.newParser(AST.D1);
			parser.setSource(editor.getDocument().get().toCharArray());
			unit = (CompilationUnit) parser.createAST(null);
		} catch (Throwable t) {
			DescentUI.log(t);
		}
		
		// and now mark the errors
		for(IProblem problem : unit.getProblems()) {
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
				editor.updateCurrentOutlineElement();
				editor.updateOutlinePage();
			}
		});
	}

}
