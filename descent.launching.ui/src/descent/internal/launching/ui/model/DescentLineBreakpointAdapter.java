package descent.internal.launching.ui.model;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import descent.core.JavaCore;
import descent.launching.IDescentLaunchConfigurationConstants;
import descent.launching.model.DescentLineBreakpoint;

public class DescentLineBreakpointAdapter implements IToggleBreakpointsTarget {

	public void toggleLineBreakpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
		ITextEditor textEditor = getEditor(part);
		if (textEditor != null && selection instanceof ITextSelection) {
			IResource resource = (IResource) textEditor.getEditorInput().getAdapter(IResource.class);
			ITextSelection textSelection = (ITextSelection) selection;
			int lineNumber = textSelection.getStartLine();
			IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(IDescentLaunchConfigurationConstants.ID_D_DEBUG_MODEL);
			for (int i = 0; i < breakpoints.length; i++) {
				IBreakpoint breakpoint = breakpoints[i];
				if (resource.equals(breakpoint.getMarker().getResource())) {
					if (((ILineBreakpoint)breakpoint).getLineNumber() == (lineNumber + 1)) {
						// remove
						breakpoint.delete();
						return;
					}
				}
			}
			
			IEditorInput editorInput = textEditor.getEditorInput();
            IDocumentProvider documentProvider = textEditor.getDocumentProvider();
            if (documentProvider == null) {
                return;
            }
            IDocument document = documentProvider.getDocument(editorInput);
            int lines = document.getNumberOfLines();
            int charStart = -1;
            int charEnd = -1;
            try {
				charStart = document.getLineOffset(lineNumber);
				if (lineNumber == lines - 1) {
					charEnd = document.getLength() - 1; 
				} else {
					charEnd = document.getLineOffset(lineNumber + 1);
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			
			// create line breakpoint (doc line numbers start at 0)
			DescentLineBreakpoint lineBreakpoint = new DescentLineBreakpoint(resource, lineNumber + 1, charStart, charEnd);
			DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(lineBreakpoint);
		}
	}
	
	/**
	 * Returns the editor being used to edit a D file, associated with the
	 * given part, or <code>null</code> if none.
	 *  
	 * @param part workbench part
	 * @return the editor being used to edit a PDA file, associated with the
	 * given part, or <code>null</code> if none
	 */
	private ITextEditor getEditor(IWorkbenchPart part) {
		if (part instanceof ITextEditor) {
			ITextEditor editorPart = (ITextEditor) part;
			IResource resource = (IResource) editorPart.getEditorInput().getAdapter(IResource.class);
			if (resource != null && JavaCore.isJavaLikeFileName(resource.getName())) {
				return editorPart;
			}
		}
		return null;		
	}

	public void toggleMethodBreakpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
	}

	public void toggleWatchpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
	}
	
	public boolean canToggleLineBreakpoints(IWorkbenchPart part, ISelection selection) {
		return true;
	}

	public boolean canToggleMethodBreakpoints(IWorkbenchPart part, ISelection selection) {
		return false;
	}

	public boolean canToggleWatchpoints(IWorkbenchPart part, ISelection selection) {
		return false;
	}

}
