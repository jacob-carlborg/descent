package mmrnmhrm.ui.editor;

import java.util.Iterator;

import mmrnmhrm.core.dltk.DeeLanguageToolkit;
import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.text.DeeDocumentSetupParticipant;
import mmrnmhrm.ui.text.DeePartitions;

import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.ISourceReference;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.dltk.internal.ui.editor.ScriptOutlinePage;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.dltk.ui.text.folding.IFoldingStructureProvider;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.ITextViewerExtension;
import org.eclipse.jface.text.source.DefaultCharacterPairMatcher;
import org.eclipse.jface.text.source.ICharacterPairMatcher;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;

import dtool.dom.ast.ASTNeoNode;
import dtool.dom.definitions.DefUnit;

public class DeeEditor extends ScriptEditor {
	
	public static final String EDITOR_ID = DeePlugin.PLUGIN_ID + ".editors.DeeEditor";
	public static final String CONTEXTS_DEE_EDITOR = DeePlugin.PLUGIN_ID + ".contexts.DeeEditor";

	public static final String EDITOR_CONTEXT = "#DeeEditorContext";
	public static final String RULER_CONTEXT = "#DeeRulerContext";

	private org.eclipse.dltk.internal.ui.editor.
		BracketInserter fBracketInserter = new DeeBracketInserter(this);
	
	private ICharacterPairMatcher bracketMatcher = 
		new DefaultCharacterPairMatcher("{}[]()".toCharArray());

	protected void initializeEditor() {
		super.initializeEditor();
		setEditorContextMenuId(EDITOR_CONTEXT);
		setRulerContextMenuId(RULER_CONTEXT);
	}
	
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
	protected void initializeKeyBindingScopes() {
		setKeyBindingScopes(new String[] { CONTEXTS_DEE_EDITOR });
	}

	@Override
	protected ScriptOutlinePage doCreateOutlinePage() {
		return new DeeOutlinePage(this, DeePlugin.getDefault().getPreferenceStore());
	}
	
	@Override
	protected void doSelectionChanged(SelectionChangedEvent event) {
		// XXX: DLTK copy 0.9
		ISourceReference reference = null;
		ISelection selection = event.getSelection();
		Iterator iter = ((IStructuredSelection) selection).iterator();
		while (iter.hasNext()) {
			Object obj = iter.next();
			if (obj instanceof ASTNeoNode) {
				reference = adaptNodeToReference((ASTNeoNode)obj);
				break;
			}
		}
		if (!isActivePart() && DLTKUIPlugin.getActivePage() != null)
			DLTKUIPlugin.getActivePage().bringToTop(this);
		setSelection(reference, !isActivePart());
	}

	private ISourceReference adaptNodeToReference(final ASTNeoNode node) {
		return new org.eclipse.dltk.core.ISourceReference(){
			@Override
			final public ISourceRange getSourceRange() {
				return new ISourceRange() {
					@Override
					final public int getOffset() {
						if(node instanceof DefUnit)
							return ((DefUnit)node).defname.getOffset();
						return node.getOffset();
					}
				
					@Override
					final public int getLength() {
						if(node instanceof DefUnit)
							return ((DefUnit)node).defname.getLength();
						return node.getLength();
					}
				
				};
			}
			@Override
			final public boolean exists() {
				return true;
			}
			@Override
			final public String getSource() throws ModelException {
				return null;
			}
		};
	}
	
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		boolean closeBrackets = true;
		boolean closeStrings = true;
		boolean closeAngularBrackets = false;

		fBracketInserter.setCloseBracketsEnabled(closeBrackets);
		fBracketInserter.setCloseStringsEnabled(closeStrings);
		fBracketInserter.setCloseAngularBracketsEnabled(closeAngularBrackets);

		ISourceViewer sourceViewer = getSourceViewer();
		if (sourceViewer instanceof ITextViewerExtension)
			((ITextViewerExtension) sourceViewer)
					.prependVerifyKeyListener(fBracketInserter);
	}
	
	public void dispose() {
		ISourceViewer sourceViewer = getSourceViewer();
		if (sourceViewer instanceof ITextViewerExtension)
			((ITextViewerExtension) sourceViewer)
					.removeVerifyKeyListener(fBracketInserter);
		super.dispose();
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
	
	private IFoldingStructureProvider fFoldingProvider = null;

	protected IFoldingStructureProvider getFoldingStructureProvider() {
		if (fFoldingProvider == null) {
			fFoldingProvider = new DeeFoldingStructureProvider();
		}
		return fFoldingProvider;
	}

	
	@SuppressWarnings("restriction")
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
