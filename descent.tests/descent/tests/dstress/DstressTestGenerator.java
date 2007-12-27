package descent.tests.dstress;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DstressTestGenerator extends DstressTestGeneratorBase {
	
	public static void main(String[] args) throws Exception {
		generateCompile();
		generateNoCompile();
	}
	
	private static void generateCompile() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("package descent.tests.dstress;\r\n" + 
				"\r\n" + 
				"import java.io.File;\r\n" + 
				"import java.io.FileReader;\r\n" + 
				"\r\n" + 
				"import descent.core.IProblemRequestor;\r\n" + 
				"import descent.core.compiler.IProblem;\r\n" +
				"import descent.internal.compiler.parser.SemanticContext;\r\n" +
				"import descent.core.dom.AST;\r\n" + 
				"import descent.internal.compiler.parser.Global;\r\n" +
				"import descent.internal.compiler.parser.Module;\r\n" + 
				"import descent.internal.compiler.parser.Parser;\r\n" + 
				"import descent.tests.mars.Parser_Test;\r\n\r\n");
		sb.append("public class DstressCompile_Test extends Parser_Test implements IDstressConfiguration {\r\n");
		sb.append("\r\n");
		
		List<File> compile = new ArrayList<File>();
		compile.addAll(listRecursive(new File(DSTRESS_PATH, "compile")));
		compile.addAll(listRecursive(new File(DSTRESS_PATH, "run")));
		compile.addAll(listRecursive(new File(DSTRESS_PATH, "norun")));
		
		for(File file : compile) {
			
			String name = file.getName();
			if (failures.contains(name)) {
				continue;
			}
			
			int errorsNumber = errors(file).size();
			if (errorsNumber == 0) {
				sb.append("\tpublic void test_")
					.append(file.getName().replace('.', '_'))
					.append("() throws Exception {\r\n");
				sb.append("\t\tcompile(\"").append(file.getAbsolutePath().replace("\\", "\\\\")).append("\");\r\n");
				sb.append("\t}\r\n\r\n");
			}
		}
		
		sb.append("\tprivate void compile(String file) throws Exception {\r\n");
		sb.append("char[] source = getContents(new File(file));\r\n" + 
				"		Parser parser = new Parser(AST.D1, source);\r\n" + 
				"		parser.filename = file.substring(DSTRESS_WHERE_PATH.length()).toCharArray();\r\n" + 
				"		final Module module = parser.parseModuleObj();\r\n" + 
				"		Global global = new Global();\r\n" + 
				"		global.params.warnings = false;\r\n" + 
				"		\r\n" + 
				"		SemanticContext context = new SemanticContext(new IProblemRequestor() {\r\n" + 
				"			public void acceptProblem(IProblem problem) {\r\n" + 
				"				module.problems.add(problem);\r\n" + 
				"			}\r\n" + 
				"			public void beginReporting() {\r\n" + 
				"			}\r\n" + 
				"			public void endReporting() {\r\n" + 
				"			}\r\n" + 
				"			public boolean isActive() {\r\n" + 
				"				return false;\r\n" + 
				"			}\r\n" + 
				"			\r\n" + 
				"		}, module, null, new DmdModuleFinder(global), global);\r\n" + 
				"		\r\n" + 
				"		if (!(module.problems != null && module.problems.size() > 0)) {\r\n" + 
				"			module.semantic(context);\r\n" + 
				"		}\r\n" + 
				"		\r\n" + 
				"		if (!module.problems.isEmpty()) {\r\n" + 
				"			fail(module.problems.toString());\r\n" + 
				"		}");
		sb.append("\t}\r\n\r\n");
		
		sb.append(
				"\tprivate char[] getContents(File file) throws Exception {\r\n" + 
				"\t\tchar[] contents = new char[(int) file.length()];\r\n" + 
				"\t\tFileReader r = new FileReader(file);\r\n" + 
				"\t\tr.read(contents);\r\n" + 
				"\t\tr.close();\r\n" + 
				"\t\treturn contents;\r\n" + 
				"\t}\r\n");
		
		sb.append("\r\n");
		sb.append("}\r\n");
		
		FileWriter fw = new FileWriter("descent/tests/dstress/DstressCompile_Test.java");
		fw.write(sb.toString());
		fw.close();
	}
	
	private static void generateNoCompile() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("package descent.tests.dstress;\r\n" + 
				"\r\n" + 
				"import java.io.File;\r\n" + 
				"import java.io.FileReader;\r\n" + 
				"\r\n" +
				"import descent.core.IProblemRequestor;\r\n" + 
				"import descent.core.compiler.IProblem;\r\n" +
				"import descent.internal.compiler.parser.SemanticContext;\r\n" +
				"import descent.core.dom.AST;\r\n" + 
				"import descent.internal.compiler.parser.Global;\r\n" +
				"import descent.internal.compiler.parser.Module;\r\n" + 
				"import descent.internal.compiler.parser.Parser;\r\n" + 
				"import descent.tests.mars.Parser_Test;\r\n\r\n");
		sb.append("public class DstressNoCompile_Test extends Parser_Test implements IDstressConfiguration {\r\n");
		sb.append("\r\n");
		
		List<File> compile = new ArrayList<File>();
		compile.addAll(listRecursive(new File(DSTRESS_PATH, "nocompile")));
		for(File file : compile) {
			
			String name = file.getName();
			if (failures.contains(name)) {
				continue;
			}
			
			sb.append("\tpublic void test_")
				.append(file.getName().replace('.', '_'))
				.append("() throws Exception {\r\n");
			
			String fullFilename = file.getAbsolutePath().replace("\\", "\\\\");
			if (syntaxErrors.contains(name)) {
				sb.append("\t\tnocompileSyntaxError(");
				sb.append("\"").append(fullFilename).append("\"");
				sb.append(");\r\n");
			} else {
				List<String> errors = errors(file);
				sb.append("\t\tnocompile(");
				sb.append("\"").append(fullFilename).append("\"");
				sb.append(", ");
				if(multiLineErrors.containsKey(name))
				{
					sb.append(multiLineErrors.get(name));
				}
				else
				{
					sb.append(errors.size());
				}
				sb.append(");\r\n\r\n");
				for(String error : errors)
				{
					sb.append("\t\t// ");
					// Need to replace a backslash-u with \ u
					// so Eclipse doesn't complain about an invalid
					// unicode sequence (even in comments!)
					sb.append(error.replace("\\u", "\\ u"));
					sb.append("\r\n");
				}
			}
			sb.append("\t}\r\n\r\n");
		}
		
		sb.append("\tprivate void nocompile(String file, int expectedErrors) throws Exception {\r\n");
		sb.append("char[] source = getContents(new File(file));\r\n" + 
				"		Parser parser = new Parser(AST.D1, source);\r\n" + 
				"		parser.filename = file.substring(DSTRESS_WHERE_PATH.length()).toCharArray();\r\n" + 
				"		final Module module = parser.parseModuleObj();\r\n" + 
				"		Global global = new Global();\r\n" + 
				"		global.params.warnings = false;\r\n" + 
				"		\r\n" + 
				"		SemanticContext context = new SemanticContext(new IProblemRequestor() {\r\n" + 
				"			public void acceptProblem(IProblem problem) {\r\n" + 
				"				module.problems.add(problem);\r\n" + 
				"			}\r\n" + 
				"			public void beginReporting() {\r\n" + 
				"			}\r\n" + 
				"			public void endReporting() {\r\n" + 
				"			}\r\n" + 
				"			public boolean isActive() {\r\n" + 
				"				return false;\r\n" + 
				"			}\r\n" + 
				"			\r\n" + 
				"		}, module, null, new DmdModuleFinder(global), global);\r\n" + 
				"		\r\n" + 
				"		if (!(module.problems != null && module.problems.size() > 0)) {\r\n" + 
				"			module.semantic(context);\r\n" + 
				"		}\r\n" + 
				"		\r\n" + 
				"		if (module.problems.size() != expectedErrors) {\r\n" + 
				"			fail(\"Expected \" + expectedErrors + \" errors but were \" + module.problems.size() + \": \" + module.problems.toString());\r\n" + 
				"		}\r\n");
		sb.append("\t}\r\n\r\n");
		
		sb.append("\tprivate void nocompileSyntaxError(String file) throws Exception {\r\n");
		sb.append("char[] source = getContents(new File(file));\r\n" + 
				"		Parser parser = new Parser(AST.D1, source);\r\n" + 
				"		parser.filename = file.substring(DSTRESS_WHERE_PATH.length()).toCharArray();\r\n" + 
				"		final Module module = parser.parseModuleObj();\r\n" + 
				"		Global global = new Global();\r\n" + 
				"		global.params.warnings = false;\r\n" + 
				"		\r\n" + 
				"		SemanticContext context = new SemanticContext(new IProblemRequestor() {\r\n" + 
				"			public void acceptProblem(IProblem problem) {\r\n" + 
				"				module.problems.add(problem);\r\n" + 
				"			}\r\n" + 
				"			public void beginReporting() {\r\n" + 
				"			}\r\n" + 
				"			public void endReporting() {\r\n" + 
				"			}\r\n" + 
				"			public boolean isActive() {\r\n" + 
				"				return false;\r\n" + 
				"			}\r\n" + 
				"			\r\n" + 
				"		}, module, null, new DmdModuleFinder(global), global);\r\n" + 
				"		\r\n" + 
				"		if (!(module.problems != null && module.problems.size() > 0)) {\r\n" + 
				"			module.semantic(context);\r\n" + 
				"		}\r\n" + 
				"		\r\n" + 
				"		if (module.problems.isEmpty()) {\r\n" + 
				"			fail(\"Expected at least one error\");\r\n" + 
				"		}\r\n");
		sb.append("\t}\r\n\r\n");
		
		sb.append(
				"\tprivate char[] getContents(File file) throws Exception {\r\n" + 
				"\t\tchar[] contents = new char[(int) file.length()];\r\n" + 
				"\t\tFileReader r = new FileReader(file);\r\n" + 
				"\t\tr.read(contents);\r\n" + 
				"\t\tr.close();\r\n" + 
				"\t\treturn contents;\r\n" + 
				"\t}\r\n");
		
		sb.append("\r\n");
		sb.append("}\r\n");
		
		FileWriter fw = new FileWriter("descent/tests/dstress/DstressNoCompile_Test.java");
		fw.write(sb.toString());
		fw.close();
	}
	
	private static List<File> listRecursive(File dir) {
		List<File> files = new ArrayList<File>();
		listRecursive(dir, files);		
		return files;
	}

	private static void listRecursive(File dir, List<File> acumulator) {
		File[] subFiles = dir.listFiles();
		if (subFiles == null) {
			return;
		}
		
		for(File subFile : subFiles) {
			if (subFile.isDirectory()) {
				listRecursive(subFile, acumulator);
			} else if (subFile.isFile() && subFile.getName().endsWith(".d")
					// Remove this condition when asm is supported by Descent
					&& !subFile.getName().startsWith("asm_")
					) {
				acumulator.add(subFile);
			}
		}
	}
	
	private static List<String> errors(File file) throws Exception {
		System.out.println("Processing " + file.getName() + "...");
		Process process = Runtime.getRuntime().exec("dmd " + file.getAbsolutePath() + " -o-");
		InputStreamReader reader = new InputStreamReader(process.getInputStream());
		BufferedReader br = new BufferedReader(reader);
		
		List<String> errors = new ArrayList<String>();
		
		String line;
		while((line = br.readLine()) != null) {
			if(!line.equals("")) {
				errors.add(line);
			}
		}
		br.close();
		
		return errors;
	}

}
