package descent.internal.formatter;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants2;
import descent.internal.formatter.align.Alignment2;

public class DefaultCodeFormatterOptions2
{
	public static final int TAB = 1;
	public static final int SPACE = 2;
	public static final int MIXED = 4;
	
	// TODO different default profiles? (or just get rid of the unused ones)
	
	public static DefaultCodeFormatterOptions2 getDefaultSettings() {
		DefaultCodeFormatterOptions2 options = new DefaultCodeFormatterOptions2();
		options.setDefaultSettings();
		return options;
	}
	
	public static DefaultCodeFormatterOptions2 getEclipseDefaultSettings() {
		DefaultCodeFormatterOptions2 options = new DefaultCodeFormatterOptions2();
		options.setDefaultSettings();
		return options;
	}

	public static DefaultCodeFormatterOptions2 getJavaConventionsSettings() {
		DefaultCodeFormatterOptions2 options = new DefaultCodeFormatterOptions2();
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
	
	private DefaultCodeFormatterOptions2() {
		// cannot be instantiated
	}

	public DefaultCodeFormatterOptions2(Map settings) {
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
		 *     $optionsMapInitializer = "$$_{'optName'} ? DefaultCodeFormatterConstants2.TRUE : DefaultCodeFormatterConstants2.FALSE";
		 * }
		 * else
		 * {
		 *     $optionsMapInitializer = "null";
		 * }
		 * print DST "\t\toptions.put(DefaultCodeFormatterConstants2.$$_{'constName'}, $optionsMapInitializer);\n";
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
		 *     $initializer = "DefaultCodeFormatterConstants2.TRUE.equals(current)";
		 * }
		 * else
		 * {
		 *     $initializer = "null";
		 * }
		 * 
		 * print DST "\t\t\n";
		 * print DST "\t\tcurrent = settings.get(DefaultCodeFormatterConstants2.$$_{'constName'});\n";
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
