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
public class GoToDefinition_TestDefUnitContainer1 extends FindDef_CommonTest  {
	
	static final String testfile = "refs/refDefUnitContainers1.d";
	
	@BeforeClass
	public static void classSetup() {
		staticTestInit(testfile);
	}
	
	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
        		{62, 137},
        		{65, 163},
        		{68, 191},
        		{71, 211},
        		{74, 231},
        		{77, 251},

        		{272, 137},
        		{275, 163},
        		{278, 191},
        		{281, 211},
        		{284, 231},
        		{287, 251},
        });
    }
    

	
	public GoToDefinition_TestDefUnitContainer1(int offset, int targetOffset) throws IOException, CoreException {
		super(offset, targetOffset, testfile);
	}
	

}