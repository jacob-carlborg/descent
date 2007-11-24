package descent.internal.ui.compare;

import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;

import org.eclipse.core.resources.IFile;

import org.eclipse.swt.graphics.Image;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;

import org.eclipse.compare.EditionSelectionDialog;
import org.eclipse.compare.ITypedElement;

import descent.core.IMember;

import descent.internal.ui.IJavaHelpContextIds;
import descent.internal.ui.JavaPlugin;
import descent.internal.ui.util.ExceptionHandler;


/**
 * Provides "Replace from local history" for Java elements.
 */
class JavaCompareWithEditionActionImpl extends JavaHistoryActionImpl {
	
	private static final String BUNDLE_NAME= "descent.internal.ui.compare.CompareWithEditionAction"; //$NON-NLS-1$
	
	
	JavaCompareWithEditionActionImpl() {
		super(false);
	}	
	
	public void run(ISelection selection) {
		
		String errorTitle= CompareMessages.CompareWithHistory_title; 
		String errorMessage= CompareMessages.CompareWithHistory_internalErrorMessage; 
				
		IMember input= getEditionElement(selection);
		if (input == null) {
			String invalidSelectionMessage= CompareMessages.CompareWithHistory_invalidSelectionMessage; 
			MessageDialog.openInformation(getShell(), errorTitle, invalidSelectionMessage);
			return;
		}
		
		IFile file= getFile(input);
		if (file == null) {
			MessageDialog.openError(getShell(), errorTitle, errorMessage);
			return;
		}
		
		boolean inEditor= beingEdited(file);

		// get a TextBuffer
		
		IPath path= file.getFullPath();
		ITextFileBufferManager bufferManager= FileBuffers.getTextFileBufferManager();
		
		ITextFileBuffer textFileBuffer= null;
		try {
			bufferManager.connect(path, null);
			textFileBuffer= bufferManager.getTextFileBuffer(path);

			ITypedElement target= new JavaTextBufferNode(file, textFileBuffer.getDocument(), inEditor);

			ITypedElement[] editions= buildEditions(target, file);

			ResourceBundle bundle= ResourceBundle.getBundle(BUNDLE_NAME);
			EditionSelectionDialog d= new EditionSelectionDialog(getShell(), bundle);
			d.setHelpContextId(IJavaHelpContextIds.COMPARE_ELEMENT_WITH_HISTORY_DIALOG);
			d.setCompareMode(true);
			Image image= JavaCompareUtilities.getImage(input);
			d.setEditionTitleImage(image);
			d.selectEdition(target, editions, input);
			if (image != null && !image.isDisposed())
				image.dispose();
		} catch(CoreException ex) {
			ExceptionHandler.handle(ex, getShell(), errorTitle, errorMessage);
		} finally {
			try {
				if (textFileBuffer != null)
					bufferManager.disconnect(path, null);
			} catch (CoreException e) {
				JavaPlugin.log(e);
			}
		}
	}
}

