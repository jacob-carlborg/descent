package dtool.tests.ref;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class GoToDefinition_TestScopes extends FindDef_CommonTest  {
	
	static final String testfile = "refScopes.d";
	
	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {106, 5},
                {162, 5},
                {356, 5},
                {393, 200},
                {431, 222},
                {533, 200},
                {571, 694},
                {614, 743},
        });
    }
    
	@BeforeClass
	public static void classSetup() {
		staticTestInit(testfile);
	}
	
	public GoToDefinition_TestScopes(int offset, int targetOffset) throws IOException, CoreException {
		super(offset, targetOffset, testfile);
	}	    
	 
	
}
