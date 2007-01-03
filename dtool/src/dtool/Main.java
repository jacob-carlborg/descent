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
import dtool.project.DeeProject;


public class Main {
	
	private Main() { }
	

    public static DeeProject dproj;

	public static void main(String[] args) throws Exception {
		System.out.println("======== DTool ========");
		
		try {
			dproj = DeeProject.newTestProject();
			testDescent(args);
			//testDtool(args);
			
		} catch (Exception e) {
			// For annoying flushing problems in Eclipse console
			System.out.flush();
			System.err.flush();
				
			throw e;
		} 
		System.out.println("= THE END =");
	}

    public static void testDescent(String[] args) {
		
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
		
		testFindEntityByOffset(40);
		
		testFindEntityByName();
	}

	private static void testFindEntityByName() {
		try {
			System.out.println("====== findEntity by name: ======");
			DefUnit defunit = dproj.findEntity("%%.Foo");
			System.out.println(defunit);
			System.out.println(StringUtil.collToString(defunit.getScope().getDefUnits(), "\n"));
			
		} catch (ModelException e) {
			throw ExceptionAdapter.unchecked(e);
		}
	}

	private static void testFindEntityByOffset(int offset) {
		try {
			;
			System.out.println("===== findEntity by offset: "+offset+ " =====");
			ASTNode elem = dproj.findEntity(dproj.testcu, offset);
			if(elem == null) {
				System.out.println("No Elem found??");
				return;
			}
			System.out.println(ASTPrinter.toStringElement(elem));
			if(elem instanceof Entity) {
				DefUnit defunit = ((Entity)elem).getReferencedDefUnit();
				System.out.println("F3 Declaration at:" +defunit.getStartPos());
				System.out.println(StringUtil.collToString(defunit.getScope().getDefUnits(), "\n"));
			} else if(elem instanceof DefUnit) {
				DefUnit defunit = ((DefUnit)elem);
				System.out.println(defunit);
				System.out.println(StringUtil.collToString(defunit.getScope().getDefUnits(), "\n"));
			} else {
				System.out.println("other");
			} 
		} catch (Exception e) {
			throw ExceptionAdapter.unchecked(e);
		}
	}

}
