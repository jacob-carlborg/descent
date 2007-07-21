package mmrnmhrm.tests.core;

import java.util.Arrays;
import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FindDef_TestSelfImport extends FindDef_CommonImportTest  {
	
	static final String testSrcFile = "testSelfImport1.d";

	@BeforeClass
	public static void classSetup() {
		staticTestInit(testSrcFile);
	}
	
	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{

                {"testSelfImport1.d", 120, 77, "testSelfImport1.d"},
                {"testSelfImport1.d", 178, 77, "testSelfImport1.d"},
                {"testSelfImport1.d", 194, 101, "testSelfImport1.d"},
                
                {"testSelfImport2.d", 104, 77, "testSelfImport2.d"},
                {"testSelfImport2.d", 181, 77, "testSelfImport2.d"},
                {"testSelfImport2.d", 225, 77, "testSelfImport2.d"},
                {"testSelfImport2.d", 241, 162, "testSelfImport2.d"},
             
                {"testSelfImport3.d", 114, 77, "testSelfImport3.d"},
                {"testSelfImport3.d", 162, 77, "testSelfImport3.d"},
                {"testSelfImport3.d", 211, 77, "testSelfImport3.d"},
                {"testSelfImport3.d", 227, 138, "testSelfImport3.d"},
        });
    }
    
	
	public FindDef_TestSelfImport(String srcFile, int defOffset, int refOffset, String targetFile) 
		throws Exception {
		super(getCompilationUnit(srcFile), defOffset, refOffset, targetFile);
	}
	
	
}