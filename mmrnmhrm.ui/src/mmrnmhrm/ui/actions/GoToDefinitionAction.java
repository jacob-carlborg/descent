package mmrnmhrm.ui.actions;

import melnorme.lang.ui.EditorUtil;
import melnorme.miscutil.log.Logg;
import mmrnmhrm.core.model.CompilationUnit;
import mmrnmhrm.ui.DeePluginImages;
import mmrnmhrm.ui.editor.DeeEditor;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import dtool.dom.ast.ASTElementFinder;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.ASTPrinter;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Module;
import dtool.dom.definitions.Symbol;
import dtool.dom.references.Entity;
import dtool.refmodel.IIntrinsicUnit;
import dtool.refmodel.NodeUtil;

public class GoToDefinitionAction extends AbstractDeeEditorAction {
	
	public GoToDefinitionAction() {
		super("Go To Definition");
	}
	
	public GoToDefinitionAction(DeeEditor deeEditor) {
		this();
		this.deeEditor = deeEditor;
		DeePluginImages.setupActionImages(this, "gotodef.gif");
	}
	
	public void run() {
		GoToDefinitionAction.execute(deeEditor.getCompilationUnit(), deeEditor);
	}
	
	/** {@inheritDoc} */
	public void run(IAction action) {
		GoToDefinitionAction.execute(deeEditor.getCompilationUnit(), deeEditor);
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

	public static void execute(CompilationUnit cunit, DeeEditor deeEditor) {
		IWorkbenchWindow window = deeEditor.getSite().getWorkbenchWindow();
		
		TextSelection sel = deeEditor.getSelection();
		int offset = sel.getOffset();
		Logg.main.println("[" + sel.getOffset() +","+ sel.getLength() + "] =>" + offset);
		Logg.main.println(sel.getText());
		
		
		ASTNode elem = ASTElementFinder.findElement(cunit.getModule(), offset);
		
		if(elem == null) {
			dialogWarning(window.getShell(), "No element found at pos: " + offset);
			return;
		}
		System.out.println("FOUND: " + ASTPrinter.toStringNodeExtra(elem));
		

		
		GoToDefinitionAction.execute(deeEditor, cunit, elem);
	}

	public static void execute(AbstractTextEditor deeEditor, CompilationUnit refCUnit, ASTNode elem) {
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
							"DefUnit: " +defunit+ " is language intrinsic.");
			} else {
				try {
					Module targetModule = NodeUtil.getParentModule(defunit);
					CompilationUnit targetCUnit = (CompilationUnit) targetModule.cunit;
					if(deeEditor == null || targetCUnit != refCUnit) {
						IWorkbenchPage page = window.getActivePage();
						deeEditor = (AbstractTextEditor) IDE.openEditor(page, targetCUnit.file, DeeEditor.EDITOR_ID);
					}
					EditorUtil.setSelection(deeEditor, defunit);
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
		} else if(elem instanceof Symbol) {
			dialogInfo(window.getShell(),
					"Already at definition of element: " + elem);
		} else {
			dialogInfo(window.getShell(),
					"Element is not an entity reference. ("+ elem +")");
		} 
	
	}

}
