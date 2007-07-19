package descent.tests.ddoc;

import java.util.HashMap;
import java.util.Map;

import descent.internal.ui.infoviews.DdocMacros;
import descent.internal.ui.infoviews.DdocParser;
import descent.internal.ui.infoviews.DdocSection;
import junit.framework.TestCase;

public class DdocParserTests extends TestCase {
	
	public void testOneLine() {
		DdocSection[] sections = parse(
			"/**\r\n" +
			" * Line one.\r\n" +
			" */"
				);
		
		assertEquals(1, sections.length);
		assertEquals(null, sections[0].getName());
		assertEquals(DdocSection.NORMAL_SECTION, sections[0].getKind());
		assertEquals("Line one.", sections[0].getText());
	}
	
	public void testOneLineWithoutStart() {
		DdocSection[] sections = parse(
			"/**\r\n" +
			"  Line one.\r\n" +
			" */"
				);
		
		assertEquals(1, sections.length);
		assertEquals(null, sections[0].getName());
		assertEquals(DdocSection.NORMAL_SECTION, sections[0].getKind());
		assertEquals("Line one.", sections[0].getText());
	}
	
	public void testOneLineWithLeadingInFirstLine() {
		DdocSection[] sections = parse(
			"/********************\r\n" +
			" * Line one.\r\n" +
			" */"
				);
		
		assertEquals(1, sections.length);
		assertEquals(null, sections[0].getName());
		assertEquals(DdocSection.NORMAL_SECTION, sections[0].getKind());
		assertEquals("Line one.", sections[0].getText());
	}
	
	public void testOneLineWithLeadingInLastLine() {
		DdocSection[] sections = parse(
			"/**\r\n" +
			" * Line one.\r\n" +
			" ***************************/"
				);
		
		assertEquals(1, sections.length);
		assertEquals(null, sections[0].getName());
		assertEquals(DdocSection.NORMAL_SECTION, sections[0].getKind());
		assertEquals("Line one.", sections[0].getText());
	}
	
	public void testOneLineWithLeadingInLastLineWithText() {
		DdocSection[] sections = parse(
			"/**\r\n" +
			" * Line one. ***************************/"
				);
		
		assertEquals(1, sections.length);
		assertEquals(null, sections[0].getName());
		assertEquals(DdocSection.NORMAL_SECTION, sections[0].getKind());
		assertEquals("Line one.", sections[0].getText());
	}
	
	public void testOneLineWithTextAfterStars() {
		DdocSection[] sections = parse(
			"/** Don't ignore me.\r\n" +
			" */"
				);
		
		assertEquals(1, sections.length);
		assertEquals(null, sections[0].getName());
		assertEquals(DdocSection.NORMAL_SECTION, sections[0].getKind());
		assertEquals("Don't ignore me.", sections[0].getText());
	}
	
	public void testTwoLines() {
		DdocSection[] sections = parse(
			"/**\r\n" +
			" * Line one.\r\n" +
			" * Line two.\r\n" +
			" */"
				);
		
		assertEquals(1, sections.length);
		assertEquals(null, sections[0].getName());
		assertEquals(DdocSection.NORMAL_SECTION, sections[0].getKind());
		assertEquals("Line one. Line two.", sections[0].getText());
	}
	
	public void testTwoSections() {
		DdocSection[] sections = parse(
			"/**\r\n" +
			" * Line one.\r\n" +
			" *\r\n" +
			" * Line two.\r\n" +
			" */"
				);
		
		assertEquals(2, sections.length);
		assertEquals(null, sections[0].getName());
		assertEquals(DdocSection.NORMAL_SECTION, sections[0].getKind());
		assertEquals("Line one.", sections[0].getText());
		
		assertEquals(null, sections[1].getName());
		assertEquals(DdocSection.NORMAL_SECTION, sections[1].getKind());
		assertEquals("Line two.", sections[1].getText());
	}
	
	public void testTwoSectionsOfManyLines() {
		DdocSection[] sections = parse(
			"/**\r\n" +
			" * Line one A.\r\n" +
			" * Line one B.\r\n" +
			" * Line one C.\r\n" +
			" *\r\n" +
			" * Line two A.\r\n" +
			" * Line two B.\r\n" +
			" * Line two C.\r\n" +
			" */"
				);
		
		assertEquals(2, sections.length);
		assertEquals(null, sections[0].getName());
		assertEquals(DdocSection.NORMAL_SECTION, sections[0].getKind());
		assertEquals("Line one A. Line one B. Line one C.", sections[0].getText());
		
		assertEquals(null, sections[1].getName());
		assertEquals(DdocSection.NORMAL_SECTION, sections[1].getKind());
		assertEquals("Line two A. Line two B. Line two C.", sections[1].getText());
	}
	
	public void testTwoSectionsOfManyLinesWithPlus() {
		DdocSection[] sections = parse(
			"/++\r\n" +
			" + Line one A.\r\n" +
			" + Line one B.\r\n" +
			" + Line one C.\r\n" +
			" +\r\n" +
			" + Line two A.\r\n" +
			" + Line two B.\r\n" +
			" + Line two C.\r\n" +
			" +/"
				);
		
		assertEquals(2, sections.length);
		assertEquals(null, sections[0].getName());
		assertEquals(DdocSection.NORMAL_SECTION, sections[0].getKind());
		assertEquals("Line one A. Line one B. Line one C.", sections[0].getText());
		
		assertEquals(null, sections[1].getName());
		assertEquals(DdocSection.NORMAL_SECTION, sections[1].getKind());
		assertEquals("Line two A. Line two B. Line two C.", sections[1].getText());
	}
	
	public void testNamedSectionIsNotIdentifier() {
		DdocSection[] sections = parse(
			"/**\r\n" +
			" * Authors(andothers): Melvin D. Nerd, melvin@mailinator.com\r\n" +
			" */"
				);
		
		assertEquals(1, sections.length);
		assertEquals(null, sections[0].getName());
		assertEquals(DdocSection.NORMAL_SECTION, sections[0].getKind());
		assertEquals("Authors(andothers): Melvin D. Nerd, melvin@mailinator.com", sections[0].getText());
	}
	
	public void testNamedSectionWithOneLine() {
		DdocSection[] sections = parse(
			"/**\r\n" +
			" * Authors: Melvin D. Nerd, melvin@mailinator.com\r\n" +
			" */"
				);
		
		assertEquals(1, sections.length);
		assertEquals("Authors", sections[0].getName());
		assertEquals(DdocSection.NORMAL_SECTION, sections[0].getKind());
		assertEquals("Melvin D. Nerd, melvin@mailinator.com", sections[0].getText());
	}
	
	public void testNamedSectionWithTwoLines() {
		DdocSection[] sections = parse(
			"/**\r\n" +
			" * Authors:\r\n" +
			" * Melvin D. Nerd, melvin@mailinator.com\r\n" +
			" */"
				);
		
		assertEquals(1, sections.length);
		assertEquals("Authors", sections[0].getName());
		assertEquals(DdocSection.NORMAL_SECTION, sections[0].getKind());
		assertEquals("Melvin D. Nerd, melvin@mailinator.com", sections[0].getText());
	}
	
	public void testNamedSectionWithTwoSeparatedLines() {
		DdocSection[] sections = parse(
			"/**\r\n" +
			" * Authors:\r\n" +
			" * Melvin D. Nerd, melvin@mailinator.com\r\n" +
			" *\r\n" +
			" * Another author.\r\n" +
			" */"
				);
		
		assertEquals(1, sections.length);
		assertEquals("Authors", sections[0].getName());
		assertEquals(DdocSection.NORMAL_SECTION, sections[0].getKind());
		assertEquals("Melvin D. Nerd, melvin@mailinator.com  Another author.", sections[0].getText());
	}
	
	public void testNamedSectionEmpty() {
		DdocSection[] sections = parse(
			"/**\r\n" +
			" * Authors:\r\n" +
			" */"
				);
		
		assertEquals(1, sections.length);
		assertEquals("Authors", sections[0].getName());
		assertEquals(DdocSection.NORMAL_SECTION, sections[0].getKind());
		assertEquals("", sections[0].getText());
	}
	
	public void testNamedSectionEmpty2() {
		DdocSection[] sections = parse(
			"/**\r\n" +
			" * Authors:\r\n" +
			" * Examples:\r\n" +
			" */"
				);
		
		assertEquals(2, sections.length);
		
		assertEquals("Authors", sections[0].getName());
		assertEquals(DdocSection.NORMAL_SECTION, sections[0].getKind());
		assertEquals("", sections[0].getText());
		
		assertEquals("Examples", sections[1].getName());
		assertEquals(DdocSection.NORMAL_SECTION, sections[1].getKind());
		assertEquals("", sections[1].getText());
	}
	
	public void testCode() {
		DdocSection[] sections = parse(
			"/**\r\n" +
			" * ---\r\n" +
			" * int x = 2;\r\n" +
			" * ---\r\n" +
			" */"
				);
		
		assertEquals(1, sections.length);
		assertEquals(null, sections[0].getName());
		assertEquals(DdocSection.CODE_SECTION, sections[0].getKind());
		assertEquals("int x = 2;", sections[0].getText());
	}
	
	public void testCode2() {
		DdocSection[] sections = parse(
			"/**\r\n" +
			" * Example:\r\n" +
			" *\r\n" +
			" * ---\r\n" +
			" * int x = 2;\r\n" +
			" * ---\r\n" +
			" */"
				);
		
		assertEquals(2, sections.length);
		
		assertEquals("Example", sections[0].getName());
		assertEquals("", sections[0].getText());
		assertEquals(DdocSection.NORMAL_SECTION, sections[0].getKind());
		
		assertEquals(null, sections[1].getName());
		assertEquals("int x = 2;", sections[1].getText());
		assertEquals(DdocSection.CODE_SECTION, sections[1].getKind());
	}
	
	public void testCodeWithSomethingBefore() {
		DdocSection[] sections = parse(
			"/**\r\n" +
			" * Authors: Melvin D. Nerd, melvin@mailinator.com\r\n" +
			" * ---\r\n" +
			" * int x = 2;\r\n" +
			" * ---\r\n" +
			" */"
				);
		
		assertEquals(2, sections.length);
		
		assertEquals("Authors", sections[0].getName());
		assertEquals(DdocSection.NORMAL_SECTION, sections[0].getKind());
		assertEquals("Melvin D. Nerd, melvin@mailinator.com", sections[0].getText());
		
		assertEquals(null, sections[1].getName());
		assertEquals(DdocSection.CODE_SECTION, sections[1].getKind());
		assertEquals("int x = 2;", sections[1].getText());
	}
	
	public void testCodeWithSomethingAfter() {
		DdocSection[] sections = parse(
			"/**\r\n" +
			" * ---\r\n" +
			" * int x = 2;\r\n" +
			" * ---\r\n" +
			" * Authors: Melvin D. Nerd, melvin@mailinator.com\r\n" +
			" */"
				);
		
		assertEquals(2, sections.length);
		
		assertEquals(null, sections[0].getName());
		assertEquals(DdocSection.CODE_SECTION, sections[0].getKind());
		assertEquals("int x = 2;", sections[0].getText());
		
		assertEquals("Authors", sections[1].getName());
		assertEquals(DdocSection.NORMAL_SECTION, sections[1].getKind());
		assertEquals("Melvin D. Nerd, melvin@mailinator.com", sections[1].getText());	
	}
	
	public void testCodeWithSectionInItStartsANewSection() {
		DdocSection[] sections = parse(
			"/**\r\n" +
			" * ---\r\n" +
			" * int x = 2;\r\n" +
			" * Authors: Melvin D. Nerd, melvin@mailinator.com\r\n" +
			" * ---\r\n" +
			" */"
				);
		
		assertEquals(2, sections.length);
		
		assertEquals(null, sections[0].getName());
		assertEquals(DdocSection.NORMAL_SECTION, sections[0].getKind());
		assertEquals("---  int x = 2;", sections[0].getText());
		
		assertEquals(" Authors", sections[1].getName());
		assertEquals(DdocSection.NORMAL_SECTION, sections[1].getKind());
		assertEquals("Melvin D. Nerd, melvin@mailinator.com", sections[1].getText());
	}
	
	public void testCodeBug() {
		DdocSection[] sections = parse(
			"/**\r\n" +
			" * ---\r\n" +
			" * class ConduitFilter\r\n" +
			"   {\r\n" +
			"   \r\n" +
			"   int x\r\n" +
			"   }\r\n" +
			"   ---\r\n" +
			" */"
				);
		
		assertEquals(1, sections.length);
		
		assertEquals(null, sections[0].getName());
		assertEquals(DdocSection.CODE_SECTION, sections[0].getKind());
	}
	
	
	
	public void testParameters() {
		DdocSection[] sections = parse(
			"/**\r\n" +
			" * Params:\r\n" +
			" *     x = something\r\n" +
			" *     y = somethingElse\r\n" +
			" */"
				);
		
		assertEquals(1, sections.length);
		assertEquals("Params", sections[0].getName());
		assertEquals(DdocSection.PARAMS_SECTION, sections[0].getKind());
		assertEquals("x = something y = somethingElse", sections[0].getText());
		assertEquals(2, sections[0].getParameters().length);
		
		assertEquals("x", sections[0].getParameters()[0].getName());
		assertEquals("something", sections[0].getParameters()[0].getText());
		
		assertEquals("y", sections[0].getParameters()[1].getName());
		assertEquals("somethingElse ", sections[0].getParameters()[1].getText());
	}
	
	public void testParametersMultiline() {
		DdocSection[] sections = parse(
			"/**\r\n" +
			" * Params:\r\n" +
			" *     x = something\r\n" +
			" *         and more\r\n" +
			" */"
				);
		
		assertEquals(1, sections.length);
		assertEquals(1, sections[0].getParameters().length);
		
		assertEquals("x", sections[0].getParameters()[0].getName());
		assertEquals("something and more ", sections[0].getParameters()[0].getText());
	}
	
	public void testParametersIgnoreBeforeParam() {
		DdocSection[] sections = parse(
			"/**\r\n" +
			" * Params:\r\n" +
			" *     this is not a parameter\r\n" +
			" *     x = something\r\n" +
			" */"
				);
		
		assertEquals(1, sections.length);
		assertEquals(1, sections[0].getParameters().length);
		
		assertEquals("x", sections[0].getParameters()[0].getName());
		assertEquals("something ", sections[0].getParameters()[0].getText());
	}
	
	public void testParametersInline() {
		DdocSection[] sections = parse(
			"/**\r\n" +
			" * Params: x = something\r\n" +
			" */"
				);
		
		assertEquals(1, sections.length);
		assertEquals(1, sections[0].getParameters().length);
		
		assertEquals("x", sections[0].getParameters()[0].getName());
		assertEquals("something ", sections[0].getParameters()[0].getText());
	}
	
	public void testParametersWithSomethingAfter() {
		DdocSection[] sections = parse(
			"/**\r\n" +
			" * Params:\r\n" +
			" *     x = something\r\n" +
			" *     y = somethingElse\r\n" +
			" * Returns:\r\n" +
			" *     an object" +
			" */"
				);
		
		assertEquals(2, sections.length);
		
		assertEquals("Params", sections[0].getName());
		assertEquals(DdocSection.PARAMS_SECTION, sections[0].getKind());
		assertEquals("x = something y = somethingElse", sections[0].getText());
		assertEquals(2, sections[0].getParameters().length);
		
		assertEquals("x", sections[0].getParameters()[0].getName());
		assertEquals("something", sections[0].getParameters()[0].getText());
		
		assertEquals("y", sections[0].getParameters()[1].getName());
		assertEquals("somethingElse", sections[0].getParameters()[1].getText());
		
		assertEquals("Returns", sections[1].getName());
		assertEquals(DdocSection.NORMAL_SECTION, sections[1].getKind());
		assertEquals("an object", sections[1].getText());
	}
	
	public void testMacros() {
		DdocSection[] sections = parse(
			"/**\r\n" +
			" * Macros:\r\n" +
			" *     x = something\r\n" +
			" *     y = somethingElse\r\n" +
			" */"
				);
		
		assertEquals(1, sections.length);
		assertEquals("Macros", sections[0].getName());
		assertEquals(DdocSection.MACROS_SECTION, sections[0].getKind());
		assertEquals("x = something y = somethingElse", sections[0].getText());
		assertEquals(2, sections[0].getParameters().length);
		
		assertEquals("x", sections[0].getParameters()[0].getName());
		assertEquals("something", sections[0].getParameters()[0].getText());
		
		assertEquals("y", sections[0].getParameters()[1].getName());
		assertEquals("somethingElse ", sections[0].getParameters()[1].getText());
	}
	
	public void testReplaceMacro() {
		Map<String, String> macros = new HashMap<String, String>();
		macros.put("MACRO", "my macro");
		
		String actual = DdocMacros.replaceMacros("This is $(MACRO)", macros);
		assertEquals("This is my macro", actual);
	}
	
	public void testDontReplaceMacro1() {
		Map<String, String> macros = new HashMap<String, String>();
		macros.put("MACRO", "my macro");
		
		String actual = DdocMacros.replaceMacros("This is $", macros);
		assertEquals("This is $", actual);
	}
	
	public void testDontReplaceMacro2() {
		Map<String, String> macros = new HashMap<String, String>();
		macros.put("MACRO", "my macro");
		
		String actual = DdocMacros.replaceMacros("This is $(", macros);
		assertEquals("This is $(", actual);
	}
	
	public void testReplaceMacroArgumentZero1() {
		Map<String, String> macros = new HashMap<String, String>();
		macros.put("MACRO", "my $0");
		
		String actual = DdocMacros.replaceMacros("This is $(MACRO macro)", macros);
		assertEquals("This is my macro", actual);
	}
	
	public void testReplaceMacroArgumentZero2() {
		Map<String, String> macros = new HashMap<String, String>();
		macros.put("MACRO", "$0");
		
		String actual = DdocMacros.replaceMacros("This is $(MACRO my,macro)", macros);
		assertEquals("This is my,macro", actual);
	}
	
	public void testReplaceMacroArgumentZero3() {
		Map<String, String> macros = new HashMap<String, String>();
		macros.put("MACRO", "my $0");
		
		String actual = DdocMacros.replaceMacros("This is $(MACRO macro\nmacro2)", macros);
		assertEquals("This is my macro\nmacro2", actual);
	}
	
	public void testReplaceMacroArgumentZeroCountParenthesis() {
		Map<String, String> macros = new HashMap<String, String>();
		macros.put("ONE", "<a>$0</a>");
		
		String actual = DdocMacros.replaceMacros("$(ONE some())", macros);
		assertEquals("<a>some()</a>", actual);
	}
	
	public void testReplaceMacroArguments2() {
		Map<String, String> macros = new HashMap<String, String>();
		macros.put("MACRO", "$1 $2");
		
		String actual = DdocMacros.replaceMacros("This is $(MACRO my,macro)", macros);
		assertEquals("This is my macro", actual);
	}
	
	public void testReplaceMacroArguments3() {
		Map<String, String> macros = new HashMap<String, String>();
		macros.put("MACRO", "$3");
		
		String actual = DdocMacros.replaceMacros("This is $(MACRO my,macro)", macros);
		assertEquals("This is my,macro", actual);
	}
	
	public void testReplaceMacroArgumentsPlus() {
		Map<String, String> macros = new HashMap<String, String>();
		macros.put("MACRO", "$+");
		
		String actual = DdocMacros.replaceMacros("This is $(MACRO one,two,three)", macros);
		assertEquals("This is two,three", actual);
	}
	
	public void testReplaceRecursive() {
		Map<String, String> macros = new HashMap<String, String>();
		macros.put("ONE", "I use $(TWO)");
		macros.put("TWO", "two");
		
		String actual = DdocMacros.replaceMacros("$(ONE)", macros);
		assertEquals("I use two", actual);
	}
	
	public void testReplaceRecursive2() {
		Map<String, String> macros = new HashMap<String, String>();
		macros.put("I", "<i>$0</i>");
		macros.put("D_PARAM", "$(I $0)");
		
		String actual = DdocMacros.replaceMacros("$(D_PARAM count)", macros);
		assertEquals("<i>count</i>", actual);
	}
	
	public void testDontHangOnMe() {
		Map<String, String> macros = new HashMap<String, String>();
		macros.put("RECURSIVE", "I'm very $(RECURSIVE)");
		
		String actual = DdocMacros.replaceMacros("$(RECURSIVE)", macros);
		assertEquals("I'm very ", actual);
	}
	
	public void testDontHangOnMe2() {
		Map<String, String> macros = new HashMap<String, String>();
		macros.put("CHEATER", "I'm very $(TRICKY)");
		macros.put("TRICKY", "I'm very $(CHEATER)");
		
		String actual = DdocMacros.replaceMacros("$(CHEATER)", macros);
		assertEquals("I'm very I'm very ", actual);
	}
	
	public void testDontHangOnMeButAllowMultiple() {
		Map<String, String> macros = new HashMap<String, String>();
		macros.put("THIS", "this");
		
		String actual = DdocMacros.replaceMacros("Duplicate $(THIS) $(THIS)", macros);
		assertEquals("Duplicate this this", actual);
	}
	
	public void testReplaceNested() {
		Map<String, String> macros = new HashMap<String, String>();
		macros.put("ONE", "one");
		macros.put("TWO", "Parameter: $0");
		
		String actual = DdocMacros.replaceMacros("$(TWO $(ONE))", macros);
		assertEquals("Parameter: one", actual);
	}
	
	public void testReplaceNested2() {
		Map<String, String> macros = new HashMap<String, String>();
		macros.put("ONE", "one");
		macros.put("TWO", "Parameter: $0");
		macros.put("THREE", "Argument: $0");
		
		String actual = DdocMacros.replaceMacros("$(THREE $(TWO $(ONE)))", macros);
		assertEquals("Argument: Parameter: one", actual);
	}
	
	public void testReplaceNestedMany() {
		Map<String, String> macros = new HashMap<String, String>();
		macros.put("ONE", "one");
		macros.put("TWO", "Parameter: $0");
		
		String actual = DdocMacros.replaceMacros("$(TWO $(ONE) $(ONE))", macros);
		assertEquals("Parameter: one one", actual);
	}
	
	private DdocSection[] parse(String text) {
		return new DdocParser(text).parse().getSections();
	}

}
