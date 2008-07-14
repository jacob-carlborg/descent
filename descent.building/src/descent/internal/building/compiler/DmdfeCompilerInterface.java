package descent.internal.building.compiler;

import descent.building.compiler.IBuildManager;
import descent.building.compiler.ICompileManager;
import descent.building.compiler.ICompilerInterface;

public abstract class DmdfeCompilerInterface implements ICompilerInterface
{
    public ICompileManager getCompileManager(IBuildManager buildManager)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean supportsInternalDependancyAnalysis()
    {
        return true;
    }
}
