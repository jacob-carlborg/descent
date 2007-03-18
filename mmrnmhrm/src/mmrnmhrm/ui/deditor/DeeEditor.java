package mmrnmhrm.ui.deditor;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.ui.actions.SampleAction;
import mmrnmhrm.ui.outline.DeeContentOutlinePage;
import mmrnmhrm.ui.text.DeeDocument;
import mmrnmhrm.ui.text.DeeDocumentProvider;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import util.log.Logg;

public class DeeEditor extends AbstractDecoratedTextEditor {
	
	private DeeDocumentProvider documentProvider;
	private DeeDocument document;
	private DeeContentOutlinePage outlinePage; // Instantiated lazily
	private DeeSourceViewerConfiguration sourceViewerConfiguration;

	public DeeEditor() {
		super();
		this.documentProvider = DeeCore.getDeeDocumentProvider();
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
		//setPreferenceStore(EditorsPlugin.getDefault().getPreferenceStore());
		//configureInsertMode(SMART_INSERT, false);
		setInsertMode(INSERT);
	}

	@Override
	protected void createActions() {
		// TODO Auto-generated method stub createActions
		super.createActions();
	}

	public void dispose() { 
	 	super.dispose(); 
	}

	public DeeDocument getDocument() {
		return document;
	}

	public TextSelection getSelection() {
		return (TextSelection) getSelectionProvider().getSelection();
	}
	
	public void setSelection(int offset, int length) {
		getSelectionProvider().setSelection(new TextSelection(offset, length)); 
	}
	
	
	@Override
	protected void editorContextMenuAboutToShow(IMenuManager menu) {
		super.editorContextMenuAboutToShow(menu);
		menu.appendToGroup("additions", new SampleAction(": <additions> action"));
		menu.add(new SampleAction(": <> action"));
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

	
	protected void doSetInput(IEditorInput input) throws CoreException {
		super.doSetInput(input);
		Logg.println("Got input:" + input + " : " + input.getName());
		document = (DeeDocument) documentProvider.getDocument(input);
		if (outlinePage != null)
			outlinePage.updateInput();
	}
	
	protected void editorSaved() {
		super.editorSaved();
		document.updateCompilationUnit();
		if (outlinePage != null)
			outlinePage.update(); 
	}

	
}
