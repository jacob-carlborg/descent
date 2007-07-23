package dtool.tests.ref;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

// TODO test special defunits (not a priority)


@RunWith(Parameterized.class)
public class GoToDefinition_TestTargets2 extends FindDef_CommonTest  {
	
	static final String testfile = "refTargets2.d";
	
	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                
                // Special symbols
                //{1293, 1260},
                //{1315, 1280},
                //{1360, 1363},
                //{1378, 1379},
                //{1421, 1404},
        });
    }
    
	@BeforeClass
	public static void classSetup() {
		staticTestInit(testfile);
	}
	        
	public GoToDefinition_TestTargets2(int offset, int targetOffset) throws IOException, CoreException  {
		super(offset, targetOffset, testfile);
	}
	  

}
