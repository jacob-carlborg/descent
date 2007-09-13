package mmrnmhrm.ui.editor;

import mmrnmhrm.core.dltk.DeeLanguageToolkit;
import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.text.DeeDocumentSetupParticipant;
import mmrnmhrm.ui.text.DeePartitions;

import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.internal.ui.editor.ScriptOutlinePage;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.dltk.ui.text.folding.IFoldingStructureProvider;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.source.DefaultCharacterPairMatcher;
import org.eclipse.jface.text.source.ICharacterPairMatcher;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;

public class DeeEditor extends DeeBaseEditor {
	
	public static final String EDITOR_ID = DeePlugin.PLUGIN_ID + ".editors.DeeEditor";
	public static final String CONTEXTS_DEE_EDITOR = DeePlugin.PLUGIN_ID + ".contexts.DeeEditor";

	public static final String EDITOR_CONTEXT = "#DeeEditorContext";
	public static final String RULER_CONTEXT = "#DeeRulerContext";

	
	private ICharacterPairMatcher bracketMatcher = 
		new DefaultCharacterPairMatcher("{}[]()".toCharArray());

	@Override
	public String getEditorId() {
		return EDITOR_ID;
	}
	
	@Override
	public IDLTKLanguageToolkit getLanguageToolkit() {
		return DeeLanguageToolkit.getDefault();
	}
	
	@Override
	protected IPreferenceStore getScriptPreferenceStore() {
		return DeePlugin.getDefault().getPreferenceStore();
	}

	@Override
	public ScriptTextTools getTextTools() {
		return DeePlugin.getDefault().getTextTools();
	}
	
	@Override
	protected ScriptOutlinePage doCreateOutlinePage() {
		return new DeeOutlinePage(this, DeePlugin.getDefault().getPreferenceStore());
	}
	
	@Override
	protected void connectPartitioningToElement(IEditorInput input,
			IDocument document) {
		if (document instanceof IDocumentExtension3) {
			IDocumentExtension3 extension = (IDocumentExtension3) document;
			if (extension.getDocumentPartitioner(DeePartitions.DEE_PARTITIONING) == null) {
				DeeDocumentSetupParticipant participant = new DeeDocumentSetupParticipant();
				participant.setup(document);
			}
		}
	}
	
	@Override
	protected void initializeEditor() {
		super.initializeEditor();
		setEditorContextMenuId(EDITOR_CONTEXT);
		setRulerContextMenuId(RULER_CONTEXT);
	}

	
	@Override
	protected void initializeKeyBindingScopes() {
		setKeyBindingScopes(new String[] { CONTEXTS_DEE_EDITOR });
	}
	
	
	@Override
	protected void createActions() {
		super.createActions();
	}
	
	@Override
	public void editorContextMenuAboutToShow(IMenuManager menu) {
		super.editorContextMenuAboutToShow(menu);
		//menu.prependToGroup(ITextEditorActionConstants.GROUP_OPEN, fActionGoToDefinition);
		menu.prependToGroup(ITextEditorActionConstants.GROUP_OPEN,
				DeeEditorActionContributor.getCommand_FindDefinition());
	}
	
	@Override
	protected void configureSourceViewerDecorationSupport(
			SourceViewerDecorationSupport support) {
		support.setCharacterPairMatcher(bracketMatcher);
		support.setMatchingCharacterPainterPreferenceKeys(MATCHING_BRACKETS,
				MATCHING_BRACKETS_COLOR);

		super.configureSourceViewerDecorationSupport(support);
	}

	
	private IFoldingStructureProvider fFoldingProvider = null;

	@Override
	protected IFoldingStructureProvider getFoldingStructureProvider() {
		if (fFoldingProvider == null) {
			fFoldingProvider = new DeeFoldingStructureProvider();
		}
		return fFoldingProvider;
	}

	
	@SuppressWarnings("restriction") @Override
	protected org.eclipse.dltk.internal.ui.actions.
	FoldingActionGroup createFoldingActionGroup() {
		return new org.eclipse.dltk.internal.ui.actions.
				FoldingActionGroup(this, getViewer(), DeePlugin.getDefault()
				.getPreferenceStore());
	}
	
	@Override
	public String getCallHierarchyID() {
		return "org.eclipse.dltk.callhierarchy.view";
	}
	

}
