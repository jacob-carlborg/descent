package descent.internal.ui.actions;

import java.util.ResourceBundle;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

import descent.core.IJavaElement;
import descent.core.ctfe.IDescentLaunchConfigurationConstants;
import descent.internal.ui.javaeditor.EditorUtility;
import descent.internal.ui.javaeditor.JavaEditor;

public class DebugCtfeAction extends TextEditorAction {

	public DebugCtfeAction(ResourceBundle bundle, String prefix,
			ITextEditor editor) {
		super(bundle, prefix, editor);
	}
	
	/*
	 * @see descent.internal.ui.actions.AddBlockCommentAction#validSelection(org.eclipse.jface.text.ITextSelection)
	 */
	protected boolean isValidSelection(ITextSelection selection) {
		return selection != null;
	}
	
	@Override
	public void run() {
		ITextEditor editor= getTextEditor();
		if (!(editor instanceof JavaEditor))
			return;
		
		JavaEditor jedit = (JavaEditor) editor;
		IJavaElement inputElement = EditorUtility.getEditorInputJavaElement(jedit, false);
		
		ISelection selection = editor.getSelectionProvider().getSelection();
		if (!(selection instanceof ITextSelection))
			return;
		
		debugAtCompileTime(inputElement, (ITextSelection) selection);
	}

	private void debugAtCompileTime(IJavaElement inputElement, ITextSelection selection) {
		ILaunchConfiguration config = createConfiguration(inputElement, selection);
		DebugUITools.launch(config, "debug");
	}
	
	protected ILaunchConfiguration createConfiguration(IJavaElement inputElement, ITextSelection selection) {
		ILaunchConfiguration config = null;
		ILaunchConfigurationWorkingCopy wc = null;
		try {
			String inputHandle = inputElement.getHandleIdentifier();
			
			ILaunchConfigurationType configType = getConfigurationType();
			wc = configType.newInstance(null, getLaunchManager().generateUniqueLaunchConfigurationNameFrom(inputElement.getResource().getName()));
			wc.setAttribute(IDescentLaunchConfigurationConstants.ATTR_PROJECT_NAME, inputElement.getResource().getProject().getName());
			wc.setAttribute(IDescentLaunchConfigurationConstants.ATTR_INPUT_ELEMENT_HANDLE_IDENTIFIER, inputHandle);
			wc.setAttribute(IDescentLaunchConfigurationConstants.ATTR_INPUT_ELEMENT_SOURCE_OFFSET, selection.getOffset());
			wc.setMappedResources(new IResource[] {inputElement.getResource().getProject()});
			config = wc.doSave();
		} catch (CoreException exception) {
			exception.printStackTrace();
		}
		return config;
	}
	
	protected ILaunchConfigurationType getConfigurationType() {
		return getLaunchManager().getLaunchConfigurationType(IDescentLaunchConfigurationConstants.ID_D_APPLICATION);		
	}
	
	protected ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();
	}

}
