package mmrnmhrm.ui.editors;

import mmrnmhrm.DeeCore;
import mmrnmhrm.text.DeeDocument;
import mmrnmhrm.text.DeeDocumentProvider;
import mmrnmhrm.ui.outline.DeeOutlinePage;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.util.Assert;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.ITextEditorHelpContextIds;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

public class DeeEditor extends AbstractDecoratedTextEditor {
	
	DeeDocumentProvider documentProvider;
	DeeDocument document;
	DeeOutlinePage outlinePage;

	public DeeEditor() {
		super();
		documentProvider = DeeCore.getDeeDocumentProvider();
		setDocumentProvider(documentProvider);
	}
	
	protected void initializeEditor() {
		super.initializeEditor();
		setSourceViewerConfiguration(new DeeSourceViewerConfiguration());
		setEditorContextMenuId("#DeeEditorContext"); 
		setRulerContextMenuId("#DeeRulerContext"); 
		/*
		setHelpContextId(ITextEditorHelpContextIds.TEXT_EDITOR);
		setPreferenceStore(EditorsPlugin.getDefault().getPreferenceStore());
		*/
		//configureInsertMode(SMART_INSERT, false);
		setInsertMode(INSERT);

	}
	
	public void dispose() { 
	 	super.dispose(); 
	}
	
	protected void doSetInput(IEditorInput input) throws CoreException {
		super.doSetInput(input);
		document = (DeeDocument) documentProvider.getDocument(input);
	}
	
	protected void editorSaved() {
		super.editorSaved();
		if (outlinePage != null)
			outlinePage.update(); 
	}
	

	public DeeDocument getDocument() {
		return document;
	}
	
	public Object getAdapter(Class required) {
		
		if (IContentOutlinePage.class.equals(required)) {
			if (outlinePage == null) {
				outlinePage = new DeeOutlinePage(this);
			}
			return outlinePage;
		}
		return super.getAdapter(required);
	}


	
}
