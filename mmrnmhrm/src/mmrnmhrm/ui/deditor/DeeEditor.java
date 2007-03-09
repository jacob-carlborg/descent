package mmrnmhrm.ui.deditor;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.text.DeeDocument;
import mmrnmhrm.text.DeeDocumentProvider;
import mmrnmhrm.ui.actions.SampleAction;
import mmrnmhrm.ui.outline.DeeContentOutlinePage;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

public class DeeEditor extends AbstractDecoratedTextEditor {
	
	DeeDocumentProvider documentProvider;
	DeeDocument document;
	DeeContentOutlinePage outlinePage;
	public DeeSourceViewerConfiguration sourceViewerConfiguration;

	public DeeEditor() {
		super();
		this.documentProvider = DeeCore.getDeeDocumentProvider();
		setDocumentProvider(documentProvider);
	}
	
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
	
	public void dispose() { 
	 	super.dispose(); 
	}
	@Override
	protected void createActions() {
		// TODO Auto-generated method stub
		super.createActions();
	}

	

	public DeeDocument getDocument() {
		return document;
	}
	

	public TextSelection getSelection() {
		return (TextSelection) getSelectionProvider().getSelection();
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
		document = (DeeDocument) documentProvider.getDocument(input);
	}
	
	protected void editorSaved() {
		super.editorSaved();
		document.updateCompilationUnit();
		if (outlinePage != null)
			outlinePage.update(); 
	}

	
}
