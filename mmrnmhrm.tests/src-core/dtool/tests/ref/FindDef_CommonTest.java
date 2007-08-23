package dtool.tests.ref;

import java.io.IOException;
import java.util.Collection;

import mmrnmhrm.core.model.CompilationUnit;
import mmrnmhrm.tests.BasePluginTest;
import mmrnmhrm.tests.SampleMainProject;

import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

import descent.internal.compiler.parser.ast.IASTNode;
import dtool.dom.ast.ASTNodeFinder;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Module;
import dtool.dom.references.Reference;
import dtool.refmodel.NodeUtil;

//@RunWith(Parameterized.class)
public abstract class FindDef_CommonTest extends BasePluginTest {

	public static int counter = -666;
	
	public static final String TEST_SRCFOLDER = SampleMainProject.TEST_SRC_REFS;
	

	protected static void staticTestInit(String testfile) {
		counter = -1;
		System.out.println("======== "+ testfile +" ========");
	}
	
	CompilationUnit cunit;
	Module module;

	int offset; 
    int targetOffset;
	
	public FindDef_CommonTest(int offset, int targetOffset, String testfile) throws IOException, CoreException {
		this.offset = offset;
		this.targetOffset = targetOffset;
		//cunit = CoreTestUtils.testParseCUnit(TestUtils.readTestDataFile(testfile));
		cunit = SampleMainProject.getCompilationUnit(TEST_SRCFOLDER +"/"+ testfile);
		//System.out.println("==== Source length: "+cunit.source.length()+" ====");
		module = cunit.getNeoModule();	
	}
	
	@Test
	public void test() {
		assertFindReF(cunit, offset, cunit, targetOffset);
	}

	
	public static void assertFindReF(CompilationUnit cunit, int offset,
			CompilationUnit targetCunit, int targetOffset) {
		counter++;
		System.out.print("Find ref case #"+counter+": "+offset+": ");
		System.out.println(cunit.getSource().substring(offset).split("\\s")[0]);
		
		IASTNode node = ASTNodeFinder.findElement(cunit.getNeoModule(), offset);
		Reference ent = (Reference) node;
		
		Collection<DefUnit> defunits = ent.findTargetDefUnits(true);
		
		if(defunits == null || defunits.isEmpty()) {
			assertTrue(targetOffset == -1, 
					" Find Ref got no DefUnit.");
			return;
		}
		DefUnit defunit = defunits.iterator().next();
		
		assertTrue(defunit != null, " defunit = null");

		Module obtainedModule = NodeUtil.getParentModule(defunit);
		assertTrue(obtainedModule.getModuleUnit().equals(targetCunit.getModuleUnit()),
				" Find Ref got wrong target module.");
		
		assertTrue(defunit.defname.getStartPos() == targetOffset, 
				" Find Ref went to wrong offset: " + defunit.defname.getStartPos());
	}
}