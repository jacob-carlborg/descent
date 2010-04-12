package testproduct;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	// Actions - important to allocate these only in makeActions, and then use
	// them
	// in the fill methods. This ensures that the actions aren't recreated
	// when fillActionBars is called with FILL_PROXY.
	private IWorkbenchAction exitAction;
	private IWorkbenchAction copyAction;
	private IWorkbenchAction prefAction;
	private IWorkbenchAction helpAction;
	private IWorkbenchAction aboutAction;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	@Override
	protected void makeActions(final IWorkbenchWindow window) {
		// Creates the actions and registers them.
		// Registering is needed to ensure that key bindings work.
		// The corresponding commands keybindings are defined in the plugin.xml
		// file.
		// Registering also provides automatic disposal of the actions when
		// the window is closed.

		exitAction = ActionFactory.QUIT.create(window);
		register(exitAction);
		copyAction = ActionFactory.COPY.create(window);
		register(copyAction);
		prefAction = ActionFactory.PREFERENCES.create(window);
		register(prefAction);
		helpAction = ActionFactory.HELP_CONTENTS.create(window);
		register(helpAction);
		aboutAction = ActionFactory.ABOUT.create(window);
		register(aboutAction);
		TestApplicationPlugin.getInstance().getLog().log(new Status(IStatus.ERROR, TestApplicationPlugin.PLUGIN_ID, "massage"));
	}

	@Override
	protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager fileMenu = new MenuManager("&File", IWorkbenchActionConstants.M_FILE);
		menuBar.add(fileMenu);
		fileMenu.add(copyAction);
		fileMenu.add(prefAction);
		fileMenu.add(exitAction);
		fileMenu.add(aboutAction);
		fileMenu.add(helpAction);
	}
	
	@Override
	public void fillActionBars(int flags) {
		super.fillActionBars(flags);
	}

}
