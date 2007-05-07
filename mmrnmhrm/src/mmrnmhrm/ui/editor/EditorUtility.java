package mmrnmhrm.ui.editor;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

public class EditorUtility {

	public static IProject getProject(IEditorInput input) {
		IProject project = null;
		if (input instanceof IFileEditorInput) {
			project = ((IFileEditorInput)input).getFile().getProject();
		} 
		return project;
	}

}
