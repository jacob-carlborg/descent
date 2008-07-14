package descent.internal.building.debuild;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;

import descent.internal.building.BuilderUtil;

/**
 * The incremental consistency manager class is responsible for maintaining
 * consistency between successive builds. Given a build request, it ensures
 * that any existing object files in the output folder that need to be rebuilt
 * (for example, if they need to be built with symbolic debug information on,
 * while a previous compile had symbolic debug off) will be deleted prior to
 * invoking the builder proper.
 * 
 * Note that the incremental consistency manager is NOT responsible for deleting
 * objects of individual files that need to be recompiled due to changes to their
 * source input file, only object files whose code is inconsistent with the build
 * configuration settings. Input files should not be considered when deciding
 * which files to delete, this management is done selectively post-dependancy-
 * analysis.
 * 
 * (TODO right now it doesn't even do this)
 * 
 * This implementation is very naive and simply clears the output folder if
 * the launch configuration changed at all. Future versions may do this more
 * intelligently (for example, if changes were made to warning settings, this
 * will not affect whether object files are still valid.)
 * 
 * @author Robert Fraser
 */
/* package */ final class IncrementalConsistencyManager
{
    private final BuildRequest req;
    
    /**
     * Creates the consistency manager for a build
     * 
     * @param req
     */
    public IncrementalConsistencyManager(BuildRequest req)
    {
        this.req = req;
    }
    
    /**
     * Runs the consistency manager.
     */
    public void checkConsistentState()
    {
        try
        {
            // TODO
            if(true)
            {
                IFolder folder = req.getOutputResource();
                if(!folder.exists())
                    folder.create(true, true, BuilderUtil.NO_MONITOR);
                clearOutputFolder();
            }
            else
            {
                // TODO this doesn't work
                
                // Check if the folder exists
                IFolder folder = req.getOutputResource();
                if(!folder.exists())
                {
                    folder.create(true, true, BuilderUtil.NO_MONITOR);
                    createConfigFile();
                    return;
                }
    
                folder.refreshLocal(IResource.DEPTH_INFINITE, BuilderUtil.NO_MONITOR);
                IResource launchConfigFile = folder.findMember(getLaunchConfigFilename());
                if(null == launchConfigFile || !(launchConfigFile instanceof IFile))
                {
                    System.out.println("File doesn't exist!");
                    clearOutputFolder();
                    createConfigFile();
                    return;
                }
                
                ILaunchConfiguration launchConfig = DebugPlugin.getDefault().
                        getLaunchManager().getLaunchConfiguration((IFile) launchConfigFile);
                if(!launchConfigsEqual(launchConfig, req.getLaunchConfig()))
                {
                    System.out.println("Not equal!");
                    clearOutputFolder();
                    createConfigFile();
                    return;
                }
                
                // If we get here, we can safely use any object files already
                // generated for incremental compilation.
                System.out.println("Contents saved!!!");
            }
        }
        catch(CoreException e)
        {
            throw new DebuildException(String.format(
                    "Error preparing output folder: %1$s", e.getMessage()));
        }
        
    }
    
    /**
     * Gets the file name of the target launch configuration.
     */
    private String getLaunchConfigFilename()
    {
        return req.getLaunchConfig().getName().concat(".").
                concat(ILaunchConfiguration.LAUNCH_CONFIGURATION_FILE_EXTENSION);
    }

    /**
     * Copes the original launch configuration to the target location
     */
    private void createConfigFile() throws CoreException
    {
        IFile src = req.getLaunchConfig().getFile();
        IFile dst = req.getOutputResource().getFile(getLaunchConfigFilename());
        src.copy(dst.getFullPath(), true, BuilderUtil.NO_MONITOR);
    }
    
    /**
     * Clears the output folder of all resources.
     */
    private void clearOutputFolder() throws CoreException
    {
        System.out.println("Folder cleared!!!");
        
        IFolder outputFolder = req.getOutputResource();
        IResource[] members = outputFolder.members();
        for(IResource file : members)
        {
            try
            {
                file.delete(true, BuilderUtil.NO_MONITOR);
            }
            catch(CoreException e)
            {
                throw new DebuildException(String.format(
                        "Error deleting resource %1$s: %2$s", 
                        file.getFullPath().toString(), e.getMessage()));
            }
        }
    }
    
    /**
     * This method is needed since 
     * {@link ILaunchConfiguration#contentsEqual(ILaunchConfiguration)} compares
     * the paths of launch configurations as well, and the getInfo method is not
     * publicly available.
     */
    private static boolean launchConfigsEqual(ILaunchConfiguration config,
            ILaunchConfiguration other) throws CoreException
    {
        return config.getName().equals(other.getName()) &&
                config.getType().equals(other.getType()) &&
                config.getAttributes().equals(other.getAttributes());
    }
    
    // NEXTVERSION make this smarter
}
