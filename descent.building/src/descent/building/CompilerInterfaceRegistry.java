package descent.building;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

import descent.building.compiler.ICompilerInterface;
import descent.internal.building.BuildingPlugin;
import descent.launching.IVMInstallType;

/**
 * Class to get information about what compilers are configured for use with the
 * default D builder.
 * 
 * @author Robert Fraser
 */
public final class CompilerInterfaceRegistry
{
    private static final class CompilerInterfaceType 
            implements ICompilerInterfaceType
    {
        private static final String ATTR_ID = "id"; //$NON-NLS-1$
        private static final String ATTR_CLASS = "class"; //$NON-NLS-1$
        private static final String ATTR_VM_INSTALL_TYPE = "vmInstallType"; //$NON-NLS-1$
        
        private final IConfigurationElement info;
        private ICompilerInterface compilerInterfaceInstance;
        
        public CompilerInterfaceType(IConfigurationElement info)
        {
            this.info = info;
        }

        public String getIdentifier()
        {
            return info.getAttribute(ATTR_ID);
        }

        public String getVMInstallType()
        {
            return info.getAttribute(ATTR_VM_INSTALL_TYPE);
        }
        
        public synchronized ICompilerInterface getCompilerInterface() throws CoreException
        {
            if(compilerInterfaceInstance == null)
                compilerInterfaceInstance = (ICompilerInterface) info.createExecutableExtension(ATTR_CLASS);
            
            return compilerInterfaceInstance;
        }
    }
    
    private List<CompilerInterfaceType> compilerInterfaces;
    
    public ICompilerInterfaceType[] getCompilerInterfaces()
    {
        if(null == compilerInterfaces)
            loadCompilerInterfaces();
        
        return compilerInterfaces.toArray(
                new ICompilerInterfaceType[compilerInterfaces.size()]);
    }
    
    public ICompilerInterfaceType getCompilerInterfaceById(String id)
    {
        if(null == compilerInterfaces)
            loadCompilerInterfaces();
        
        for(ICompilerInterfaceType compilerInterface : compilerInterfaces)
        {
            if(compilerInterface.getIdentifier().equals(id))
                return compilerInterface;
        }
        return null;
    }
    
    public ICompilerInterfaceType getCompilerInterfaceByVMInstallType
        (IVMInstallType vmInstallType)
    {
        return getCompilerInterfaceByVMInstallType(vmInstallType.getId());
    }
    
    public ICompilerInterfaceType getCompilerInterfaceByVMInstallType
        (String vmInstallTypeId)
    {
        if(null == compilerInterfaces)
            loadCompilerInterfaces();
        
        for(ICompilerInterfaceType compilerInterface : compilerInterfaces)
        {
            if(compilerInterface.getVMInstallType().equals(vmInstallTypeId))
                return compilerInterface;
        }
        return null;
    }
    
    private synchronized void loadCompilerInterfaces()
    {
        if(null != compilerInterfaces)
            return;
        
        compilerInterfaces = new ArrayList<CompilerInterfaceType>();
        IExtensionPoint extensionPoint = Platform.getExtensionRegistry().
                getExtensionPoint(BuildingPlugin.PLUGIN_ID,
                BuildingPlugin.ID_EXTENSION_POINT_COMPILER_INTERFACES);
        IConfigurationElement[] infos = extensionPoint.getConfigurationElements();
        
        for(IConfigurationElement info : infos)
        {
            CompilerInterfaceType compilerInterface = new CompilerInterfaceType(info);
            compilerInterfaces.add(compilerInterface);
        }
    }
    
    //--------------------------------------------------------------------------
    // Instance management
    
    private static CompilerInterfaceRegistry instance;
    
    /**
     * Gets the instance of the singleton.
     * 
     * @return the singleton instance
     */
    public static synchronized CompilerInterfaceRegistry getInstance()
    {
        if(null == instance)
            instance = new CompilerInterfaceRegistry();
        
        return instance;
    }
    
    private CompilerInterfaceRegistry()
    {
        // Should not be called outside this class
    }
}
