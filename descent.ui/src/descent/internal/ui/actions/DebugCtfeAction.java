package descent.internal.ui.actions;

import java.lang.reflect.InvocationTargetException;
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
import descent.core.ISourceReference;
import descent.core.ctfe.IDescentCtfeLaunchConfigurationConstants;
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
		
		try {
			IJavaElement[] elements= SelectionConverter.codeResolveForked(jedit, false);
			if (elements.length != 1)
				return;
			
			ISelection selection = editor.getSelectionProvider().getSelection();
			if (!(selection instanceof ITextSelection))
				return;
			
			IJavaElement elem = elements[0];
			debugAtCompileTime(elem, (ITextSelection) selection);
			
			//DebugUITools.launch(configuration, mode)
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// ignore
		}
	}

	private void debugAtCompileTime(IJavaElement elem, ITextSelection selection) {
		ILaunchConfiguration config = createConfiguration(elem, selection);
		DebugUITools.launch(config, "debug");
	}
	
	protected ILaunchConfiguration createConfiguration(IJavaElement elem, ITextSelection selection) {
		ILaunchConfiguration config = null;
		ILaunchConfigurationWorkingCopy wc = null;
		try {
			ILaunchConfigurationType configType = getConfigurationType();
			wc = configType.newInstance(null, getLaunchManager().generateUniqueLaunchConfigurationNameFrom(elem.getResource().getName()));
			wc.setAttribute(IDescentCtfeLaunchConfigurationConstants.ATTR_PROGRAM_NAME, elem.getElementName());
			wc.setAttribute(IDescentCtfeLaunchConfigurationConstants.ATTR_PROJECT_NAME, elem.getResource().getProject().getName());
			wc.setAttribute(IDescentCtfeLaunchConfigurationConstants.ATTR_SOURCE_OFFSET, selection.getOffset());
			wc.setMappedResources(new IResource[] {elem.getResource().getProject()});
			config = wc.doSave();
		} catch (CoreException exception) {
			exception.printStackTrace();
		}
		return config;
	}
	
	protected ILaunchConfigurationType getConfigurationType() {
		return getLaunchManager().getLaunchConfigurationType(IDescentCtfeLaunchConfigurationConstants.ID_D_APPLICATION);		
	}
	
	protected ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();
	}

}
