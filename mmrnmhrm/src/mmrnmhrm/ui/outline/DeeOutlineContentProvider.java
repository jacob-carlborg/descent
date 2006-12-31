package mmrnmhrm.ui.outline;

import mmrnmhrm.DeeCore;
import mmrnmhrm.text.DeeDocumentProvider;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IEditorInput;

import util.Assert;

import dtool.dom.ast.ASTChecker;
import dtool.dom.ast.TreeDepthRecon;
import dtool.dom.base.ASTNode;
import dtool.dom.base.Module;
import dtool.project.CompilationUnit;

public class DeeOutlineContentProvider implements ITreeContentProvider {

	private IEditorInput input;

	public Object[] getChildren(Object parentElement) {
		
		if (parentElement == input) {
			return new String[] { new String("INPUT") };
		}

		ASTNode elem = (ASTNode) parentElement;
		return elem.getChildren();
	}

	public Object getParent(Object element) {
		Assert.fail();
		ASTNode elem = (ASTNode) element; 
		return elem.getParent();
	}

	public boolean hasChildren(Object element) {
		ASTNode elem = (ASTNode) element; 
		return !TreeDepthRecon.isLeaf(elem);
	}

	public Object[] getElements(Object inputElement) {
		
		if(inputElement instanceof IEditorInput) {
			input = (IEditorInput) inputElement;
	    	
	    	DeeDocumentProvider documentProvider = DeeCore.getDeeDocumentProvider();
	    	CompilationUnit cunit = documentProvider.getCompilationUnit(input);
	    	
	    	return cunit.getNeoModule().getChildren();
	    }
		
		ASTNode elem = (ASTNode) inputElement;
		return elem.getChildren();
		
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		//Assert.isTrue(newInput == null);
	}

}
