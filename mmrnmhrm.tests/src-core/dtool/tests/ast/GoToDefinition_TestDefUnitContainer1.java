package dtool.tests.ast;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class GoToDefinition_TestDefUnitContainer1 extends GoToDefinition_CommonTest  {
	
	static final String testfile = "refs/refDefUnitContainers1.d";
	
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
    
	@BeforeClass
	public static void classSetup() {
		prepClass(testfile);
	}
	
	public GoToDefinition_TestDefUnitContainer1(int defOffset, int refOffset) throws IOException, CoreException {
		super(defOffset, refOffset, testfile);
	}
	


	@Test
	public void test() {
		assertGoToReF(defOffset, refOffset);
	}
}
