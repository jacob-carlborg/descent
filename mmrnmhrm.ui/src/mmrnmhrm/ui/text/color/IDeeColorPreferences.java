package mmrnmhrm.ui.text.color;

import mmrnmhrm.ui.DeePlugin;

/**
 * Color preference constants used in the Dee preference store. 
 */
public interface IDeeColorPreferences extends ILangColorPreferences {

	/** Prefix for D preference keys. */
	String PREFIX = DeePlugin.PLUGIN_ID + ".color."; 

	String DEE_SPECIAL = PREFIX + "special";
	String DEE_STRING = PREFIX + "string";
	String DEE_LITERALS = PREFIX + "literals";
	String DEE_OPERATORS = PREFIX + "operators";
	String DEE_BASICTYPES = PREFIX + "basictypes";
	String DEE_KEYWORD = PREFIX + "keyword";
	String DEE_DOCCOMMENT = PREFIX + "doccomment";
	String DEE_COMMENT = PREFIX + "comment";
	String DEE_DEFAULT = PREFIX + "default";

}
