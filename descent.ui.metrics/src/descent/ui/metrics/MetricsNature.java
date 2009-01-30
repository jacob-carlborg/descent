package descent.ui.metrics;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;


public final class MetricsNature implements IProjectNature {
    public static final String NATURE_ID = "descent.ui.metrics.MetricsNature";

    private IProject project;

    public void configure() throws CoreException {
        IProjectDescription description = project.getDescription();
        ICommand[] commands = description.getBuildSpec();
        if (locateBuilderInArray(commands) != -1) {
            return;
        }

        addBuilderToCommands(description, commands);
    }

    private void addBuilderToCommands(IProjectDescription description, ICommand[] commands) throws CoreException {
        ICommand command = description.newCommand();
        command.setBuilderName(MetricsBuilder.BUILDER_ID);
        ICommand[] newCommands = new ICommand[commands.length + 1];
        System.arraycopy(commands, 0, newCommands, 0, commands.length);
        newCommands[commands.length] = command;

        description.setBuildSpec(newCommands);
        project.setDescription(description, null);
    }

    public void deconfigure() throws CoreException {
        removeBuilder();
        removeMarkers();
    }

    public void setProject(IProject newProject) {
        project = newProject;
    }
    
    public IProject getProject() {
        return project;
    }

    private void removeBuilder() throws CoreException {
        IProjectDescription description = project.getDescription();
        ICommand[] commands = description.getBuildSpec();
        int builderIndex = locateBuilderInArray(commands);
        if (builderIndex != -1) {
            commands = removeBuilderFromCommands(commands, builderIndex);
            description.setBuildSpec(commands);
            project.setDescription(description, null);
        }
    }

    private void removeMarkers() throws CoreException {
        project.deleteMarkers(MetricsBuilder.MARKER_ID, true, IResource.DEPTH_INFINITE);
    }

    private int locateBuilderInArray(ICommand[] commands) {
        for (int i = 0; i < commands.length; i++) {
            if (commands[i].getBuilderName().equals(MetricsBuilder.BUILDER_ID)) {
                return i;
            }
        }

        return -1;
    }

    private ICommand[] removeBuilderFromCommands(ICommand[] currentCommands, int builderIndex) {
        ICommand[] newCommands = new ICommand[currentCommands.length - 1];
        if (builderIndex > 0) {
            System.arraycopy(currentCommands, 0, newCommands, 0, builderIndex);
        }

        if (builderIndex < currentCommands.length - 1) {
            System.arraycopy(currentCommands, builderIndex + 1, newCommands, builderIndex, currentCommands.length - builderIndex);
        }

        return newCommands;
    }
}
