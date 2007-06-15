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
public class GoToDefinition_TestKinds1 extends GoToDefinition_CommonTest  {
	
	static final String testfile = "refs/refKinds.d";
	
	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {198, 14},
                {230, 4},
                {258, 14},
                {291, 4},
                {334, 124},
                {367, 127},
                {0, 0},
                {0, 0},
                {0, 0},
                {0, 0},
                {0, 0},

        });
    }
    
	public GoToDefinition_TestKinds1(int defOffset, int refOffset) throws IOException {
		super(defOffset, refOffset, testfile);
	}
	
	@BeforeClass
	public static void classSetup() throws IOException {
		counter = -1;
	}

	@Test
	public void test() {
		assertGoToReF(defOffset, refOffset);
	}
}
