package dtool.formater;

import java.io.OutputStream;

import dtool.project.CompilationUnit;

public class CodeFormatter {

	public static void formatSource(CompilationUnit cu, OutputStream out) {
		cu.getModule().accept(new FormaterVisitor(out, cu.source));
	}

	public static void formatSource(CompilationUnit cu) {
		cu.getModule().accept(new FormaterVisitor(System.out, cu.source));
	}
}
