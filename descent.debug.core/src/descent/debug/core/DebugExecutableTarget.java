package descent.debug.core;

import descent.launching.AbstractExecutableTarget;

public class DebugExecutableTarget extends AbstractExecutableTarget
{
    @Override
    public boolean getAddAssertsAndContracts()
    {
        return true;
    }

    @Override
    public boolean getAddDebugInfo()
    {
        return true;
    }

    @Override
    public boolean getAddUnittests()
    {
        return false;
    }

    @Override
    public boolean getInlineFunctions()
    {
        return false;
    }

    @Override
    public boolean getInstrumentForCoverage()
    {
        return false;
    }

    @Override
    public boolean getOptimizeCode()
    {
        return false;
    }

}
