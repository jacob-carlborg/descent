package dtool.project;

import java.util.List;

import util.ExceptionAdapter;
import descent.core.domX.ASTNode;
import dtool.dom.DefUnit;
import dtool.dombase.ASTElementFinder;
import dtool.dombase.IScope;
import dtool.model.BindingResolver;
import dtool.model.ModelException;

public class Project implements IScope {

	public CompilationUnit testcu;
	
	public static Project newTestProject() {
		Project dproj = new Project();
		try {
			dproj.testcu = new CompilationUnit("testinput/test.d");
			System.out.println(">> read: " + dproj.testcu.file + " ");
			//System.out.println(testcu.source);
			
		} catch (Exception e) {
			throw new ExceptionAdapter(e);
		}
		return dproj;
	}

	public List<DefUnit> getDefUnits() {
		return testcu.getModule().getDefUnits();
	}

	public DefUnit findEntity(String string) throws ModelException {
		return (new BindingResolver(this)).findEntity(string);
	}

	public ASTNode findEntity(CompilationUnit cunit, int offset) {
		ASTNode elem = ASTElementFinder.findElement(cunit.getModule(), offset);
		return elem;
	}

}
