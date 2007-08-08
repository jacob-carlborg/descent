package mmrnmhrm.ui.editor.text;

import melnorme.lang.ui.LangPlugin;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;

public class LangSourceViewerConfiguration extends TextSourceViewerConfiguration {


	public LangSourceViewerConfiguration(IPreferenceStore preferenceStore) {
		super(preferenceStore);
	}

	/** Returns the settings for the given section. */
	protected IDialogSettings getSettings(String sectionName) {
		IDialogSettings settings= LangPlugin.getInstance().getDialogSettings().getSection(sectionName);
		if (settings == null)
			settings= LangPlugin.getInstance().getDialogSettings().addNewSection(sectionName);
	
		return settings;
	}
	
	public IInformationControlCreator getInformationControlCreator(ISourceViewer sourceViewer) {
		return new IInformationControlCreator() {
			@SuppressWarnings("restriction")
			public IInformationControl createInformationControl(Shell parent) {
				return new DefaultInformationControl(parent, SWT.NONE, 
						new org.eclipse.jface.internal.text.html.HTMLTextPresenter(true));
			}
		};
	}

}