package descent.tests.dstress;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import descent.core.compiler.IProblem;
import descent.core.dom.AST;
import descent.core.dom.CompilationUnitResolver;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.Parser;
import descent.tests.mars.Parser_Test;
import static descent.tests.dstress.SpecificationsFactory.TODO;
import static descent.tests.dstress.SpecificationsFactory.four;
import static descent.tests.dstress.SpecificationsFactory.one;
import static descent.tests.dstress.SpecificationsFactory.three;
import static descent.tests.dstress.SpecificationsFactory.two;

public class Dstress_Test extends Parser_Test implements IDstressConfiguration {
	
	private static Map<String, ISpecification> nocompile;
	private static Set<String> compileFail;
	
	static {
		// Currently, in this files dmd fails to compile but it should
		compileFail = new HashSet<String>();
		compileFail.add("array_initialization_33_A.d");
		compileFail.add("assert_20_A.d");
		compileFail.add("assert_20_B.d");
		compileFail.add("bug_e2ir_520_B.d");
		compileFail.add("bug_e2ir_772_H.d");
		compileFail.add("bug_e2ir_772_I.d");
		compileFail.add("bug_expression_4420_A.d");
		compileFail.add("bug_expression_4420_B.d");
	}
	
	static {
		nocompile = new LinkedHashMap<String, ISpecification>();
		nocompile.put("a\\alias_18.d", one(AST.D1, IProblem.AliasCannotBeConst, "const"));
		nocompile.put("a\\alias_26_A.d", one(AST.D1, IProblem.SymbolConflictsWithSymbolAtLocation, "void main(){", 5, 4));
		nocompile.put("a\\alias_26_B.d", one(AST.D1, IProblem.SymbolConflictsWithSymbolAtLocation, "alias void a;", 11, 1));
		nocompile.put("a\\alias_26_C.d", one(AST.D1, IProblem.SymbolConflictsWithSymbolAtLocation, "void main(){", 5, 4));
		nocompile.put("a\\alias_26_D.d", one(AST.D1, IProblem.SymbolConflictsWithSymbolAtLocation, "void main(){", 5, 4));
		nocompile.put("a\\alias_28_A.d", TODO(AST.D1));
		nocompile.put("a\\alias_28_B.d", one(AST.D1, IProblem.UndefinedProperty, "A.x", 2, 1));
		nocompile.put("a\\alias_28_C.d", one(AST.D1, IProblem.UndefinedProperty, "a.x", 2, 1));
		nocompile.put("a\\alias_28_D.d", one(AST.D1, IProblem.UndefinedProperty, "a.x", 2, 1));
		nocompile.put("a\\alias_28_E.d", one(AST.D1, IProblem.UndefinedProperty, "A.x", 2, 1));
		nocompile.put("a\\alias_28_F.d", TODO(AST.D1));
		nocompile.put("a\\alias_30_A.d", one(AST.D1, IProblem.CircularDefinition, "Foo;", 0, 3));
		nocompile.put("a\\alias_30_B.d", one(AST.D1, IProblem.CircularDefinition, "Foo;", 0, 3));
		nocompile.put("a\\alias_30_C.d", one(AST.D1, IProblem.CircularDefinition, "Foo;", 0, 3));
		nocompile.put("a\\alias_30_D.d", one(AST.D1, IProblem.SymbolConflictsWithSymbolAtLocation, "Foo;", 0, 3));
		nocompile.put("a\\alias_30_E.d", one(AST.D1, IProblem.SymbolConflictsWithSymbolAtLocation, "Foo;", 0, 3));
		nocompile.put("a\\alias_30_F.d", one(AST.D1, IProblem.SymbolConflictsWithSymbolAtLocation, "Foo;", 0, 3));
		nocompile.put("a\\alias_30_G.d", one(AST.D1, IProblem.CircularDefinition, "alias b a;", 8, 1));
		nocompile.put("a\\alias_30_L.d", one(AST.D1, IProblem.CircularDefinition, "alias a a;", 8, 1));
		nocompile.put("a\\alias_33_A.d", four(AST.D1, IProblem.UndefinedIdentifier, "alias UNDEFINED", 6, 9, IProblem.UndefinedIdentifier, "alias UNDEFINED", 6, 9, IProblem.UsedAsAType, "alias UNDEFINED", 6, 9, IProblem.VoidsHaveNoValue, "ALIAS x", 0, 5));
		nocompile.put("a\\alias_33_B.d", four(AST.D1, IProblem.UndefinedIdentifier, "alias UNDEFINED", 6, 9, IProblem.UsedAsAType, "alias UNDEFINED", 6, 9, IProblem.VoidsHaveNoValue, "ALIAS x", 0, 5, IProblem.UndefinedIdentifier, "alias UNDEFINED", 6, 9));
		nocompile.put("a\\alias_33_C.d", four(AST.D1, IProblem.UndefinedIdentifier, "ALIAS x;", 0, 5, IProblem.UsedAsAType, "ALIAS x;", 0, 5, IProblem.VoidsHaveNoValue, "ALIAS x", 0, 5, IProblem.UndefinedIdentifier, "alias UNDEFINED", 6, 9));
		nocompile.put("a\\alias_33_D.d", four(AST.D1, IProblem.UndefinedIdentifier, "alias UNDEFINED", 6, 9, IProblem.UndefinedIdentifier, "alias UNDEFINED", 6, 9, IProblem.UsedAsAType, "alias UNDEFINED", 6, 9, IProblem.VoidsHaveNoValue, "ALIAS x", 0, 5));
		nocompile.put("a\\alias_34_A.d", three(AST.D1, IProblem.UndefinedIdentifier, "(Foo)", 1, 3, IProblem.UsedAsAType, "(Foo)", 1, 3, IProblem.CannotHaveParameterOfTypeVoid, "(Foo)", 1, 3));
		nocompile.put("a\\alias_36_B.d", two(AST.D1, IProblem.SymbolNotATemplateItIs, "foo!(long);", 0, 3, IProblem.ExpressionHasNoEffect, "foo!(long);", 0, 10));
		nocompile.put("a\\alias_36_C.d", one(AST.D1, IProblem.SymbolNotATemplate, "mixin foo!(long);", 6, 10));
		nocompile.put("a\\alias_36_D.d", TODO(AST.D1));
		nocompile.put("a\\alias_37_A.d", one(AST.D1, IProblem.SymbolCannotBeDeclaredToBeAFunction, "foo x;", 4, 1));
		nocompile.put("a\\alias_37_B.d", one(AST.D1, IProblem.CannotHaveArrayOfType, "foo[3]"));
		nocompile.put("a\\alias_37_C.d", one(AST.D1, IProblem.CannotHaveArrayOfType, "foo[3]"));
		nocompile.put("a\\alias_37_D.d", one(AST.D1, IProblem.SymbolCannotBeDeclaredToBeAFunction, "foo x;", 4, 1));
		nocompile.put("a\\alias_39_A.d", two(AST.D1, IProblem.UndefinedIdentifier, "(y);", 1, 1, IProblem.SymbolDoesNotMatchAnyTemplateDeclaration, "mixin Foo!(y);", 0, 13));
		nocompile.put("a\\alias_39_B.d", TODO(AST.D1));
		nocompile.put("a\\alias_41_B.d", one(AST.D1, IProblem.SymbolConflictsWithSymbolAtLocation, "struct Foo", 7, 3));
		nocompile.put("a\\alias_42_C.d", TODO(AST.D1));
		nocompile.put("a\\array_initialization_10.d", one(AST.D1, IProblem.IndexOverflowForStaticArray, "int.max/32"));
		nocompile.put("a\\array_initialization_18_A.d", TODO(AST.D1));
	}
	
	public void testNoCompile() throws Throwable {
		int total = nocompile.size();
		int passed = 0;
		
		StringBuilder sb = new StringBuilder();
		
		for(Map.Entry<String, ISpecification> entry : nocompile.entrySet()) {
			String filename = entry.getKey();
			ISpecification spec = entry.getValue();
			
			File file = new File(new File(DSTRESS_PATH, "nocompile"), filename);
			char[] source = getContents(file);
			
			try {
				Parser parser = new Parser(spec.getApiLevel(), source);
				Module module = parser.parseModuleObj();
				CompilationUnitResolver.resolve(module);
				
				spec.validate(source, module);
				passed++;
			} catch (Exception e) {
				sb.append("\n");
				sb.append(filename);
				sb.append(": ");
				sb.append(e.getMessage());
			} catch (Throwable e) {
				System.out.println(file);
				e.printStackTrace();
				throw e;
			}
		}
		
		if (sb.length() != 0) {
			int failed = total - passed;
			throw new Exception("Failed:" + failed + ", Passed: " + passed + ", Total: " + total + sb.toString());
		}
	}
	
	public void testCompile() throws Throwable {
		List<File> compile = listRecursive(new File(DSTRESS_PATH, "compile"));
		
		int total = compile.size();
		int passed = 0;
		
		StringBuilder sb = new StringBuilder();
		
		for(File file : compile) {
			String filename = file.getName();
			
			if (compileFail.contains(filename)) {
				continue;
			}
			
			char[] source = getContents(file);
			try {
				Parser parser = new Parser(AST.D1, source);
				Module module = parser.parseModuleObj();
				
				// It seems warnings are disabled in dstress
				CompilationUnitResolver.resolve(module, false /* don't show warnings */);
				
				if (module.problems.size() == 0) {
					passed++;
				} else {
					sb.append("\n");
					sb.append(filename);
					sb.append(" had ");
					sb.append(module.problems.size());
					sb.append(" problem(s): ");
					sb.append(module.problems);
				}
			} catch (Exception e) {
				sb.append("\n");
				sb.append(filename);
				sb.append(": ");
				sb.append(e.getMessage());
//				e.printStackTrace();
//				break;
			} catch (Throwable e) {
				System.out.println(file);
				e.printStackTrace();
				throw e;
			}
		}
		
		if (sb.length() != 0) {
			int failed = total - passed;
			throw new Exception("Failed:" + failed + ", Passed: " + passed + ", Total: " + total + sb.toString());
		}
	}
	
	// Remove the _ to test a dstress test in particular
	public void _testCompileDebug() throws Throwable {
		List<File> compile = new ArrayList<File>();
		compile.add(new File("c:\\ary\\programacion\\d\\dstress\\compile\\a\\assert_14_J.d"));
		
		int total = compile.size();
		int passed = 0;
		
		StringBuilder sb = new StringBuilder();
		
		for(File file : compile) {
			String filename = file.getName();
			
			if (compileFail.contains(filename)) {
				continue;
			}
			
			char[] source = getContents(file);
			try {
				Parser parser = new Parser(AST.D1, source);
				Module module = parser.parseModuleObj();
				
				// It seems warnings are disabled in dstress
				CompilationUnitResolver.resolve(module, false /* don't show warnings */);
				
				if (module.problems.size() == 0) {
					passed++;
				} else {
					sb.append("\n");
					sb.append(filename);
					sb.append(" had ");
					sb.append(module.problems.size());
					sb.append(" problem(s): ");
					sb.append(module.problems);
				}
			} catch (Exception e) {
				sb.append("\n");
				sb.append(filename);
				sb.append(": ");
				sb.append(e.getMessage());
				e.printStackTrace();
				break;
			} catch (Throwable e) {
				System.out.println(file);
				e.printStackTrace();
				throw e;
			}
		}
		
		if (sb.length() != 0) {
			int failed = total - passed;
			throw new Exception("Failed:" + failed + ", Passed: " + passed + ", Total: " + total + sb.toString());
		}
	}

	private List<File> listRecursive(File dir) {
		List<File> files = new ArrayList<File>();
		listRecursive(dir, files);		
		return files;
	}

	private void listRecursive(File dir, List<File> acumulator) {
		File[] subFiles = dir.listFiles();
		if (subFiles == null) {
			return;
		}
		
		for(File subFile : subFiles) {
			if (subFile.isDirectory()) {
				listRecursive(subFile, acumulator);
			} else if (subFile.isFile() && subFile.getName().endsWith(".d")) {
				acumulator.add(subFile);
			}
		}
	}

	private char[] getContents(File file) throws Exception {
		char[] contents = new char[(int) file.length()];
		FileReader r = new FileReader(file);
		r.read(contents);
		r.close();
		return contents;
	}
	
}
