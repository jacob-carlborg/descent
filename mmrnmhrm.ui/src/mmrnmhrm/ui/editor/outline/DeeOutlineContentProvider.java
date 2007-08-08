package mmrnmhrm.ui.editor.outline;

import java.util.ArrayList;

import melnorme.miscutil.tree.IElement;
import melnorme.util.ui.jface.ElementContentProvider;

import org.eclipse.jface.viewers.Viewer;

import dtool.dom.ast.ASTNode;
import dtool.dom.declarations.DeclarationImport;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Module;
import dtool.dom.definitions.Module.DeclarationModule;
import dtool.refmodel.INonScopedBlock;

public class DeeOutlineContentProvider extends ElementContentProvider {

	//private CompilationUnit root;

	public static Object[] filterElements(IElement[] elements) {
		ArrayList<IElement> deeElems = new ArrayList<IElement>();
		for(IElement element : elements) {
			if(element instanceof DefUnit 
					|| element instanceof DeclarationImport 
					|| element instanceof DeclarationModule
					|| element instanceof INonScopedBlock) {
				deeElems.add(element);
			} 
		}
		return deeElems.toArray();
	}

	public Object[] getElements(Object inputElement) {
		//return filterElements(root.getModule().getChildren());
		return getChildren(inputElement);
	}

	public Object[] getChildren(Object element) {
		if(element instanceof Module || isDeclarationWithDefUnits(element)) {
			ASTNode node = (ASTNode) element;
			return filterElements(node.getChildren());
		} else {
			return ASTNode.NO_ELEMENTS;
		}
	}

	private boolean isDeclarationWithDefUnits(Object element) {
		return (!(element instanceof DefUnit) && element instanceof INonScopedBlock);
	}
	
	public boolean hasChildren(Object element) {
		if(element instanceof Module || isDeclarationWithDefUnits(element)) {
			ASTNode node = (ASTNode) element;
			return filterElements(node.getChildren()).length > 0;
		} else {
			return false;
		}
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		/*if(newInput instanceof IEditorInput) {
			IEditorInput input = (IEditorInput) newInput;
	    	DeeDocumentProvider docProvider = DeePlugin.getDeeDocumentProvider();
	    	root = docProvider.getCompilationUnit(input);
	    } else {
	    	root = null;
	    }*/
	}

}
