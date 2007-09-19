package mmrnmhrm.ui.launch;

import melnorme.miscutil.ExceptionAdapter;
import melnorme.util.ui.swt.SWTUtilExt;
import mmrnmhrm.core.build.IDeeBuilderListener;
import mmrnmhrm.ui.DeePlugin;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class DeeBuilderUIListener implements IDeeBuilderListener {

	//http://wiki.eclipse.org/FAQ_How_do_I_write_to_the_console_from_a_plug-in%3F
	
	private static final String CONSOLE_NAME = "mmrnmhrm Dee Build output:";

	public static MessageConsole getConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMgr = plugin.getConsoleManager();
		IConsole[] existing = conMgr.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		//no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMgr.addConsoles(new IConsole[]{myConsole});
		return myConsole;
	}
	
	public void clear() {
		final MessageConsole myConsole = getConsole(CONSOLE_NAME);
		myConsole.clearConsole();
	}
	
	//@Override
	public void println(String line) {
		// TODO synchronize?

		// TODO:, listen for different project outputs
		String name = CONSOLE_NAME;
		final MessageConsole myConsole = getConsole(name);
		
		MessageConsoleStream out = myConsole.newMessageStream();
		out.println(line);
		//myConsole.activate();
		if(true) return;

		final String id = IConsoleConstants.ID_CONSOLE_VIEW;
		
		SWTUtilExt.runInSWTThread(new Runnable() {
			//@Override
			public void run() {
				IConsoleView view;
				IWorkbenchPage page = DeePlugin.getActivePage();
				view = (IConsoleView) page.findView(id);
				try {
					if(false)
						view = (IConsoleView) page.showView(id);
				} catch (PartInitException e) {
					throw ExceptionAdapter.unchecked(e);
				}
				if(view != null) {
					//view.display(myConsole);
				}
			}
		});
	}



}
