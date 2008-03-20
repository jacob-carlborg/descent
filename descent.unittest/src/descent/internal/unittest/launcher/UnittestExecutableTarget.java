package descent.internal.unittest.launcher;

import descent.launching.AbstractExecutableTarget;

public class UnittestExecutableTarget extends AbstractExecutableTarget
{
    // TODO
    private static final String FLUTE_MODULE_NAME = "org.dsource.descent.flute";
    private static final String FLUTE_IMPORT_PATH = "C:\\workspace\\descent.unittest\\flute\\src";
    
    private static final String[] DEFAULT_MODULES = new String[]
    {
        //TODO FLUTE_MODULE_NAME,
    };
    private static final String[] DEFAULT_IMPORT_PATH = new String[]
    {
        FLUTE_IMPORT_PATH,
    };
    
	@Override
	protected String[] getDefaultModules()
	{
		return DEFAULT_MODULES;
	}
    
    @Override
    public String[] getDefaultImportPath()
    {
        return DEFAULT_IMPORT_PATH;
    }



    @Override
    public boolean getAddAssertsAndContracts()
    {
        // assert() is often used in unit tests, return true
        return true;
    }

    @Override
    public boolean getAddDebugInfo()
    {
        // Flute needs symbolic debug info to find the unit test execution
        // addresses, return true
        return true;
    }

    @Override
    public boolean getAddUnittests()
    {
        // "im in ur code writing ur kawment" - Happycat, return true
        return true;
    }

    @Override
    public boolean getInlineFunctions()
    {
        // Inlined functions can mess up stack traces, return false
        return false;
    }

    @Override
    public boolean getInstrumentForCoverage()
    {
        // Code coverage of unit tetss will be handled elsewhere
        return false;
    }

    @Override
    public boolean getInstrumentForProfile()
    {
        // Profiling + unit testsing will be handled elsewhere
        return false;
    }

    @Override
    public boolean getOptimizeCode()
    {
        // Optimizing makes comnpile times longer and can mess up debugging,
        // return false
        return false;
    }
    
    
}
