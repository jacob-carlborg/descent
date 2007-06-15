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
public class GoToDefinition_TestKinds3 extends GoToDefinition_CommonTest  {
	
	static final String testfile = "refs/refKinds3.d";
	
	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {218, -1},
                {250, -1},
                {279, -1},
                {312, -1},
                {354, -1},
                {387, -1},
                
                {423, -1}, {427, -1},  
                {459, -1}, {463, -1}, 
                {495, -1}, {505, -1},
                {554, -1}, {558, -1},
                {602, -1}, {606, -1},
                {652, -1}, {656, -1}, {662, -1},
                
                {733, -1},

        });
    }
    
	@BeforeClass
	public static void classSetup() {
		prepClass(testfile);
	}
	
	public GoToDefinition_TestKinds3(int defOffset, int refOffset) throws IOException {
		super(defOffset, refOffset, testfile);
	}
	


	@Test
	public void test() {
		assertGoToReF(defOffset, refOffset);
	}
}
