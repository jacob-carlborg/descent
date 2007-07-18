package mmrnmhrm.tests.core;

import java.util.Arrays;
import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FindDef_TestDiamondShaped extends FindDef_CommonImportTest  {
	
	static final String testSrcFile = "testDiamondShaped.d";

	@BeforeClass
	public static void classSetup() {
		staticTestInit(testSrcFile);
	}
	
	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{

                {195, 13, "pack2/foopublic.d"},
                {231, 13, "pack2/foopublic.d"},
                
                {269, 13, "pack2/foopublic2.d"},
                {307, 13, "pack2/foopublic2.d"},
             
        });
    }
    
	
	public FindDef_TestDiamondShaped(int defOffset, int refOffset, String targetFile) 
		throws Exception {
		super(defOffset, refOffset, targetFile);
	}
	
}
