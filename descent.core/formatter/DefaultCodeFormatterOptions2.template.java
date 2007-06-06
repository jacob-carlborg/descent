package descent.internal.formatter;

import java.util.HashMap;
import java.util.Map;

import descent.core.formatter.DefaultCodeFormatterConstants2;
import descent.core.JavaCore;
import descent.internal.formatter.align.Alignment2;

public class DefaultCodeFormatterOptions2
{
	public static final int TAB = 1;
	public static final int SPACE = 2;
	public static final int MIXED = 4;
	
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
	 * print DST "\tpublic $type $optName;\n";
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

	public Map getMap() {
		Map options = new HashMap();
		
		/* EVAL-FOR-EACH
		 * 
		 * my $optionsMapInitializer;
		 * #if($type eq "boolean")
		 * #{
		 * #   $optionsMapInitializer = "$optName ? DefaultCodeFormatterConstants2.TRUE : DefaultCodeFormatterConstants2.FALSE";
		 * #}
		 * #elsif($type eq "int")
		 * #{
		 * #    $optionsMapInitializer = "new Integer($optName)";
		 * #}
		 * #else
		 * #{
		 * #    $optionsMapInitializer = $optName;
		 * #}
		 * $optionsMapInitializer = "null";
		 * print DST "\t\toptions.put(DefaultCodeFormatterConstants2.$constName, $optionsMapInitializer);\n";
		 *
		 */
		 
		return options;
	}

	public void set(Map settings) {
		// TODO create foreach here
	}

	public void setDefaultSettings() {
		/* EVAL-FOR-EACH
	     * 
	     * if($default)
	     * {
	     *     print DST "\t\t$optName = $default;\n";
	     * }
	     *
	     */
	}
}
