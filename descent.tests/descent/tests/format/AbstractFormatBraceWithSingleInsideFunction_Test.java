package descent.tests.format;


public abstract class AbstractFormatBraceWithSingleInsideFunction_Test extends AbstractFormatBraceInsideFunction_Test {
	
	
	// TODO Descent formatter: make it configurable to write the substatement with an end line
	// Also, a space is missing after while(true)
	public void testSingleStatement() throws Exception {
		assertFormat(
				getFormattedPrefixForBrace() + "\r\n" +
					"\tint x;", 
				
					getUnformattedPrefixForBrace() + "  int   x ;"
			);
	}

}
