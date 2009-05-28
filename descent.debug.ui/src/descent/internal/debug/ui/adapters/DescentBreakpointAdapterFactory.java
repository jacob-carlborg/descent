package descent.internal.debug.ui.adapters;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import descent.core.JavaCore;
import descent.internal.debug.ui.model.DescentLineBreakpointAdapter;
import descent.internal.ui.javaeditor.IClassFileEditorInput;

public class DescentBreakpointAdapterFactory implements IAdapterFactory {

	private static Class[] PROPERTIES = { IToggleBreakpointsTarget.class, };

	public Object getAdapter(Object value, Class key) {
		if (value instanceof ITextEditor) {
			ITextEditor editorPart = (ITextEditor) value;
			IEditorInput input = editorPart.getEditorInput();
			IResource resource = (IResource) input
					.getAdapter(IResource.class);
			if (resource != null && JavaCore.isJavaLikeFileName(resource.getName())) {
				return new DescentLineBreakpointAdapter();
			}
			if (input instanceof IClassFileEditorInput) {
				return new DescentLineBreakpointAdapter();
			}
		}
		return null;
	}

	public Class[] getAdapterList() {
		return PROPERTIES;
	}

}
