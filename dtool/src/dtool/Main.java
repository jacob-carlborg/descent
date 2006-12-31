package dtool;
/**
 * @author phoenix
 *
 */

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import util.ExceptionAdapter;
import util.StringUtil;
import dtool.dom.ast.ASTChecker;
import dtool.dom.ast.ASTPrinter;
import dtool.dom.base.ASTNode;
import dtool.dom.base.DefUnit;
import dtool.dom.base.Entity;
import dtool.formater.CodeFormatter;
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
			// Annoying flushing problems
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
		
		if(cu.problems.length > 0) {
			System.out.println("== Problems: ==");
			for (int i = 0; i < cu.problems.length; i++) {
				System.out.println(cu.problems[i].toString());
			}
			System.exit(1);
		}
		
		System.out.println("====== Descent AST Tree: ======");
		cu.getOldModule().accept(new ASTPrinter(false));
		
		System.out.println("====== Neo AST Tree: ======");
		cu.adaptDOM();
		cu.getNeoModule().accept(new ASTPrinter(false, false));
		
		System.out.println("====== Neo AST Consistency check: ======");
		ASTChecker.checkConsistency(cu.getNeoModule());
		
		try {
			System.out.println("====== findEntity by name: ======");
			DefUnit defunit = dproj.findEntity("%%.Foo");
			System.out.println(defunit);
			System.out.println(StringUtil.collToString(defunit.getScope().getDefUnits(), "\n"));
			
		} catch (ModelException e) {
			throw new ExceptionAdapter(e);
		}
	
		try {
			int offset = 80;
			System.out.println("===== findEntity by offset: "+offset+ " =====");
			ASTNode elem = dproj.findEntity(dproj.testcu, offset);
			ASTPrinter.printSingleElement(elem);
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
			throw new ExceptionAdapter(e);
		}
	}

	private static void testRefactor(CompilationUnit cu) {
		System.out.println("=== Reformat: ===");
		
		OutputStream out = new ByteArrayOutputStream();
		CodeFormatter.formatSource(cu);
		//cu.module.accept(new FormaterVisitor(System.out, Main.cbuf));
		//System.out.print(out);
	}
}
