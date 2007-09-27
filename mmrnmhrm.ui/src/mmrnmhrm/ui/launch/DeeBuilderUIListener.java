package mmrnmhrm.ui.launch;

import static melnorme.miscutil.Assert.assertFail;

import java.io.IOException;

import melnorme.miscutil.ExceptionAdapter;
import melnorme.util.ui.swt.SWTUtilExt;
import mmrnmhrm.core.build.IDeeBuilderListener;
import mmrnmhrm.ui.DeePlugin;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import dtool.Logg;

public class DeeBuilderUIListener implements IDeeBuilderListener {

	//http://wiki.eclipse.org/FAQ_How_do_I_write_to_the_console_from_a_plug-in%3F
	
	private static final String CONSOLE_NAME = "mmrnmhrm Dee Build output:";


	public void clear() {
		final MessageConsole myConsole = ConsoleUtil.findConsole(CONSOLE_NAME);
		myConsole.clearConsole();
		Logg.main.println("Cleared console");
		Thread.yield();
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
		}
	}
	
	//@Override
	public void println(String line) {
		// TODO synchronize?

		// TODO:, listen for different project outputs
		String name = CONSOLE_NAME;
		MessageConsole myConsole =  ConsoleUtil.findConsole(name);
		MessageConsoleStream out = myConsole.newMessageStream();
		Logg.main.println("## Writing " + line);
		out.println(line);

		try {
			out.flush();
			out.close();
		} catch (IOException e) {
			assertFail();
		}
		
		myConsole.activate();
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
