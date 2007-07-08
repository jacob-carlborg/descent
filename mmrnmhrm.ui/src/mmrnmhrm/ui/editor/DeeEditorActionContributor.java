package mmrnmhrm.ui.editor;

import mmrnmhrm.ui.actions.AbstractDeeEditorAction;
import mmrnmhrm.ui.actions.GoToDefinitionAction;
import mmrnmhrm.ui.actions.PrintPartitionsAction;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.texteditor.BasicTextEditorActionContributor;

public class DeeEditorActionContributor extends	BasicTextEditorActionContributor {

	private DeeEditorActionContributor instance;

	private AbstractDeeEditorAction fGoToDefiniton;
	private AbstractDeeEditorAction ftestAction;
	
	public DeeEditorActionContributor() {
		fGoToDefiniton = new GoToDefinitionAction(null);
		fGoToDefiniton.setToolTipText("FROM contributor");
		ftestAction = new PrintPartitionsAction(null);
		//Assert.isTrue(instance == null);
		instance = this;
	}
	
	public DeeEditorActionContributor getInstance() {
		return instance;
	}

	@Override
	public void contributeToMenu(IMenuManager menu) {
		super.contributeToMenu(menu);

		IMenuManager navigateMenu= menu.findMenuUsingPath(IWorkbenchActionConstants.M_NAVIGATE);
		if (navigateMenu != null) {
			navigateMenu.appendToGroup(IWorkbenchActionConstants.SHOW_EXT, fGoToDefiniton);
		}
/*
		MenuManager mmrnmhrmMenu= new MenuManager("Mmrnmhrm", "mmrnmhrm.menu");
		mmrnmhrmMenu.add(fGoToDefiniton);
		mmrnmhrmMenu.add(ftestAction);
		mmrnmhrmMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menu.insertBefore(IWorkbenchActionConstants.M_PROJECT, mmrnmhrmMenu);
*/
	}
	
	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		super.contributeToToolBar(toolBarManager);
		//toolBarManager.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, fGoToDefiniton);
		toolBarManager.add(fGoToDefiniton);
	}

	@Override
	public void setActiveEditor(IEditorPart part) {
		super.setActiveEditor(part);
		fGoToDefiniton.setActiveEditor(null, part);
		ftestAction.setActiveEditor(null, part);
	}
}
