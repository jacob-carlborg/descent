package mmrnmhrm.ui.preferences;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import mmrnmhrm.org.eclipse.dltk.ui.preferences.AbstractScriptEditorColoringConfigurationBlock;
import mmrnmhrm.ui.text.DeeDocumentSetupParticipant;
import mmrnmhrm.ui.text.DeePartitions;
import mmrnmhrm.ui.text.DeeSimpleSourceViewerConfiguration;
import mmrnmhrm.ui.text.color.IDeeColorConstants;

import org.eclipse.dltk.internal.ui.editor.ScriptSourceViewer;
import org.eclipse.dltk.ui.preferences.IPreferenceConfigurationBlock;
import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.texteditor.ITextEditor;

public class DeeSourceColoringConfigurationBlock extends
		AbstractScriptEditorColoringConfigurationBlock implements
		IPreferenceConfigurationBlock {
	
	private static final String PREVIEW_FILE_NAME = "coloringpreview.txt";

	private static final String[][] fSyntaxColorListModel = new String[][] {
			{ "Comment",
				IDeeColorConstants.DEE_COMMENT, sDocumentationCategory },
			{ "Doc Comment",
				IDeeColorConstants.DEE_DOCCOMMENT, sDocumentationCategory },
				
				
				{ "Keywords",
					IDeeColorConstants.DEE_KEYWORD, sCoreCategory },
				{ "Basic Types",
					IDeeColorConstants.DEE_BASICTYPES, sCoreCategory },
				{ "Literals",
					IDeeColorConstants.DEE_LITERALS, sCoreCategory },
                { "Strings",
					IDeeColorConstants.DEE_STRING, sCoreCategory },
                { "Operators",
					IDeeColorConstants.DEE_OPERATORS, sCoreCategory },
				{ "Default",
					IDeeColorConstants.DEE_DEFAULT, sCoreCategory },							
			};
	

	public DeeSourceColoringConfigurationBlock(OverlayPreferenceStore store) {
		super(store);
	}

	@Override
	protected String[][] getSyntaxColorListModel() {
		return fSyntaxColorListModel;
	}

	@Override
	protected ProjectionViewer createPreviewViewer(Composite parent,
			IVerticalRuler verticalRuler, IOverviewRuler overviewRuler,
			boolean showAnnotationsOverview, int styles, IPreferenceStore store) {
		return new ScriptSourceViewer(parent, verticalRuler, overviewRuler,
				showAnnotationsOverview, styles, store);
	}

	@Override
	protected ScriptSourceViewerConfiguration createSimpleSourceViewerConfiguration(
			IColorManager colorManager, IPreferenceStore preferenceStore,
			ITextEditor editor, boolean configureFormatter) {
		return new DeeSimpleSourceViewerConfiguration(colorManager,
				preferenceStore, editor, DeePartitions.DEE_PARTITIONING,
				configureFormatter);
	}
	
	@Override
	protected void setDocumentPartitioning(IDocument document) {
		//DeeDocumentSetupParticipant participant = new DeeDocumentSetupParticipant();
		(new DeeDocumentSetupParticipant()).setup(document);
	}

	@Override
	protected String getPreviewContent() {
		String line;
		String separator = System.getProperty("line.separator"); //$NON-NLS-1$
		StringBuffer buffer = new StringBuffer(512);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(getClass()
					.getResourceAsStream(PREVIEW_FILE_NAME)));
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
				buffer.append(separator);
			}
		} catch (IOException io) {
			// DLTKUIPlugin.log(io);
			io.printStackTrace();
			// System.err.println("io");
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
		return buffer.toString();
	}
}
