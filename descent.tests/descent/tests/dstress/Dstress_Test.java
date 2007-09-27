package descent.tests.dstress;

import static descent.tests.dstress.SpecificationsFactory.*;

import java.io.File;
import java.io.FileReader;
import java.util.LinkedHashMap;
import java.util.Map;

import descent.core.compiler.IProblem;
import descent.core.dom.AST;
import descent.core.dom.CompilationUnitResolver;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.Parser;
import descent.tests.mars.Parser_Test;

public class Dstress_Test extends Parser_Test implements IDstressConfiguration {
	
	private Map<String, ISpecification> specifications;
	
	@Override
	protected void setUp() throws Exception {
		specifications = new LinkedHashMap<String, ISpecification>();
		specifications.put("nocompile\\a\\alias_18.d", one(AST.D1, IProblem.AliasCannotBeConst, "const"));
		specifications.put("nocompile\\a\\alias_26_A.d", two(AST.D1, IProblem.DuplicatedSymbol, "alias void a;", 11, 1, IProblem.DuplicatedSymbol, "void main(){", 5, 4));
		// TODO specifications.put("nocompile\\a\\alias_26_B.d", twoProblems(AST.D1, IProblem.DuplicatedSymbol, "alias void a;", 11, 1, IProblem.DuplicatedSymbol, "void main(){", 5, 4));
		specifications.put("nocompile\\a\\alias_26_C.d", two(AST.D1, IProblem.DuplicatedSymbol, "alias int a;", 10, 1, IProblem.DuplicatedSymbol, "void main(){", 5, 4));
		specifications.put("nocompile\\a\\alias_26_D.d", two(AST.D1, IProblem.DuplicatedSymbol, "alias void a;", 11, 1, IProblem.DuplicatedSymbol, "void main(){", 5, 4));
		// TODO specifications.put("nocompile\\a\\alias_28_A.d", noProblems(AST.D1));
		specifications.put("nocompile\\a\\alias_28_B.d", one(AST.D1, IProblem.UndefinedProperty, "A.x", 2, 1));
		specifications.put("nocompile\\a\\alias_28_C.d", one(AST.D1, IProblem.UndefinedProperty, "a.x", 2, 1));
		specifications.put("nocompile\\a\\alias_28_D.d", one(AST.D1, IProblem.UndefinedProperty, "a.x", 2, 1));
		specifications.put("nocompile\\a\\alias_28_E.d", one(AST.D1, IProblem.UndefinedProperty, "A.x", 2, 1));
		// TODO specifications.put("nocompile\\a\\alias_28_F.d", noProblems(AST.D1));
		specifications.put("nocompile\\a\\alias_30_A.d", one(AST.D1, IProblem.CircularDefinition, "Foo;", 0, 3));
		specifications.put("nocompile\\a\\alias_30_B.d", one(AST.D1, IProblem.CircularDefinition, "Foo;", 0, 3));
		specifications.put("nocompile\\a\\alias_30_C.d", one(AST.D1, IProblem.CircularDefinition, "Foo;", 0, 3));
		specifications.put("nocompile\\a\\alias_30_D.d", two(AST.D1, IProblem.DuplicatedSymbol, "struct Foo", 7, 3, IProblem.DuplicatedSymbol, "Foo;", 0, 3));
		specifications.put("nocompile\\a\\alias_30_E.d", two(AST.D1, IProblem.DuplicatedSymbol, "class Foo", 6, 3, IProblem.DuplicatedSymbol, "Foo;", 0, 3));
		specifications.put("nocompile\\a\\alias_30_F.d", two(AST.D1, IProblem.DuplicatedSymbol, "template Foo", 9, 3, IProblem.DuplicatedSymbol, "Foo;", 0, 3));
		specifications.put("nocompile\\a\\alias_30_G.d", one(AST.D1, IProblem.CircularDefinition, "alias b a;", 8, 1));
		specifications.put("nocompile\\a\\alias_30_L.d", one(AST.D1, IProblem.CircularDefinition, "alias a a;", 8, 1));
		specifications.put("nocompile\\a\\alias_33_A.d", four(AST.D1, IProblem.UndefinedIdentifier, "alias UNDEFINED", 6, 9, IProblem.UndefinedIdentifier, "alias UNDEFINED", 6, 9, IProblem.UsedAsAType, "alias UNDEFINED", 6, 9, IProblem.VoidsHaveNoValue, "ALIAS x", 0, 5));
		specifications.put("nocompile\\a\\alias_33_B.d", four(AST.D1, IProblem.UndefinedIdentifier, "alias UNDEFINED", 6, 9, IProblem.UsedAsAType, "alias UNDEFINED", 6, 9, IProblem.VoidsHaveNoValue, "ALIAS x", 0, 5, IProblem.UndefinedIdentifier, "alias UNDEFINED", 6, 9));
		specifications.put("nocompile\\a\\alias_33_C.d", four(AST.D1, IProblem.UndefinedIdentifier, "ALIAS x;", 0, 5, IProblem.UsedAsAType, "ALIAS x;", 0, 5, IProblem.VoidsHaveNoValue, "ALIAS x", 0, 5, IProblem.UndefinedIdentifier, "alias UNDEFINED", 6, 9));
		specifications.put("nocompile\\a\\alias_33_D.d", four(AST.D1, IProblem.UndefinedIdentifier, "alias UNDEFINED", 6, 9, IProblem.UndefinedIdentifier, "alias UNDEFINED", 6, 9, IProblem.UsedAsAType, "alias UNDEFINED", 6, 9, IProblem.VoidsHaveNoValue, "ALIAS x", 0, 5));
		specifications.put("nocompile\\a\\alias_34_A.d", three(AST.D1, IProblem.UndefinedIdentifier, "(Foo)", 1, 3, IProblem.UsedAsAType, "(Foo)", 1, 3, IProblem.CannotHaveParameterOfTypeVoid, "(Foo)", 1, 3));
	}
	
	public void testDstress() throws Exception {
		StringBuilder sb = new StringBuilder();
		
		for(Map.Entry<String, ISpecification> entry : specifications.entrySet()) {
			String filename = entry.getKey();
			ISpecification spec = entry.getValue();
			
			File file = new File(DSTRESS_PATH, filename);
			char[] source = getContents(file);
			
			Parser parser = new Parser(spec.getApiLevel(), source);
			Module module = parser.parseModuleObj();
			CompilationUnitResolver.resolve(module);
			
			try {
				spec.validate(source, module);
			} catch (Exception e) {
				sb.append("\n");
				sb.append(filename);
				sb.append(": ");
				sb.append(e.getMessage());
			}
		}
		
		if (sb.length() != 0) {
			throw new Exception(sb.toString());
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
