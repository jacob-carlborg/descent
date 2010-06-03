package descent.internal.formatter;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class DefaultCodeFormatterOptions
{	
	public enum TabChar
	{
		TAB(DefaultCodeFormatterConstants.TAB),
		SPACE(DefaultCodeFormatterConstants.SPACE),
		MIXED(DefaultCodeFormatterConstants.MIXED);
		
		private final String constVal;
		TabChar(String $constVal) { constVal = $constVal; }
		public String toString() { return constVal; }
	}
	
	public enum BracePosition
	{
		END_OF_LINE(DefaultCodeFormatterConstants.END_OF_LINE),
		NEXT_LINE(DefaultCodeFormatterConstants.NEXT_LINE),
		NEXT_LINE_SHIFTED(DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED);
		
		private final String constVal;
		BracePosition(String $constVal) { constVal = $constVal; }
		public String toString() { return constVal; }
	}
	
	public enum IndentationType
	{
		NO_INDENTATION(DefaultCodeFormatterConstants.NO_INDENTATION),
		INDENT_NORMAL(DefaultCodeFormatterConstants.INDENT_NORMAL),
		INDENT_HEADING_BACK(DefaultCodeFormatterConstants.INDENT_HEADING_BACK);
		
		private final String constVal;
		IndentationType(String $constVal) { constVal = $constVal; }
		public String toString() { return constVal; }
	}
	
	public static DefaultCodeFormatterOptions getDefaultSettings() {
		return getBuiltInProfile(DefaultCodeFormatterConstants.DEFAULT_PROFILE);
	}
	
	public static DefaultCodeFormatterOptions getBuiltInProfile(String name)
	{
		DefaultCodeFormatterOptions options = new DefaultCodeFormatterOptions();
		options.setDefaultSettings();
		
		if(name.equals(DefaultCodeFormatterConstants.PROFILE_JAVA_DEFAULTS))
		{
			/* EVAL-FOR-EACH
			 * if($$_{'javaDefault'})
			 * {
		     *     print DST "\t\t\toptions.$$_{'optName'} = $$_{'javaDefault'};\n";
		     * }
		     */
		}
		
		else if(name.equals(DefaultCodeFormatterConstants.PROFILE_C_SHARP_DEFAULTS))
		{
			/* EVAL-FOR-EACH
			 * if($$_{'csDefault'})
			 * {
		     *     print DST "\t\t\toptions.$$_{'optName'} = $$_{'csDefault'};\n";
		     * }
		     */
		}
		
		return options;
	}
	
	// User formatting options
	/* EVAL-FOR-EACH
	 * 
	 * print DST "\tpublic $$_{'type'} $$_{'optName'};\n";
	 *
	 */
	
	// Set by the caller
	public String line_separator;
    public int initial_indentation_level;
	
	private DefaultCodeFormatterOptions() {
		// cannot be instantiated
	}

	public DefaultCodeFormatterOptions(Map settings) {
		setDefaultSettings();
		if (settings == null) return;
		set(settings);
	}

	public Map<String, String> getMap() {
		
		Map<String, String> options = new HashMap<String, String>();
		
		/* EVAL-FOR-EACH
		 * 
		 * my $optionsMapInitializer;
		 * if($$_{'type'} eq "int")
		 * {
		 *     $optionsMapInitializer = "Integer.toString($$_{'optName'})";
		 * }
		 * elsif($$_{'type'} eq "String")
		 * {
		 *     $optionsMapInitializer = "$$_{'optName'}";
		 * }
		 * elsif($$_{'type'} eq "boolean")
		 * {
		 *     $optionsMapInitializer = "$$_{'optName'} ? DefaultCodeFormatterConstants.TRUE : DefaultCodeFormatterConstants.FALSE";
		 * }
		 * elsif($$_{'type'} eq "TabChar")
		 * {
		 *     $optionsMapInitializer = "$$_{'optName'}.toString()";
		 * }
		 * elsif($$_{'type'} eq "BracePosition")
		 * {
		 *     $optionsMapInitializer = "$$_{'optName'}.toString()";
		 * }
		 * elsif($$_{'type'} eq "IndentationType")
		 * {
		 *     $optionsMapInitializer = "$$_{'optName'}.toString()";
		 * }
		 * else
		 * {
		 *     $optionsMapInitializer = "null";
		 * }
		 * print DST "\t\toptions.put(DefaultCodeFormatterConstants.$$_{'constName'}, $optionsMapInitializer);\n";
		 *
		 */
		 
		return options;
	}

	public void set(Map<String, String> settings) {
		
		String current;
		
		/* EVAL-FOR-EACH
		 * 
		 * my $initializer;
		 * if($$_{'type'} eq "int")
		 * {
		 *     $initializer = "Integer.parseInt(current)";
		 * }
		 * elsif($$_{'type'} eq "String")
		 * {
		 *     $initializer = "current";
		 * }
		 * elsif($$_{'type'} eq "boolean")
		 * {
		 *     $initializer = "DefaultCodeFormatterConstants.TRUE.equals(current)";
		 * }
		 * elsif($$_{'type'} eq "TabChar")
		 * {
		 *     $initializer = "DefaultCodeFormatterConstants.MIXED.equals(current) ? " .
		 *                    "TabChar.MIXED : DefaultCodeFormatterConstants.SPACE.equals(current) ? " .
		 *                    "TabChar.SPACE : TabChar.TAB";
		 * }
		 * elsif($$_{'type'} eq "BracePosition")
		 * {
		 *     $initializer = "DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED.equals(current) ? " .
		 *                    "BracePosition.NEXT_LINE_SHIFTED : DefaultCodeFormatterConstants.NEXT_LINE.equals(current) ? " .
		 *                    "BracePosition.NEXT_LINE : BracePosition.END_OF_LINE";
		 * }
		 * elsif($$_{'type'} eq "IndentationType")
		 * {
		 *     $initializer = "DefaultCodeFormatterConstants.NO_INDENTATION.equals(current) ? " .
		 *                    "IndentationType.NO_INDENTATION : DefaultCodeFormatterConstants.INDENT_NORMAL.equals(current) ? " .
		 *                    "IndentationType.INDENT_NORMAL : IndentationType.INDENT_HEADING_BACK";
		 * }
		 * else
		 * {
		 *     $initializer = "null";
		 * }
		 * 
		 * print DST "\t\t\n";
		 * print DST "\t\tcurrent = settings.get(DefaultCodeFormatterConstants.$$_{'constName'});\n";
		 * print DST "\t\tif(null != current) {\n";
		 * print DST "\t\t\ttry {\n";
		 * print DST "\t\t\t\t$$_{'optName'} = $initializer;\n";
		 * print DST "\t\t\t} catch(Exception e) {\n";
		 * print DST "\t\t\t\t$$_{'optName'} = $$_{'default'};\n";
		 * print DST "\t\t\t}\n";
		 * print DST "\t\t}\n";
		 * 
		 */
	}

	public void setDefaultSettings() {
		/* EVAL-FOR-EACH
	     * print DST "\t\t$$_{'optName'} = $$_{'default'};\n";
	     */
	}
}
