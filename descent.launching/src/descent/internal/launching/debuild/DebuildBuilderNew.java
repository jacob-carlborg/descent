package descent.internal.launching.debuild;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;

import descent.launching.IDBuilder;

public class DebuildBuilderNew implements IDBuilder
{
    //--------------------------------------------------------------------------
    // New builder thing
    
    public String build(ILaunchConfiguration config, IProgressMonitor pm)
    {
        System.out.println("DebuildBuilder invoked!");
        return null;
    }
}
