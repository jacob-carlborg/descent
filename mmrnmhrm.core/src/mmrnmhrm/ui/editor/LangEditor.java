package mmrnmhrm.ui.editor;

import java.util.ArrayList;
import java.util.List;

import melnorme.lang.ui.EditorUtil;
import melnorme.lang.ui.LangPlugin;
import mmrnmhrm.ui.ActualPlugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;

public abstract class LangEditor extends AbstractDecoratedTextEditor {


	protected IPreferenceStore createCombinedPreferenceStore(IEditorInput input) {
		List<IPreferenceStore> stores= new ArrayList<IPreferenceStore>(2);
	
		//add project scope
		IProject project = EditorUtil.getProject(input);
		if (project != null) {
			stores.add(new ScopedPreferenceStore(
			new ProjectScope(project.getProject()), ActualPlugin.PLUGIN_ID));
		}
	
		stores.add(LangPlugin.getInstance().getPreferenceStore());
		stores.add(EditorsUI.getPreferenceStore());
		//stores.toArray(a)
		return new ChainedPreferenceStore(stores.toArray(new IPreferenceStore[stores.size()]));
	}
	
	protected void doSetInput(IEditorInput input) throws CoreException {
		super.doSetInput(input);
		setPreferenceStore(createCombinedPreferenceStore(input));
	}

	public TextSelection getSelection() {
		return (TextSelection) getSelectionProvider().getSelection();
	}

}