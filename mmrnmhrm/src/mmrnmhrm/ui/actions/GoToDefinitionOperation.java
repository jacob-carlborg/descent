package mmrnmhrm.ui.actions;

import melnorme.lang.ui.EditorUtil;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import dtool.dom.ast.ASTNode;
import dtool.dom.base.Entity;
import dtool.dom.base.EntityConstrainedRef;
import dtool.dom.definitions.DefUnit;

public class GoToDefinitionOperation {
	
	public static void execute(IWorkbenchWindow window, ASTNode elem) {
		if(elem instanceof EntityConstrainedRef)
			elem = ((EntityConstrainedRef) elem).entity;
			
		if(elem instanceof Entity) {
			DefUnit defunit = ((Entity)elem).getTargetDefUnit();
			if(defunit == null) {
				dialogWarning(window.getShell(), "Definition not found for entity: " + elem);
				return;
			}
			IWorkbenchPage page = window.getActivePage();
			//Module module = getModule(defunit);
			//module.cunit
			//IDE.openEditor(page, resource, true);
			EditorUtil.setSelection((AbstractTextEditor) page.getActiveEditor(), defunit);
		} else if(elem instanceof DefUnit.Symbol) {
			dialogInfo(window.getShell(),
					"Already at definition of element: " + elem);
		} else {
			dialogInfo(window.getShell(),
					"Element is not an entity reference. ("+ elem +")");
		} 

	}
	
	/*private static Module getModule(ASTNode node) {
		while(!(node instanceof Module)) {
			Assert.isTrue(!(node instanceof descent.internal.core.dom.Module));
			node = node.parent;
		}
		return (Module) node;
	}*/

	private static void dialogWarning(Shell shell, String string) {
		MessageDialog.openWarning(shell,
				"Go to Definition",	string);
	}

	private static void dialogInfo(Shell shell, String string) {
		MessageDialog.openInformation(shell,
				"Go to Definition",	string);
	}

}