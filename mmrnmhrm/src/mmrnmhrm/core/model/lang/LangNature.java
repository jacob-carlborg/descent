package mmrnmhrm.core.model.lang;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

import util.ArrayUtil;
import util.Assert;

public abstract class LangNature implements IProjectNature {
	
	public IProject project;

	/** {@inheritDoc} */
	public IProject getProject() {
		return project;
	}

	/** {@inheritDoc} */
	public void setProject(IProject project) {
		this.project = project;
	}

	/** Adds a nature to the given project. Nature must not exist already. */
	public static void addNature(IProject project, String natureID) throws CoreException {
		IProjectDescription description = project.getDescription();
		String[] natures = description.getNatureIds();
		Assert.isTrue(ArrayUtil.contains(natures, natureID) == false);
		String[] newNatures = ArrayUtil.append(natures, natureID);
		description.setNatureIds(newNatures);
		project.setDescription(description, null); 
	}

	/** Adds a builder to the build spec of this project, if the
	 * builder doesn't exist already. */
	protected void addToBuildSpec(String builderID) throws CoreException {
		IProjectDescription description = project.getDescription();
		ICommand[] commands = description.getBuildSpec();
	
		if (getBuildCommandIndex(commands, builderID) == -1) {
			// Adds the builder to the build spec.
			ICommand command = description.newCommand();
			command.setBuilderName(builderID);
	
			description.setBuildSpec(ArrayUtil.append(commands, command));
			project.setDescription(description, null);
		}
		
	}
	
	/** Find the specific build command in the given build spec.
	 * Returns it's index or -1 if not found. */
	int getBuildCommandIndex(ICommand[] buildSpec, String builderID) {
		for (int i = 0; i < buildSpec.length; ++i) {
			if (buildSpec[i].getBuilderName().equals(builderID)) {
				return i;
			}
		}
		return -1;
	}

	/** Removes the given builder from the build spec in this project. */
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