package dtool;
/**
 * @author phoenix
 *
 */

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import dtool.ANTLRparser.Model;
import dtool.dom.ext.ASTPrinter;
import dtool.formater.CodeFormatter;


public class Main {
	
    public static void main(String[] args) throws Exception {
		System.out.println("=== DTool ===");

		try {
			Project.newTestProject();

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

    public static void testDtool(String[] args) {
		System.out.println("== ANTLR Parsing... ==");
		
		Model.createModel(Project.testcu);
		Model.printModel();
		//Engine.testRefactor();
	}
	
	public static void testDescent(String[] args) {
		
		System.out.println("== Descent Parsing... ==");
		
		CompilationUnit cu = Project.testcu;
		cu.preParseCompilationUnit();
		
		if(cu.problems.length > 0) {
			System.out.println("== Problems: ==");
			System.out.println(cu.problems);
			System.exit(1);
		}
		
		System.out.println("== Descent AST Tree: ==");
		cu.cumodule.accept(new ASTPrinter(false));
		
		System.out.println("== Neo AST Tree: ==");
		cu.adaptDOM();
		cu.cumodule.accept(new ASTPrinter(false, false));
		
		if(true) return;
		
		System.out.println("=== Reformat: ===");
		
		OutputStream out = new ByteArrayOutputStream();
		CodeFormatter.formatSource(cu);
		//cu.module.accept(new FormaterVisitor(System.out, Main.cbuf));
		//System.out.print(out);
		
	}
}
