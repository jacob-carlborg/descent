package descent.internal.formatter;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants;
import descent.internal.formatter.align.Alignment2;

public class DefaultCodeFormatterOptions
{
	// TODO different default profiles? (or just get rid of the unused ones)
	
	public static DefaultCodeFormatterOptions getDefaultSettings() {
		DefaultCodeFormatterOptions options = new DefaultCodeFormatterOptions();
		options.setDefaultSettings();
		return options;
	}
	
	public static DefaultCodeFormatterOptions getEclipseDefaultSettings() {
		DefaultCodeFormatterOptions options = new DefaultCodeFormatterOptions();
		options.setDefaultSettings();
		return options;
	}

	public static DefaultCodeFormatterOptions getJavaConventionsSettings() {
		DefaultCodeFormatterOptions options = new DefaultCodeFormatterOptions();
		options.setDefaultSettings();
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

	private String getAlignment2(int alignment) {
		return Integer.toString(alignment);
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
