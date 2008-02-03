package descent.internal.ui.navigator;

import org.eclipse.core.resources.IFile;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.navigator.ILinkHelper;

import descent.core.IJavaElement;
import descent.core.JavaCore;

import descent.internal.ui.javaeditor.EditorUtility;
import descent.internal.ui.javaeditor.IClassFileEditorInput;

public class JavaFileLinkHelper implements ILinkHelper {

	public void activateEditor(IWorkbenchPage page, IStructuredSelection selection) {
		if (selection == null || selection.isEmpty())
			return;
		Object element= selection.getFirstElement();
		IEditorPart part= EditorUtility.isOpenInEditor(element);
		if (part != null) {
			page.bringToTop(part);
			if (element instanceof IJavaElement)
				EditorUtility.revealInEditor(part, (IJavaElement) element);
		}

	}

	public IStructuredSelection findSelection(IEditorInput input) {
		Object javaElement= null;
		if (input instanceof IClassFileEditorInput)
			javaElement= ((IClassFileEditorInput) input).getClassFile();
		else if (input instanceof IFileEditorInput) {
			IFile file= ((IFileEditorInput) input).getFile();
			javaElement= JavaCore.create(file);
		}
//		} else if (input instanceof JarEntryEditorInput)
//			javaElement= ((JarEntryEditorInput) input).getStorage();

		return (javaElement != null) ? new StructuredSelection(javaElement) : StructuredSelection.EMPTY;
	}

}
