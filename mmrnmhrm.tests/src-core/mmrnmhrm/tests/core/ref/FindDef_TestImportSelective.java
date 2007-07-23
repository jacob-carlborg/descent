package mmrnmhrm.tests.core.ref;

import java.util.Arrays;
import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FindDef_TestImportSelective extends FindDef_CommonImportTest  {
	
	static final String testSrcFile = "testImportSelective.d";


	@BeforeClass
	public static void classSetup() {
		staticTestInit(testSrcFile);
	}
	
	// TODO: some test cases are commented due to parser no-source-range bugs
	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{

                //
                //
                // {195, 12, "pack/sample.d"}, //FIXME: parser bugs
                //
                
                //{208, 60, "pack/sample.d"}, //FIXME: parser bugs
                //{240, 100, "pack/sample.d"}, //FIXME: parser bugs
                //{258, 25, "pack/sample.d"}, //FIXME: parser bugs
                //{286, 40, "pack/sample.d"}, //FIXME: parser bugs

                {479, -1, null},
                
                {528, 208, testSrcFile},
                {554, 221, testSrcFile},
                {586, -1, null},

                {652, 86, "pack/sample.d"},
                {672, 221, testSrcFile},
                {701, -1, null},

                
                {720, -1, null},
                {752, -1, null},
                {803, -1, null},
                {852, -1, null},
              
        });
    }
    
	
	public FindDef_TestImportSelective(int defOffset, int refOffset, String targetFile) 
		throws Exception {
		super(defOffset, refOffset, targetFile);
	}

}
