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
public class GoToDefinition_TestDefUnitContainer2 extends FindDef_CommonTest  {
	
	static final String testfile = "refs/refDefUnitContainers2.d";
    
	@BeforeClass
	public static void classSetup() {
		staticTestInit(testfile);
	}
	
	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
        		{62, 139},
        		{65, 163},
        		{68, 190},
        		{71, 209},
        		{74, 227},
        		{77, 246},

        		{272, 139},
        		{275, 163},
        		{278, 190},
        		{281, 209},
        		{284, 227},
        		{287, 246},
        });
    }
	
	public GoToDefinition_TestDefUnitContainer2(int offset, int targetOffset) throws IOException, CoreException {
		super(offset, targetOffset, testfile);
	}
	

}
