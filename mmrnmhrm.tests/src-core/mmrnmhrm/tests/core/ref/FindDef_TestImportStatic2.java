package mmrnmhrm.tests.core.ref;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import dtool.tests.ref.FindDef_CommonTest;

/**
 * This classes reuses FindDef_TestImportStatic test data, modyfing only some
 * of test cases, and the source file.
 *
 */
@RunWith(Parameterized.class)
public class FindDef_TestImportStatic2 extends FindDef_TestImportStatic  {

	@Parameters
    public static List<Object[]> data() {
    	java.util.List<Object[]> coll = FindDef_TestImportStatic.data();
    	coll.set(0, new Object[] {180, 12, "pack/mod1.d"});
    	coll.set(1, new Object[] {198, 12, "pack/mod2.d"});
    	coll.set(2, new Object[] {209, 12, "pack/sample.d"});
    	coll.set(3, new Object[] {230, 20, "pack/subpack/mod3.d"});
        return coll;
    }
    
	
	public FindDef_TestImportStatic2(int defOffset, int refOffset, String targetFile) 
		throws Exception {
		super(defOffset, refOffset, targetFile);
	}

	@Test
	public void test() throws Exception {
		cunit.getDocument().replace(ix1, 4, "//  ");
		cunit.getDocument().replace(ix2, 4, "    ");
		cunit.reconcile();
		FindDef_CommonTest.assertFindReF(cunit, offset, targetCUnit, targetOffset);
	}
	
	
}