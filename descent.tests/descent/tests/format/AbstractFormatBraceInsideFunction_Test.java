package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public abstract class AbstractFormatBraceInsideFunction_Test extends AbstractFormatInsideFunction_Test {
	
	protected abstract String getFormattedPrefixForBrace();
	
	protected abstract String getUnformattedPrefixForBrace();
	
	protected abstract String getBracePositionOptionName();
	
	protected boolean needsSemicolon() {
		return false;
	}
	
	protected boolean ignoreWithComments() {
		return false;
	}
	
	private String sc() {
		 return needsSemicolon() ? ";" : "";
	}
	
	public void testBracesAtEndOfLine() throws Exception {
		assertFormat(
				getFormattedPrefixForBrace() + " {\r\n" +
				"}" + sc(), 
				
				getUnformattedPrefixForBrace() + " {    }" + sc()
			);
	}
	
	public void testBracesNextLine() throws Exception {
		Map options = new HashMap();
		options.put(getBracePositionOptionName(), DefaultCodeFormatterConstants.NEXT_LINE);
		assertFormat(
				getFormattedPrefixForBrace() + "\r\n" +
				"{\r\n" +
				"}" + sc(), 
				
				getUnformattedPrefixForBrace() + "  {    }" + sc(),
				
				options
			);
	}
	
	public void testBracesNextLineShifted() throws Exception {
		Map options = new HashMap();
		options.put(getBracePositionOptionName(), DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED);
		assertFormat(
				getFormattedPrefixForBrace() + "\r\n" +
					"\t{\r\n" +
					"\t}" + sc(), 
				
				getUnformattedPrefixForBrace() + "  {    }" + sc(),
				
				options
			);
	}
	
	public void testWithComments() throws Exception {
		if (ignoreWithComments()) {
			return;
		}
		
		assertFormat(
				"/*\r\n" +
				" * Some\r\n" +
				" * comment\r\n" +
				" */\r\n" +
				getFormattedPrefixForBrace() + " { // comment\r\n" +
				"}" + sc(),
				
				"/*\r\n" +
				" * Some\r\n" +
				" * comment\r\n" +
				" */\r\n" +
				getUnformattedPrefixForBrace() +  "  { // comment\r\n   }" + sc()
			);
	}
	
	public void testBracesNextLineShiftedWithMembers() throws Exception {
		Map options = new HashMap();
		options.put(getBracePositionOptionName(), DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED);
		assertFormat(
				getFormattedPrefixForBrace() + "\r\n" +
					"\t{\r\n" +
					"\t\tint x;\r\n" +
					"\t}" + sc(), 
				
				getUnformattedPrefixForBrace() +  " {  int x;  }" + sc(),
				
				options
			);
	}

}
