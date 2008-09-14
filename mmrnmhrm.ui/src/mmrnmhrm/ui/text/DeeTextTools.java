package mmrnmhrm.ui.text;

import melnorme.miscutil.Assert;

import org.eclipse.dltk.ui.editor.highlighting.ISemanticHighlighter;
import org.eclipse.dltk.ui.editor.highlighting.SemanticHighlighting;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.ui.texteditor.ITextEditor;

public class DeeTextTools extends ScriptTextTools {
	
	private IPartitionTokenScanner fPartitionScanner;
	
	public DeeTextTools(boolean autoDisposeOnDisplayDispose) {
		super(DeePartitions.DEE_PARTITIONING, DeePartitions.LEGAL_CONTENT_TYPES,
				autoDisposeOnDisplayDispose);
		fPartitionScanner = new DeePartitionScanner();
	}

	@Override
	public IPartitionTokenScanner getPartitionScanner() {
		return fPartitionScanner;
	}
	
	@Override
	public ScriptSourceViewerConfiguration createSourceViewerConfiguraton(
			IPreferenceStore preferenceStore, ITextEditor editor, String partitioning) {
		Assert.isTrue(partitioning.equals(DeePartitions.DEE_PARTITIONING));
		return new DeeSourceViewerConfiguration(getColorManager(), preferenceStore, editor,
				DeePartitions.DEE_PARTITIONING);
	}


	@Override
	public SemanticHighlighting[] getSemanticHighlightings() {
		return new SemanticHighlighting[0];
	}

	@Override
	public ISemanticHighlighter getSemanticPositionUpdater() {
		return null;
	}

}
