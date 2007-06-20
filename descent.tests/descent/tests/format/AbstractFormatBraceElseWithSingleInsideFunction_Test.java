package descent.tests.format;


public abstract class AbstractFormatBraceElseWithSingleInsideFunction_Test extends AbstractFormatBraceElseInsideFunction_Test {
	
	
	// TODO Descent formatter: make it configurable to write the substatement with an end line
	// Also, a space is missing after while(true)
	public void testSingleStatement() throws Exception {
		assertFormat(
				getFormattedPrefixForBrace() + "\r\n" +
					"\tint x;", 
				
					getUnformattedPrefixForBrace() + "  int   x ;"
			);
	}
	
	// TODO make a test for else, as well

}
