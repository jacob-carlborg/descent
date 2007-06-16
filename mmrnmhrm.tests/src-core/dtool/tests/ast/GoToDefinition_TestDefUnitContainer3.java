package dtool.tests.ast;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class GoToDefinition_TestDefUnitContainer3 extends GoToDefinition_CommonTest  {
	
	static final String testfile = "refs/refDefUnitContainers3.d";
	
	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
        		{62, 146},
        		{65, 184},
        		{68, 214},
        		{72, 234},
        		{76, 288},
        		{79, 292},
        		{82, 320},

        });
    }
    
	@BeforeClass
	public static void classSetup() {
		prepClass(testfile);
	}
	
	public GoToDefinition_TestDefUnitContainer3(int defOffset, int refOffset) throws IOException {
		super(defOffset, refOffset, testfile);
	}
	


	@Test
	public void test() {
		assertGoToReF(defOffset, refOffset);
	}
}
