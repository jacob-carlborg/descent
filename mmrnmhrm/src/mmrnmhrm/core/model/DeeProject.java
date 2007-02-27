package mmrnmhrm.core.model;

import mmrnmhrm.core.DeeCore;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class DeeProject implements IProjectNature {

	public static final String NATURE_ID = "deenature";
	public static final String NATURE_FQID = DeeCore.PLUGIN_ID +"."+ NATURE_ID;

	private IProject project;

	/** {@inheritDoc} */
	public IProject getProject() {
		return project;
	}

	/** {@inheritDoc} */
	public void setProject(IProject project) {
		this.project = project;
	}

	/** {@inheritDoc} */
	public void configure() throws CoreException {
		IProjectDescription desc = project.getDescription();
		ICommand[] commands = desc.getBuildSpec();

		/*for(ICommand command : commands) {
			if (command.getBuilderName().equals(SampleBuilder.BUILDER_ID))
				return;
		}

		ICommand command = desc.newCommand();
		command.setBuilderName(SampleBuilder.BUILDER_ID);

		ICommand[] newCommands = ArrayUtil.append(commands, command);
		desc.setBuildSpec(newCommands);
		project.setDescription(desc, null);*/
		
	}

	/** {@inheritDoc} */
	public void deconfigure() throws CoreException {
		// TODO Auto-generated method stub
		
		IProjectDescription description = getProject().getDescription();
		ICommand[] commands = description.getBuildSpec();
		for (int i = 0; i < commands.length; ++i) {
/*			if (commands[i].getBuilderName().equals(SampleBuilder.BUILDER_ID)) {
				
				ICommand[] newCommands = ArrayUtil.removeAt(commands, i);
				description.setBuildSpec(newCommands);
				return;
			}
*/		}

	}

}
