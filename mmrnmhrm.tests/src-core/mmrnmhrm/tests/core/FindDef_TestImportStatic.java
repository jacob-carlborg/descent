package mmrnmhrm.tests.core;

import java.util.Arrays;
import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FindDef_TestImportStatic extends FindDef_CommonImportTest  {
	
	static final String testSrcFile = "testImportStatic.d";

	protected static int ix1, ix2;

	@BeforeClass
	public static void classSetup() {
		staticTestInit(testSrcFile);
		String str = defaultCUnit.getDocument().get();
		ix1 = str.indexOf(TESTALT_KEY, 0);
		ix2 = str.indexOf(TESTALT_KEY, ix1);
	}
	
	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{

                {100, 12, "pack/mod1.d"},
                {124, 12, "pack/mod2.d"},
                {135, 12, "pack/sample.d"},
                {156, 20, "pack/subpack/mod3.d"},

                {180, 12, "pack/mod1.d"},
                {198, 12, "pack/mod2.d"},
                {209, 12, "pack/sample.d"},
                {230, 20, "pack/subpack/mod3.d"},  
                
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
    
	
	public FindDef_TestImportStatic(int defOffset, int refOffset, String targetFile) 
		throws Exception {
		super(defOffset, refOffset, targetFile);
	}

	@Test
	public void test() throws Exception {
		cunit.getDocument().replace(ix1, 4, "    ");
		cunit.getDocument().replace(ix2, 4, "//  ");
		cunit.reconcile();
		super.test();
	}
	
	@Test
	public void test2() throws Exception {
		cunit.getDocument().replace(ix1, 4, "//  ");
		cunit.getDocument().replace(ix2, 4, "    ");
		cunit.reconcile();
		super.test();
	}
	
}
