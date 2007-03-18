package dtool;


import util.ExceptionAdapter;
import util.StringUtil;
import dtool.dom.ast.ASTChecker;
import dtool.dom.ast.ASTPrinter;
import dtool.dom.base.ASTNode;
import dtool.dom.base.DefUnit;
import dtool.dom.base.Entity;
import dtool.model.ModelException;
import dtool.project.CompilationUnit;
import dtool.project.DToolProject;


public class Main {
	
	private Main() { }
	

    public static DToolProject dproj;

	public static void main(String[] args) throws Exception {
		System.out.println("======== DTool ========");
		try {
			dproj = DToolProject.newTestProject();
			testDtool(args);
			// testDtool(args);

		} catch (Exception e) {
			// For annoying flushing problems in Eclipse console
			System.out.flush();
			System.err.flush();

			throw e;
		}
		System.out.println("= THE END =");
	}


    public static void testDtool(String[] args) {
		
		System.out.println("== Descent Parsing... ==");
		
		CompilationUnit cu = dproj.testcu;
		cu.preParseCompilationUnit();
		
		System.out.println("====== Descent AST Tree: ======");
		System.out.println(ASTPrinter.toStringAST(cu.getOldModule()));
		
		if(cu.hasErrors()) {
			System.out.println("== Problems: ==");
			for (int i = 0; i < cu.problems.length; i++) {
				System.out.println(cu.problems[i].toString());
			}
			System.exit(1);
		}
		
		System.out.println("====== Neo AST Tree: ======");
		cu.adaptDOM();
		System.out.println(ASTPrinter.toStringNeoAST(cu.getNeoModule()));
		
		System.out.println("====== Neo AST Consistency check: ======");
		ASTChecker.checkConsistency(cu.getNeoModule());
		
		testFindEntityByName();
		ASTNode elem = testFindEntityByOffset(44);
		testFindDef(elem); 
	}

	private static void testFindEntityByName() {
		try {
			System.out.println("====== findEntity by name: ======");
			DefUnit defunit = dproj.findEntity("%%.Foo");
			System.out.println(defunit);
			System.out.println(" == def units: == ");
			System.out.println(StringUtil.collToString(defunit.getScope().getDefUnits(), "\n"));
			
		} catch (ModelException e) {
			throw ExceptionAdapter.unchecked(e);
		}
	}

	private static ASTNode testFindEntityByOffset(int offset) {
		ShellUI.println("===== findEntity by offset: "+offset+ " =====");
		
		ASTNode elem = dproj.testcu.findEntity(offset);
		if(elem == null) {
			ShellUI.println("No element found at pos: " + offset);
			return null;
		}
		ShellUI.println("FOUND: " + ASTPrinter.toStringElement(elem));
		return elem;
	}

	private static void testFindDef(ASTNode elem) {
		if(elem instanceof Entity) {
			DefUnit defunit = ((Entity)elem).getTargetDefUnit();
			if(defunit == null) {
				ShellUI.println("Definition not found, for entity: " + elem);
				return;
			}
			ShellUI.println("Definition at: " + defunit.getStartPos());
			ShellUI.println(StringUtil.collToString(defunit.getScope().getDefUnits(), "\n"));
		} else if(elem instanceof DefUnit.Symbol) {
			ShellUI.println("Already at definition of element: " + elem);
		} else {
			ShellUI.println("Element is not an entity reference. (" + elem +")");
		}
	}

}
