package mmrnmhrm.ui.preferences.pages;

import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.editor.templates.RubyTemplateAccess;
import mmrnmhrm.ui.text.DeePartitions;
import mmrnmhrm.ui.text.DeeSimpleSourceViewerConfiguration;

import org.eclipse.dltk.ui.templates.ScriptTemplatePreferencePage;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;

public class DeeTemplatePreferencePage extends ScriptTemplatePreferencePage {
	
	public DeeTemplatePreferencePage() {
		setPreferenceStore(DeePlugin.getDefault().getPreferenceStore());

		setTemplateStore(RubyTemplateAccess.getTemplateStore());
		setContextTypeRegistry(RubyTemplateAccess.getContextTypeRegistry());
	}

	@Override
	protected ScriptSourceViewerConfiguration createSourceViewerConfiguration(
			IDocument document) {
		IPreferenceStore store = DeePlugin.getDefault().getPreferenceStore();

		ScriptTextTools textTools = DeePlugin.getDefault().getTextTools();
		textTools.setupDocumentPartitioner(document,
				DeePartitions.DEE_PARTITIONING);

		return new DeeSimpleSourceViewerConfiguration(textTools
				.getColorManager(), store, null,
				DeePartitions.DEE_PARTITIONING, false);

	}
}
