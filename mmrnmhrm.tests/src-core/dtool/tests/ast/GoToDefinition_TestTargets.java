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
public class GoToDefinition_TestTargets extends GoToDefinition_CommonTest  {
	
	static final String testfile = "refs/refTargets.d";
	
	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {19, 7},
                {67, 49},
                {97, 88},
                {134, 127},
                {251, 153},
                {221, 166},
                {201, 189},
                {312, 269},
                {345, 288},
                {362, 301},
                {429, 382}, //10
                {474, 405},
                {495, 418},
                {556, 512},
                {592, 532},
                {610, 545},
                {669, 626},
                {702, 645},
                {719, 658},
                {761, 734},
                {791, 741}, //20
                {806, 750},
                {1088, 826},
                {944, 837},
                {990, 862},
                {1025, 890},
                {1065, 911},
                // template
                {1173, 1138},
                {1181, 1145},
                {1189, 1152},
                {1233, 1214},
                
                // Special symbols
                //{1293, 1260},
                //{1315, 1280},
                //{1360, 1363},
                //{1378, 1379},
                //{1421, 1404},
        });
    }
    
	@BeforeClass
	public static void classSetup() throws IOException {
		counter = -1;
	}
	        
	public GoToDefinition_TestTargets(int defOffset, int refOffset) throws IOException  {
		super(defOffset, refOffset, testfile);
	}
	  
	
	@Test
	public void test() {
		assertGoToReF(defOffset, refOffset);
	}
	


}
