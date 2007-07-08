package dtool.tests.ast;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;

import mmrnmhrm.core.model.CompilationUnit;
import mmrnmhrm.tests.BaseTestClass;
import mmrnmhrm.tests.CoreTestUtils;
import mmrnmhrm.tests.TestUtils;
import dtool.dom.ast.ASTElementFinder;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Module;
import dtool.dom.references.Entity;

//@RunWith(Parameterized.class)
public abstract class GoToDefinition_CommonTest extends BaseTestClass {

	static int counter = -666;

	protected static void prepClass(String testfile) {
		counter = -1;
		System.out.println("======== "+ testfile +" ========");
	}

	
	CompilationUnit cunit;
	Module module;

	
	int defOffset; 
    int refOffset;
	
	public GoToDefinition_CommonTest(int defOffset, int refOffset, String testfile) throws IOException, CoreException {
		this.defOffset = defOffset;
		this.refOffset = refOffset;
		
		cunit = CoreTestUtils.testCUparsing(TestUtils.readTestDataFile(testfile));
		//System.out.println("==== Source length: "+cunit.source.length()+" ====");
		module = cunit.getNeoModule();	
	}
	
	

	protected void assertGoToReF(int refOffset, int defOffset) {
		counter++;
		System.out.print("Find ref case #"+counter+": "+refOffset+": ");
		System.out.println(cunit.getSource().substring(refOffset).split("\\s")[0]);
		Entity ent = (Entity) ASTElementFinder.findElement(module, refOffset);
		DefUnit defunit = ent.getTargetDefUnit();
		
		if(defOffset == -1) {
			assertTruePrintln(defunit == null, " Find Ref got an invalid DefUnit.");
			return;
		}
		
		if(defunit == null)
			assertTruePrintln(false, " Find Ref got no DefUnit.");
		
		String msg = " Find Ref went to wrong offset: " + defunit.defname.startPos;
		assertTruePrintln(defunit.defname.startPos == defOffset, msg);
	}

}