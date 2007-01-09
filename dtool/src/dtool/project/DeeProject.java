package dtool.project;

import java.io.File;
import java.util.List;

import util.ExceptionAdapter;
import util.FileUtil;
import dtool.dom.base.DefUnit;
import dtool.model.BindingResolver;
import dtool.model.IScope;
import dtool.model.ModelException;

public class DeeProject implements IScope {

	public CompilationUnit testcu;
	private BindingResolver bresolver = new BindingResolver(this);
	
	public static DeeProject newTestProject() {
		DeeProject dproj = new DeeProject();
		try {
			String source = FileUtil.readStringFromFile(new File("testinput/test.d"));
			dproj.testcu = new CompilationUnit(source);
			System.out.println(">> read: " + dproj.testcu.file + " ");
			//System.out.println(testcu.source);
			
		} catch (Exception e) {
			throw ExceptionAdapter.unchecked(e);
		}
		return dproj;
	}

	public List<DefUnit> getDefUnits() {
		return testcu.getNeoModule().getDefUnits();
	}
	
	public DefUnit findEntity(String string) throws ModelException {
		return bresolver.findEntity(string);
	}

	/*public ASTNode findEntity(CompilationUnit cunit, int offset) {
		AssertIn.isTrue(offset < cunit.source.length());
		ASTNode elem = ASTElementFinder.findElement(cunit.getModule(), offset);
		return elem;
	}*/

}
