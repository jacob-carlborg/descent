package descent.tests.ddoc;

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
		assertEquals("Line one.\nLine two.", sections[0].getText());
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
		assertEquals("Line one A.\nLine one B.\nLine one C.", sections[0].getText());
		
		assertEquals(null, sections[1].getName());
		assertEquals(DdocSection.NORMAL_SECTION, sections[1].getKind());
		assertEquals("Line two A.\nLine two B.\nLine two C.", sections[1].getText());
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
		assertEquals("Line one A.\nLine one B.\nLine one C.", sections[0].getText());
		
		assertEquals(null, sections[1].getName());
		assertEquals(DdocSection.NORMAL_SECTION, sections[1].getKind());
		assertEquals("Line two A.\nLine two B.\nLine two C.", sections[1].getText());
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
		assertEquals("Melvin D. Nerd, melvin@mailinator.com\n\nAnother author.", sections[0].getText());
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
		assertEquals("---\n int x = 2;", sections[0].getText());
		
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
		assertEquals("x = something\ny = somethingElse", sections[0].getText());
		assertEquals(2, sections[0].getParameters().length);
		
		assertEquals("x", sections[0].getParameters()[0].getName());
		assertEquals("something", sections[0].getParameters()[0].getText());
		
		assertEquals("y", sections[0].getParameters()[1].getName());
		assertEquals("somethingElse\n", sections[0].getParameters()[1].getText());
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
		assertEquals("something\nand more\n", sections[0].getParameters()[0].getText());
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
		assertEquals("something\n", sections[0].getParameters()[0].getText());
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
		assertEquals("something\n", sections[0].getParameters()[0].getText());
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
		assertEquals("x = something\ny = somethingElse", sections[0].getText());
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
		assertEquals("x = something\ny = somethingElse", sections[0].getText());
		assertEquals(2, sections[0].getParameters().length);
		
		assertEquals("x", sections[0].getParameters()[0].getName());
		assertEquals("something", sections[0].getParameters()[0].getText());
		
		assertEquals("y", sections[0].getParameters()[1].getName());
		assertEquals("somethingElse\n", sections[0].getParameters()[1].getText());
	}
	
	private DdocSection[] parse(String text) {
		return new DdocParser(text).parse().getSections();
	}

}
