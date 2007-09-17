package mmrnmhrm.ui.editor;

import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.DeePluginImages;
import mmrnmhrm.ui.actions.GoToDefinitionHandler;

import org.eclipse.dltk.internal.ui.editor.SourceModuleEditorActionContributor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.menus.CommandContributionItem;

public class DeeEditorActionContributor extends	SourceModuleEditorActionContributor {

	//private AbstractDeeEditorAction fGoToDefiniton;
	//private CommandContributionItem fGoToDefinitonHandler;
	//private AbstractDeeEditorAction ftestAction;
	
	
	public static CommandContributionItem getCommand_FindDefinition() {
		ImageDescriptor imgDesc = DeePluginImages.createActionImageDescriptor("gotodef.gif", true);
		return new CommandContributionItem(DeePlugin.getActiveWorkbenchWindow(), null, 
				GoToDefinitionHandler.COMMAND_ID, null,
				imgDesc, null, null, null, null, null,
				CommandContributionItem.STYLE_PUSH);
	}
	
	public static CommandContributionItem getCommand_SearchReferences() {
		return new CommandContributionItem(DeePlugin.getActiveWorkbenchWindow(), null, 
				"GoToDefinitionHandler.COMMAND_ID", null,
				null, null, null, null, null, null,
				CommandContributionItem.STYLE_PUSH);
	}

	
	@Override
	public void contributeToMenu(IMenuManager menu) {
		super.contributeToMenu(menu);

		IMenuManager navigateMenu= menu.findMenuUsingPath(IWorkbenchActionConstants.M_NAVIGATE);
		if (navigateMenu != null) {
			//navigateMenu.appendToGroup(IWorkbenchActionConstants.SHOW_EXT, fGoToDefiniton);
			
			navigateMenu.appendToGroup(IWorkbenchActionConstants.SHOW_EXT, 
					getCommand_FindDefinition());
		}
	}


	
	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		super.contributeToToolBar(toolBarManager);
		//toolBarManager.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, fGoToDefiniton);
		//toolBarManager.add(fGoToDefiniton);
		//toolBarManager.add(getCommand_FindDefinition());
	}

}
