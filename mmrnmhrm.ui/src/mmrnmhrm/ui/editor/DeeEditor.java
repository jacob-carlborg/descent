package mmrnmhrm.ui.editor;

import mmrnmhrm.core.model.CompilationUnit;
import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.DeePluginImages;
import mmrnmhrm.ui.actions.AbstractDeeEditorAction;
import mmrnmhrm.ui.actions.GoToDefinitionAction;
import mmrnmhrm.ui.editor.outline.DeeContentOutlinePage;
import mmrnmhrm.ui.text.DeeDocumentProvider;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import util.log.Logg;

public class DeeEditor extends LangEditor {

	public static final String EDITOR_ID = DeePlugin.PLUGIN_ID + ".editors.DeeEditor";
	
	private DeeDocumentProvider documentProvider;
	private IDocument document;
	private CompilationUnit cunit;
	private DeeContentOutlinePage outlinePage; // Instantiated lazily
	private DeeSourceViewerConfiguration sourceViewerConfiguration;

	AbstractDeeEditorAction fActionGoToDefinition;

	public DeeEditor() {
		super();
		this.documentProvider = DeePlugin.getDeeDocumentProvider();
		setDocumentProvider(documentProvider);
	}
	
	
	@Override
	protected void initializeEditor() {
		super.initializeEditor();
		sourceViewerConfiguration = new DeeSourceViewerConfiguration();
		setSourceViewerConfiguration(sourceViewerConfiguration);
		setEditorContextMenuId("#DeeEditorContext"); 
		setRulerContextMenuId("#DeeRulerContext"); 
		//setHelpContextId(ITextEditorHelpContextIds.TEXT_EDITOR);
		setInsertMode(INSERT);
		setPreferenceStore(createCombinedPreferenceStore(null));

	}
	
	@Override
	protected void createActions() {
		super.createActions();
		fActionGoToDefinition = new GoToDefinitionAction(this);
	}

	public void dispose() { 
	 	super.dispose(); 
	}
	

	public IDocument getDocument() {
		return document;
	}
	
	public CompilationUnit getCompilationUnit() {
		return cunit;
	}
	
	@Override
	protected boolean affectsTextPresentation(PropertyChangeEvent event) {
		// FIXME properly react to presentation preference changes
		DeePlugin.getDefaultDeeCodeScanner().loadDeeTokens();
		return true;
	}

	protected void doSetInput(IEditorInput input) throws CoreException {
		super.doSetInput(input);
		Logg.main.println("Got Editor input:" + input + " : " + input.getName());
		document = documentProvider.getDocument(input);
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
		cunit.reconcile();
	}
	
	protected void editorSaved() {
		super.editorSaved();
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
		menu.prependToGroup(ITextEditorActionConstants.GROUP_OPEN, fActionGoToDefinition);
	}

}
