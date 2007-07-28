package mmrnmhrm.ui.actions;

import java.util.Collection;

import melnorme.lang.ui.EditorUtil;
import melnorme.miscutil.StringUtil;
import melnorme.miscutil.log.Logg;
import mmrnmhrm.core.model.CompilationUnit;
import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.editor.DeeEditor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;

import dtool.dom.ast.ASTElementFinder;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.ASTPrinter;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Module;
import dtool.dom.definitions.Symbol;
import dtool.dom.references.Entity;
import dtool.refmodel.EntitySearch;
import dtool.refmodel.IIntrinsicUnit;
import dtool.refmodel.NodeUtil;

public class GoToDefinitionHandler extends AbstractHandler  {

	public static final String COMMAND_ID = DeePlugin.PLUGIN_ID+".commands.openDefinition";
	private static final String GO_TO_DEFINITION_OPNAME = "Go to Definition";

	public Object execute(ExecutionEvent event) throws ExecutionException {

		IEditorPart editor = HandlerUtil.getActiveEditorChecked(event);
		try {
			executeOperation((ITextEditor) editor, false);
		} catch (CoreException ce) {
			throw new ExecutionException(GO_TO_DEFINITION_OPNAME, ce);
		}
		return null;
	}

	public static void executeChecked(final ITextEditor srcEditor,
			final boolean openNewEditor) {
		OperationsManager.doOperation2(GO_TO_DEFINITION_OPNAME, new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				executeOperation(srcEditor, openNewEditor);
			}
		});
	}

	public static void executeOperation(ITextEditor srcEditor,
			boolean openNewEditor) throws CoreException {

		IWorkbenchWindow window = srcEditor.getSite().getWorkbenchWindow();
		
		IEditorInput input = srcEditor.getEditorInput();
		CompilationUnit srcCUnit = DeePlugin.getInstance().getCompilationUnit(input);
		srcCUnit.reconcile();
		
		TextSelection sel = EditorUtil.getSelection(srcEditor);
		int offset = sel.getOffset();
		
		ASTNode elem = ASTElementFinder.findElement(srcCUnit.getModule(), offset);
		
		if(elem == null) {
			dialogWarning(window.getShell(), "No element found at pos: " + offset);
			Logg.main.println(" ! ASTElementFinder null?");
			return;
		}
		Logg.main.println(" Selected Element: " + ASTPrinter.toStringNodeExtra(elem));

		if(elem instanceof Symbol) {
			dialogInfo(window.getShell(),
					"Element is not an entity reference,"
					+" it's already a definition: " + elem);
			return;
		}
		if(!(elem instanceof Entity)) {
			dialogInfo(window.getShell(),
					"Element is not an entity reference: "+ elem);
			return;
		} 
		
		// find the target
		Collection<DefUnit> defunits = ((Entity)elem).findTargetDefUnits(false);
		
		if(defunits == null || defunits.size() == 0) {
			dialogWarning(window.getShell(), 
					"Definition not found for entity reference: " + elem);
			return;
		}

		Logg.main.println(" Find Definition, found: " 
				+ StringUtil.collToString(defunits, " ") );
		
		if(defunits.size() > 1) {
			dialogInfo(window.getShell(), 
					"Multiple definitions found: \n" 
					+ StringUtil.collToString(defunits, "\n")
					+ "\nGoing to the first one.");
		}

		DefUnit defunit = EntitySearch.getResultDefUnit(defunits);
		
		if(defunit.hasNoSourceRangeInfo()) {
			dialogError(window.getShell(),
					"DefUnit: " +defunit+ " has no source range info!");
			return;
		} 
		if(defunit instanceof IIntrinsicUnit) {
			dialogInfo(window.getShell(),
				"DefUnit: " +defunit+ " is a language native.");
			return;
		} 
		
		ITextEditor targetEditor;

		Module targetModule = NodeUtil.getParentModule(defunit);
		CompilationUnit targetCUnit = (CompilationUnit) targetModule.getCUnit();

		if(openNewEditor || srcCUnit != targetCUnit) {
			IWorkbenchPage page = window.getActivePage();
			targetEditor = (ITextEditor) IDE.openEditor(page, targetCUnit.getFile(), DeeEditor.EDITOR_ID);
		} else {
			targetEditor = srcEditor;
		}
		EditorUtil.setSelection(targetEditor, defunit);

	}
	

	private static void dialogError(Shell shell, String msg) {
		OperationsManager.openError(shell,
				GO_TO_DEFINITION_OPNAME, msg);
	}

	static void dialogWarning(Shell shell, String msg) {
		OperationsManager.openWarning(shell,
				GO_TO_DEFINITION_OPNAME, msg);
	}

	static void dialogInfo(Shell shell, String msg) {
		OperationsManager.openInfo(shell,
				GO_TO_DEFINITION_OPNAME, msg);
	}
	
	
}
