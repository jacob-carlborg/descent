package mmrnmhrm.core.launch;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mmrnmhrm.core.model.DeeNature;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.dltk.launching.AbstractScriptLaunchConfigurationDelegate;
import org.eclipse.dltk.launching.InterpreterConfig;

public class DeeLaunchConfiguration extends AbstractScriptLaunchConfigurationDelegate {

	public static class DeeInterpreterConfig extends InterpreterConfig {
		public DeeInterpreterConfig(File mainScript, File workingDirectory) {
			super(mainScript, workingDirectory); 
		}

		@SuppressWarnings("unchecked")
		@Override
		public String[] renderCommandLine(String exe) {
			List<String> items = new ArrayList<String>();

			//items.add(exe);

			Iterator<String> it;
			/*// Interpreter arguments
			Iterator it = interpreterArgs.iterator();
			while (it.hasNext()) {
				items.add(it.next());
			}*/

			// Script file
			items.add(getScriptFile().toString());

			// Script arguments
			it = ((List<String>) getScriptArgs()).iterator();
			while (it.hasNext()) {
				items.add(it.next());
			}

			return (String[]) items.toArray(new String[items.size()]);
		}
	}
	
	@Override
	public String getLanguageId() {
		return DeeNature.NATURE_ID;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected InterpreterConfig createInterpreterConfig(
			ILaunchConfiguration configuration, ILaunch launch)
			throws CoreException {

		// Validation already included
		final File mainScript = new File(getScriptLaunchPath(configuration));
		final File workingDirectory = getWorkingDirectory(configuration);

		InterpreterConfig config = new DeeInterpreterConfig(mainScript,
				workingDirectory);

		// Script arguments
		String[] scriptArgs = getScriptArguments(configuration);
		config.addScriptArgs(scriptArgs);

		// Interpreter argument
		String[] interpreterArgs = getInterpreterArguments(configuration);
		config.addInterpreterArgs(interpreterArgs);

		// Environment
//		config.addEnvVars(DebugPlugin.getDefault().getLaunchManager()
//				.getNativeEnvironmentCasePreserved());
		Map configEnv = configuration.getAttribute(
				ILaunchManager.ATTR_ENVIRONMENT_VARIABLES, new HashMap());
		// build base environment
		Map env = DebugPlugin.getDefault().getLaunchManager()
				.getNativeEnvironmentCasePreserved();
		boolean append = configuration.getAttribute(
				ILaunchManager.ATTR_APPEND_ENVIRONMENT_VARIABLES, true);
		if (configEnv != null) {
			for (Iterator iterator = configEnv.keySet().iterator(); iterator
					.hasNext();) {
				String name = (String) iterator.next();
				if (!env.containsKey(name) || !append) {
					env.put(name, configEnv.get(name));
				}
			}
		}
		config.addEnvVars(env);

		return config;
	}


}
