package mmrnmhrm.ui;

import mmrnmhrm.core.dltk.DeeLanguageToolkit;
import mmrnmhrm.ui.editor.DeeEditor;
import mmrnmhrm.ui.preferences.DeeCompilersPreferencePage;
import mmrnmhrm.ui.text.DeeSimpleSourceViewerConfiguration;

import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;
import org.eclipse.dltk.ui.ScriptElementLabels;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.dltk.ui.viewsupport.ScriptUILabelProvider;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;

public class DeeUILanguageToolkit implements IDLTKUILanguageToolkit {

	private static final DeeUILanguageToolkit instance = new DeeUILanguageToolkit();
	private static final DeeScriptElementLabels elementLabels = new DeeScriptElementLabels(); 

	
	public static DeeUILanguageToolkit getDefault() {
		return instance ;
	}
	
	@Override
	public IDLTKLanguageToolkit getCoreToolkit() {
		return DeeLanguageToolkit.getDefault();
	}
	
	@Override
	public IDialogSettings getDialogSettings() {
		return DeePlugin.getInstance().getDialogSettings();
	}
	
	@Override
	public String getEditorId(Object inputElement) {
		return DeeEditor.EDITOR_ID;
	}
	
	@Override
	public IPreferenceStore getPreferenceStore() {
		return DeePlugin.getInstance().getPreferenceStore();
	}
	
	@Override
	public String getPartitioningId() {
		return DeeConstants.PARTITIONING_ID;
	}
	
	@Override
	public ScriptTextTools getTextTools() {
		return DeePlugin.getDefault().getTextTools();
	}
	
	@Override
	public ScriptSourceViewerConfiguration createSourceViwerConfiguration() {
		return new DeeSimpleSourceViewerConfiguration(getTextTools()
				.getColorManager(), getPreferenceStore(), null,
				getPartitioningId(), false);
	}
	
	@Override
	public ScriptUILabelProvider createScripUILabelProvider() {
		return new DeeScriptUILabelProvider();
	}
	
	@Override
	public ScriptElementLabels getScriptElementLabels() {
		return elementLabels; 
	}

	@Override
	public String getInterpreterPreferencePage() {
		return DeeCompilersPreferencePage.PAGE_ID;
	}

	@Override
	public String getInterpreterContainerId() {
		// TODO Provide launching support?
		return null;
	}


	@Override
	public boolean getProvideMembers(ISourceModule element) {
		return true;
	}

}
