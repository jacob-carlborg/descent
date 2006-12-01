package dtool.formater;

import java.io.OutputStream;

import dtool.CompilationUnit;

public class CodeFormatter {

	public static void formatSource(CompilationUnit cu, OutputStream out) {
		cu.cumodule.accept(new FormaterVisitor(out, cu.source));
	}

	public static void formatSource(CompilationUnit cu) {
		cu.cumodule.accept(new FormaterVisitor(System.out, cu.source));
	}
}
