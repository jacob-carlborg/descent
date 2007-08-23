package mmrnmhrm.ui.editor;

import melnorme.miscutil.log.Logg;
import mmrnmhrm.core.model.CompilationUnit;
import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.DeePluginImages;
import mmrnmhrm.ui.editor.outline.DeeContentOutlinePage;
import mmrnmhrm.ui.text.DeeDocumentProvider;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;


public class DeeEditor extends LangEditor {

	public static final String EDITOR_ID = DeePlugin.PLUGIN_ID + ".editors.DeeEditor";
	public static final String CONTEXTS_DEE_EDITOR = DeePlugin.PLUGIN_ID + ".contexts.DeeEditor";
	
	protected static final boolean CODE_ASSIST_DEBUG = true ||
	"true".equalsIgnoreCase(Platform.getDebugOption(
			DeePlugin.PLUGIN_ID+"/debug/ResultCollector"));
	
	private DeeDocumentProvider documentProvider;
	//private IDocument document;
	private CompilationUnit cunit;
	private DeeContentOutlinePage outlinePage; // Instantiated lazily
	//private DeeSourceViewerConfiguration sourceViewerConfiguration;



	public DeeEditor() {
		super();
		this.documentProvider = DeePlugin.getDeeDocumentProvider();
		setDocumentProvider(documentProvider);
	}
	
	
	@Override
	protected void initializeEditor() {
		super.initializeEditor();
		setPreferenceStore(createCombinedPreferenceStore(null));
		setSourceViewerConfiguration(createLangSourceViewerConfiguration());
		setEditorContextMenuId("#DeeEditorContext"); 
		setRulerContextMenuId("#DeeRulerContext"); 
		//setHelpContextId(ITextEditorHelpContextIds.TEXT_EDITOR);
		setInsertMode(INSERT);
	}
	
	private SourceViewerConfiguration createLangSourceViewerConfiguration() {
		return DeePlugin.getDefault().getTextTools()
			.createSourceViewerConfiguraton(getPreferenceStore(), this);
	}
	
	@Override
	protected void initializeKeyBindingScopes() {
		setKeyBindingScopes(new String[] { CONTEXTS_DEE_EDITOR });  //$NON-NLS-1$
	}

	public void dispose() { 
	 	super.dispose(); 
	}
	

	public IDocument getDocument() {
		return getSourceViewer().getDocument();
	}
	
	public CompilationUnit getCompilationUnit() {
		return cunit;
	}
	
	@Override
	protected void handlePreferenceStoreChanged(PropertyChangeEvent event) {
		super.handlePreferenceStoreChanged(event);
	}
	
	@Override
	protected boolean affectsTextPresentation(PropertyChangeEvent event) {
		return ((ScriptSourceViewerConfiguration) getSourceViewerConfiguration())
				.affectsTextPresentation(event)
				|| super.affectsTextPresentation(event);
	}

	protected void doSetInput(IEditorInput input) throws CoreException {
		super.doSetInput(input);
		Logg.main.println("Got Editor input:" + input + " : " + input.getName());
		//document = documentProvider.getDocument(input);
		cunit = DeePlugin.getInstance().getCompilationUnit(input);
		if(cunit.isOutOfModel()) {
			setTitleImage(DeePluginImages.getImage(DeePluginImages.ELEM_FILEOUT));
		}
		
		if (outlinePage != null)
			outlinePage.updateView();
	}
	
	@Override
	protected void performSave(boolean overwrite, IProgressMonitor progressMonitor) {
		super.performSave(overwrite, progressMonitor);
	}
	
	protected void editorSaved() {
		super.editorSaved();
		cunit.reconcile();
		if (outlinePage != null)
			outlinePage.updateView(); 
	}
	
	public Object getAdapter(Class required) {
		if (IContentOutlinePage.class.equals(required)) {
			if (outlinePage == null) {
				outlinePage = new DeeContentOutlinePage(this);
			}
			return outlinePage;
		}
		return super.getAdapter(required);
	}

	@Override
	protected void editorContextMenuAboutToShow(IMenuManager menu) {
		super.editorContextMenuAboutToShow(menu);
		//menu.prependToGroup(ITextEditorActionConstants.GROUP_OPEN, fActionGoToDefinition);
		menu.prependToGroup(ITextEditorActionConstants.GROUP_OPEN,
				DeeEditorActionContributor.getCommand_FindDefinition());
	}

}
