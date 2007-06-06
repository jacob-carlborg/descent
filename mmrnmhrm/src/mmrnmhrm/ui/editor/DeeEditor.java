package mmrnmhrm.ui.editor;

import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.actions.DeeEditorAction;
import mmrnmhrm.ui.actions.GoToDefinitionAction;
import mmrnmhrm.ui.editor.outline.DeeContentOutlinePage;
import mmrnmhrm.ui.text.DeeDocument;
import mmrnmhrm.ui.text.DeeDocumentProvider;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import util.log.Logg;

public class DeeEditor extends LangEditor {
	
	private DeeDocumentProvider documentProvider;
	private DeeDocument document;
	private DeeContentOutlinePage outlinePage; // Instantiated lazily
	private DeeSourceViewerConfiguration sourceViewerConfiguration;

	DeeEditorAction fActionGoToDefinition;

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
	

	public void dispose() { 
	 	super.dispose(); 
	}
	
	@Override
	protected boolean affectsTextPresentation(PropertyChangeEvent event) {
		// FIXME properly react to presentation preference changes
		DeePlugin.getDefaultDeeCodeScanner().loadDeeTokens();
		return true;
	}

	@Override
	protected void createActions() {
		super.createActions();
		fActionGoToDefinition = new GoToDefinitionAction(this);
	}


	public DeeDocument getDocument() {
		return document;
	}

	protected void doSetInput(IEditorInput input) throws CoreException {
		super.doSetInput(input);
		Logg.main.println("Got Editor input:" + input + " : " + input.getName());
		document = (DeeDocument) documentProvider.getDocument(input);
		
		if (outlinePage != null)
			outlinePage.updateView();
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
