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
public class GoToDefinition_TestKinds2 extends GoToDefinition_CommonTest  {
	
	static final String testfile = "refs/refKinds.d";
	
	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {198, 14},

        });
    }
    
	@BeforeClass
	public static void classSetup() throws IOException {
		counter = -1;
	}
 	        
	public GoToDefinition_TestKinds2(int defOffset, int refOffset) throws IOException {
		super(defOffset, refOffset, testfile);
	}
	


	@Test
	public void test() {
		assertGoToReF(defOffset, refOffset);
	}
}
