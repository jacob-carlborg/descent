package dtool.tests.ast;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

// TODO test special defunits (not a priority)


@RunWith(Parameterized.class)
public class GoToDefinition_TestTargets2 extends GoToDefinition_CommonTest  {
	
	static final String testfile = "refs/refTargets2.d";
	
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
		prepClass(testfile);
	}
	        
	public GoToDefinition_TestTargets2(int defOffset, int refOffset) throws IOException  {
		super(defOffset, refOffset, testfile);
	}
	  
	
	@Test
	public void test() {
		assertGoToReF(defOffset, refOffset);
	}
	
}
