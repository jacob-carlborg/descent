package descent.launching;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;

public interface IDBuilder
{
    public String build(ILaunchConfiguration config, IProgressMonitor pm);
}
