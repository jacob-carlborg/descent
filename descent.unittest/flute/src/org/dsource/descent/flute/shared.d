/**
 * Provides functions shared between various modules in this package in a
 * standard-library-independent manner. In general, this module exists
 * soley to reduce redefinitions of functions that are needed by other
 * modules in this package. While this may be a questionable software
 * design technique (after all, many unnneeded functions will be imported into
 * all the modules in this package, it just makes everything easier to
 * manage, since almost all standard library accesses are done through here.
 */
module org.dsource.descent.flute.shared;

version(Tango)
{
	version = inTango;
}
else
{
	version = inPhobos;
	
	public import std.c.string : strlen;
	public import std.string : find, format;
	public import std.ctype : isdigit;
}

char[] toHex(void* val)
{
	const int percision = (void*).sizeof * 2;
	
	version(inPhobos)
		return format("%#0.*x", percision, val);
	else
		{ } // TANGO
}