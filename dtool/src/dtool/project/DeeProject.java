package dtool.project;

import java.io.File;
import java.util.List;

import util.AssertIn;
import util.ExceptionAdapter;
import dtool.dom.ast.ASTElementFinder;
import dtool.dom.base.ASTNode;
import dtool.dom.base.DefUnit;
import dtool.model.BindingResolver;
import dtool.model.IScope;
import dtool.model.ModelException;

public class DeeProject implements IScope {

	public CompilationUnit testcu;
	
	public static DeeProject newTestProject() {
		DeeProject dproj = new DeeProject();
		try {
			dproj.testcu = new CompilationUnit(new File("testinput/test.d"));
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
		return (new BindingResolver(this)).findEntity(string);
	}

	public ASTNode findEntity(CompilationUnit cunit, int offset) {
		AssertIn.isTrue(offset < cunit.source.length());
		ASTNode elem = ASTElementFinder.findElement(cunit.getNeoModule(), offset);
		return elem;
	}

}