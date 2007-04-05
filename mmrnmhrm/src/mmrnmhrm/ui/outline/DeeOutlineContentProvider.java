package mmrnmhrm.ui.outline;

import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.text.DeeDocumentProvider;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IEditorInput;

import util.tree.TreeDepthRecon;
import dtool.dom.base.ASTNode;
import dtool.project.CompilationUnit;

public class DeeOutlineContentProvider implements ITreeContentProvider {

	private CompilationUnit root;


	public Object[] getChildren(Object parentElement) {
		ASTNode elem = (ASTNode) parentElement;
		return elem.getChildren();
	}

	public Object getParent(Object element) {
		ASTNode elem = (ASTNode) element; 
		return elem.getParent();
	}

	public boolean hasChildren(Object element) {
		ASTNode elem = (ASTNode) element; 
		return !TreeDepthRecon.isLeaf(elem);
	}

	public Object[] getElements(Object inputElement) {
		return root.getModule().getChildren();
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		if(newInput instanceof IEditorInput) {
			IEditorInput input = (IEditorInput) newInput;
	    	DeeDocumentProvider docProvider = DeePlugin.getDeeDocumentProvider();
	    	root = docProvider.getCompilationUnit(input);
	    } else {
	    	root = null;
	    }
	}


	public void dispose() {
	}

}
