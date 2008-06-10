package descent.internal.building.compiler;

import descent.building.compiler.ICompileCommand;
import descent.building.compiler.ICompilerInterface;
import descent.building.compiler.ILinkCommand;
import descent.building.compiler.IResponseInterpreter;

public abstract class DmdfeCompilerInterface implements ICompilerInterface
{
    public ICompileCommand createCompileCommand()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public IResponseInterpreter createCompileResponseInterpreter()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public ILinkCommand createLinkCommand()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public IResponseInterpreter createLinkResponseInterpreter()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
