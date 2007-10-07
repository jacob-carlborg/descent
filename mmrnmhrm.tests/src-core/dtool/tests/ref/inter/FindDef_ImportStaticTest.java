package dtool.tests.ref.inter;

import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FindDef_ImportStaticTest extends FindDef__ImportsCommon  {
	
	static final String testSrcFile = "testImportStatic.d";

	protected static int ix1, ix2, ix1end, ix2end;

	@BeforeClass
	public static void classSetup() {
		staticTestInit(testSrcFile);
		//String str = defaultCUnit.getDocument().get();
		ix1 = 77; //str.indexOf(TESTALT_KEY, 0);
		ix1end = 161; // Hardcoded value
		ix2 = 16; //str.indexOf(TESTALT_KEY, ix1+1);
		ix2end = 235; // Hardcoded value
	}
	
	@Parameters
    public static List<Object[]> data() {
        return Arrays.asList(new Object[][]{
        		
                {100, 12, "pack/mod1.d"}, // ALT 1 only
                {124, 12, "pack/mod2.d"}, // ALT 1 only
                {135, 12, "pack/sample.d"}, // ALT 1 only
                {156, 20, "pack/subpack/mod3.d"}, // ALT 1 only
                
                {251, 12, "pack/sample.d"},
                {279, 60, "pack/sample.d"},
                {335, 60, "pack/sample.d"},
                {347, 86, "pack/sample.d"},
                
                {370, 55, "pack2/foopublic.d"},
                {406, -1, null},
                {470, 55, "pack2/foopublic.d"}, // This is ugly D behavior
                {536, -1, null},
              
        });
    }
    
	
	public FindDef_ImportStaticTest(int defOffset, int refOffset, String targetFile) 
		throws Exception {
		super(defOffset, refOffset, targetFile);
	}

	@Test
	@Override
	public void test() throws Exception {
		sourceModule.getModuleUnit().getBuffer().replace(ix1, 4, "    ");
		sourceModule.getModuleUnit().getBuffer().replace(ix2, 4, "//  ");
		sourceModule.getModuleUnit().reconcile(false, null, null);
		super.test();
	}
	
}
