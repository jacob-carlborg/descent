package dtool.tests.ast;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class GoToDefinition_TestKinds2 extends GoToDefinition_CommonTest  {
	
	static final String testfile = "refs/refKinds2.d";
	static final int defOffsetIncrement = 20;
	
	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {258, 241},
                {290, 217},
                {319, 14},
                {352, 4},
                {394, 124},
                {427, 124},
                
                {463, 241}, {467, -1},  
                {499, 14},  {503, 59}, 
                {535, 124}, {545, 154},
                {594, 241}, {598, -1},
                {642, 14},  {646, 25},
                {692, 241}, {696, -1}, {702, -1},
                
                {773, 241},
        });
    }
    
	@BeforeClass
	public static void classSetup() {
		prepClass(testfile);
	}
 	        
	public GoToDefinition_TestKinds2(int defOffset, int refOffset) throws IOException, CoreException {
		super(defOffset + defOffsetIncrement, refOffset, testfile);
	}
	


	@Test
	public void test() {
		assertGoToReF(defOffset, refOffset);
	}
}
