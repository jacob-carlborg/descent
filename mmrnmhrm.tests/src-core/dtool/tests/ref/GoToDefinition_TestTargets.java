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
public class GoToDefinition_TestTargets extends FindDef_CommonTest  {
	
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
                //{201, 189}, // TODO out(result)
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
                // template
                {1088, 826},
                {944, 837},
                {990, 862},
                {1025, 890},
                {1065, 911},

                {1173, 1138},
                {1181, 1145},
                {1189, 1152},
                
                // TODO: support these reftargets
                //{1233, 1214},
                
                // agreggate templates
                //{1272, 1267},
                //{1277, 1267},
                //{1312, 1306},
                
        });
    }
    
	@BeforeClass
	public static void classSetup() {
		staticTestInit(testfile);
	}
	        
	public GoToDefinition_TestTargets(int offset, int targetOffset) throws IOException, CoreException  {
		super(offset, targetOffset, testfile);
	}
	  
	
}