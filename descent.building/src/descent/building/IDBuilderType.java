package descent.building;

import org.eclipse.core.runtime.CoreException;

/**
 * Defines an interface for getting info about a builder type registered with
 * the plugin.
 * 
 * @author Robert Fraser
 */
public interface IDBuilderType
{
    /**
     * Gets the unique identifier of the builder
     * 
     * @return the 'id' property of the extension
     */
    public String getIdentifier();
    
    /**
     * Gets the launch configuration type associated with this builder
     * 
     * @return the 'launchConfigurationType' property of the extension
     */
    public String getLaunchConfigurationType();
    
    /**
     * Gets a new instance of the builder class
     * 
     * @return               a new instance of the builder type
     * @throws CoreException if there was a problem constructing the new builder
     *                       instance
     */
    public IDBuilder getBuilder() throws CoreException;
}
