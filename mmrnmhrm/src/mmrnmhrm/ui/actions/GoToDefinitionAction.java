package mmrnmhrm.ui.actions;

import melnorme.lang.ui.EditorUtil;
import mmrnmhrm.core.model.CompilationUnit;
import mmrnmhrm.ui.DeePluginImages;
import mmrnmhrm.ui.editor.DeeEditor;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import util.log.Logg;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.ASTPrinter;
import dtool.dom.base.Entity;
import dtool.dom.definitions.DefUnit;
import dtool.model.IIntrinsicUnit;

public class GoToDefinitionAction extends DeeEditorAction {
	
	public GoToDefinitionAction() {
		super("Go To Definition");
	}
	
	public GoToDefinitionAction(DeeEditor deeEditor) {
		this();
		this.deeEditor = deeEditor;
		DeePluginImages.setupActionImages(this, "gotodef.gif");
	}
	
	public void run() {
		GoToDefinitionAction.execute(deeEditor);
	}
	
	/** {@inheritDoc} */
	public void run(IAction action) {
		GoToDefinitionAction.execute(deeEditor);
	}

	private static void dialogInfo(Shell shell, String string) {
		MessageDialog.openInformation(shell,
				"Go to Definition",	string);
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

	public static void execute(DeeEditor deeEditor) {
		IWorkbenchWindow window = deeEditor.getSite().getWorkbenchWindow();
		
		TextSelection sel = deeEditor.getSelection();
		int offset = sel.getOffset();
		Logg.main.println("[" + sel.getOffset() +","+ sel.getLength() + "] =>" + offset);
		Logg.main.println(sel.getText());
		
		CompilationUnit cunit = deeEditor.getDocument().getCompilationUnit();
	
		ASTNode elem = cunit.findEntity(offset);
		if(elem == null) {
			dialogWarning(window.getShell(), "No element found at pos: " + offset);
			return;
		}
		System.out.println("FOUND: " + ASTPrinter.toStringNodeExtra(elem));
		
		GoToDefinitionAction.execute(deeEditor, elem);
	}

	public static void execute(AbstractTextEditor deeEditor, ASTNode elem) {
		IWorkbenchWindow window = deeEditor.getSite().getWorkbenchWindow();
		
		if(elem instanceof Entity) {
			DefUnit defunit = ((Entity)elem).getTargetDefUnit();
			if(defunit == null) {
				dialogWarning(window.getShell(), "Definition not found for entity: " + elem);
				return;
			}
			if(defunit.hasNoSourceRangeInfo()) {
				dialogInfo(window.getShell(),
						"DefUnit: " +defunit+ " has no source range info!");
			} else if(defunit instanceof IIntrinsicUnit) {
					dialogInfo(window.getShell(),
							"DefUnit: " +defunit+ " is languageintrinsic.");
			} else {
				//IWorkbenchPage page = window.getActivePage();
				//Module module = getModule(defunit);
				//module.cunit
				//IDE.openEditor(page, resource, true);
				EditorUtil.setSelection(deeEditor, defunit);
			}
		} else if(elem instanceof DefUnit.Symbol) {
			dialogInfo(window.getShell(),
					"Already at definition of element: " + elem);
		} else {
			dialogInfo(window.getShell(),
					"Element is not an entity reference. ("+ elem +")");
		} 
	
	}

}
