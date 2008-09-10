package descent.building;

import org.eclipse.core.runtime.CoreException;

import descent.building.compiler.ICompilerInterface;

/**
 * Defines an interface for getting information about a compiler interface type.
 * This class is not intended to be instantiated outside the buidler framework,
 * instead you should create classes of type ]
 * {@link #descent.building.compiler.ICompilerInterface}
 * 
 * @author Robert Fraser
 */
public interface ICompilerInterfaceType
{
    /**
     * Gets the unique identifier of this compiler interface.
     */
    public String getIdentifier();
    
    /**
     * Gets the identifier of the {@link descent.launching.IVMInstallType} that
     * this is associated with
     */
    public String getVMInstallType();
    
    /**
     * Gets the shared instance of this compiler interface. Note that successive
     * calls to this on the same compielr interface type will return the same
     * instance.
     */
    public ICompilerInterface getCompilerInterface() throws CoreException;
    
    /**
     * Gets whether this compiler type reports that multiple instances of it may
     * be launched concurrently to speed up compilation.
     */
    public boolean getUseThreadPooling();
}
