package descent.building;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

import descent.internal.building.BuildingPlugin;

/**
 * Class used to get information about what types of D builders have been configured
 * for use with Descent.
 * 
 * @author Robert Fraser
 */
public final class BuilderRegistry
{
    private static final class DBuilderType implements IDBuilderType
    {
        // Attributes defined in the schema
        private static final String ATTR_ID = "id";
        private static final String ATTR_CLASS = "class";
        private static final String ATTR_LAUNCH_CONFIGURATION_TYPE = "launchConfigurationType";
        
        private final IConfigurationElement info;

        private DBuilderType(IConfigurationElement info)
        { 
            this.info = info;
        }
        
        /* (non-Javadoc)
         * @see descent.launching.IDBuilderType#getBuilder()
         */
        public IDBuilder getBuilder() throws CoreException
        {
            return (IDBuilder) info.createExecutableExtension(ATTR_CLASS);
        }
        
        /* (non-Javadoc)
         * @see descent.launching.IDBuilderType#getIdentifier()
         */
        public String getIdentifier()
        {
            return info.getAttribute(ATTR_ID);
        }
        
        /* (non-Javadoc)
         * @see descent.launching.IDBuilderType#getLaunchConfigurationType()
         */
        public String getLaunchConfigurationType()
        {
            return info.getAttribute(ATTR_LAUNCH_CONFIGURATION_TYPE);
        }
    }
    
    private List<DBuilderType> builders;
    
    /**
     * Gets info about all the builders registered with this plugin
     * 
     * @return the list of all builders associated with this plugin
     */
    public IDBuilderType[] getBuilders()
    {
        if(null == builders)
            loadBuilders();
        
        return builders.toArray(new IDBuilderType[builders.size()]);
    }
    
    /**
     * Gets the builder associated with the given id. I'm not sure if Eclipse
     * allows multiple things to have the same "id" property, but if it does,
     * that would be an undefined condition for this method.
     * 
     * @param id the id to look up
     * @return   the builder associated with the id or <code>null</code> if none
     *           was found
     */
    public IDBuilderType getBuilderById(String id)
    {
        if(null == builders)
            loadBuilders();
        
        for(IDBuilderType builder : builders)
        {
            if(builder.getIdentifier().equals(id))
                return builder;
        }
        return null;
    }
    
    /**
     * Gets the builder associated with the given launch configuration type. Note
     * that if multiple builders are associated with the same launch configuration
     * type, the one which is returned is undefined, so don't let this happen.
     * 
     * @param launchConfigurationType the launch configuration type to look up
     * @return                        the associated builder or <code>null</code>
     *                                if none was found
     */
    public IDBuilderType getBuilderForLaunchConfigurationType(
            String launchConfigurationType)
    {
        if(null == builders)
            loadBuilders();
        
        for(IDBuilderType builder : builders)
        {
            if(builder.getLaunchConfigurationType().equals(launchConfigurationType))
                return builder;
        }
        return null;
    }
    
    /**
     * Gets the builder type associated with the default descent builder
     * 
     * @return the builder type associated with the default descent builder
     */
    public IDBuilderType getDescentBuilder()
    {
        return getBuilderById(IDescentBuilderConstants.ID_DESCENT_BUILDER);
    }
    
    private synchronized void loadBuilders()
    {
        if(null != builders)
            return;
        
        builders = new ArrayList<DBuilderType>();
        IExtensionPoint extensionPoint = Platform.getExtensionRegistry().
                getExtensionPoint(BuildingPlugin.PLUGIN_ID,
                BuildingPlugin.ID_EXTENSION_POINT_D_BUILDERS);
        IConfigurationElement[] infos = extensionPoint.getConfigurationElements();
        
        for(IConfigurationElement info : infos)
        {
            DBuilderType builder = new DBuilderType(info);
            builders.add(builder);
        }
    }
    
    //--------------------------------------------------------------------------
    // Instance management
    
    private static BuilderRegistry instance;
    
    /**
     * Gets the instance of the singleton.
     * 
     * @return the singleton instance
     */
    public static synchronized BuilderRegistry getInstance()
    {
        if(null == instance)
            instance = new BuilderRegistry();
        
        return instance;
    }
    
    private BuilderRegistry()
    {
        // Should not be called outside this class
    }
}
