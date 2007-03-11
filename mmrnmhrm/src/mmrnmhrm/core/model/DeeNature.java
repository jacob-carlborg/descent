package mmrnmhrm.core.model;

import mmrnmhrm.core.DeeCore;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

import util.ArrayUtil;

public class DeeNature implements IProjectNature {

	public static final String NATURE_ID = "deenature";
	public static final String NATURE_FQID = DeeCore.PLUGIN_ID +"."+ NATURE_ID;

	public IProject project;

	
	public DeeNature() {
	}
	
	/** {@inheritDoc} */
	public IProject getProject() {
		return project;
	}

	/** {@inheritDoc} */
	public void setProject(IProject project) {
		this.project = project;
	}

	/** Configure the project with a Dee nature. */
	public void configure() throws CoreException {
		addToBuildSpec(DeeCore.BUILDER_ID);
	}
	
	/** Remove the Dee nature from the project. */
	public void deconfigure() throws CoreException {
		removeFromBuildSpec(DeeCore.BUILDER_ID);
	}

	/** Adds a builder to the build spec for this project. */
	protected void addToBuildSpec(String builderID) throws CoreException {
		IProjectDescription description = project.getDescription();
		ICommand[] commands = description.getBuildSpec();

		if (getDeeCommandIndex(commands) == -1) {
			// Add a Dee command to the build spec
			ICommand command = description.newCommand();
			command.setBuilderName(builderID);

			description.setBuildSpec(ArrayUtil.append(commands, command));
			project.setDescription(description, null);
			
		}
	}
	
	/** Find the specific Dee command amongst the given build spec
	 * and return its index or -1 if not found. */
	private int getDeeCommandIndex(ICommand[] buildSpec) {
		for (int i = 0; i < buildSpec.length; ++i) {
			if (buildSpec[i].getBuilderName().equals(DeeCore.BUILDER_ID)) {
				return i;
			}
		}
		return -1;
	}

	
	/** Removes the given builder from the build spec for this project. */
	protected void removeFromBuildSpec(String builderID) throws CoreException {

		IProjectDescription description = project.getDescription();
		ICommand[] commands = description.getBuildSpec();
		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].getBuilderName().equals(builderID)) {
				ICommand[] newCommands = ArrayUtil.removeAt(commands, i); 
				description.setBuildSpec(newCommands);
				project.setDescription(description, null);
				return;
			}
		}
	}

}
