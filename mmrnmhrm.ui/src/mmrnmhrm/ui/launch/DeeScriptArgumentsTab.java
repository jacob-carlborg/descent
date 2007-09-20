/**
 * 
 */
package mmrnmhrm.ui.launch;

import java.util.List;

import melnorme.miscutil.ExceptionAdapter;
import melnorme.miscutil.StringUtil;
import mmrnmhrm.core.build.BudDeeModuleCompiler;
import mmrnmhrm.core.launch.DeeLaunchConfigurationDelegate;
import mmrnmhrm.core.model.DeeModel;
import mmrnmhrm.core.model.DeeProjectOptions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.debug.ui.launchConfigurations.ScriptArgumentsTab;
import org.eclipse.dltk.internal.debug.ui.launcher.InterpreterArgumentsBlock;
import org.eclipse.swt.widgets.Composite;

final class DeeScriptArgumentsTab extends ScriptArgumentsTab {
	
	private final class DeeInterpreterArgumentsBlock extends
			InterpreterArgumentsBlock {
		
		@Override
		public void initializeFrom(ILaunchConfiguration configuration) {
			super.initializeFrom(configuration);
			IScriptProject deeProj;
			try {
				deeProj = DeeLaunchConfigurationDelegate.getScriptProject(configuration);
			} catch (CoreException e) {
				// TO DO: check the exception
				throw ExceptionAdapter.unchecked(e);
			}
			DeeProjectOptions deeProjectInfo = DeeModel.getDeeProjectInfo(deeProj);
			List<String> cmdLine = BudDeeModuleCompiler.createCommandLine(
					null, deeProj, deeProjectInfo.compilerOptions);
			fInterpreterArgumentsText.setText(
					StringUtil.collToString(cmdLine, " "));

		}
	}

	@Override
	protected InterpreterArgumentsBlock createInterpreterArgsBlock() {
		InterpreterArgumentsBlock interpreterArgsBlock = new DeeInterpreterArgumentsBlock();
		return interpreterArgsBlock;
	}
	
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		fInterpreterArgumentsBlock.setEnabled(false);
	}
	
}