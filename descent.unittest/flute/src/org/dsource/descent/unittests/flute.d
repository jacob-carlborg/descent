/**
 * Summary:
 * Flute is an interactive tool for executing D unit tests. It is based on Thomas
 * Kuehne's $(LINK2 http://flectioned.kuehne.cn/#unittest,UnittestWalker). Like
 * UnittestWalker, it requires $(LINK2 http://flectioned.kuehne.cn,Flectioned) to work
 * correctly, and right now is only compatible with D version 1.x. It extends
 * UnittestWalker by providing an interactive (stdin/stdout-based) way to find and
 * execute unit tests.
 * 
 * However, while flute can be used interactively, it is mainly
 * designed to be extended by automated testing tools, such as 
 * $(LINK2 http://www.dsource.org/projects/descent,descent.unittest) or build tools,
 * and hopefully be integrated into a project like CruiseControl, etc. It also allows
 * for execution of tests one at a time or execution of selected tests, which makes
 * test automation more versatile (especially if running all the tests takes a long
 * time.) To be truly used effectively, flute needs to be paired with a code analysis
 * or other tool to identify/name unittests which is then executed by the fluted
 * application. If all you want is to run all the unittests in the project,
 * UnittestWalker is a better bet. If using the command line version directly, I
 * would suggest redirecting stdin from a file to create testing suites.
 * 
 * Usage:
 * Flute must be statically linked against an application, just like UnittestWalker.
 * To do so, place it at the end of the build command, i.e. "$(B dmd -unittest 
 * $(I &lt;your source code&gt;) flectioned.d flute.d)" or "$(B gdmd -fall-sources
 *  -unittest $(I &lt;your source code&gt;) flectioned.d flute.d)". To run, simply start
 *  the generated executable. Note that your actual application cannot be started
 *  if you use flute.
 * 
 * Definitions: $(UL
 *    $(LI A "line" is any number of ASCII characters (that may contain CR, LF or a
 *        CRLF pair, although they will only do so if the host program uses
 *        them, for example in the text for an assert() statement) followed by a
 *        system-specific line terminator.))
 * 
 * Interface:
 * The interface is well-defined. That is, while it is designed to be human-
 * readable, it is fully specified and can hopefully be processed by automated
 * testing tools. The interface may change between versions.
 * 
 * When the program is executed, one or more lines containing version information
 * will be displayed. For this version, the version line will be "$(B flute 0.1)".
 * Warnings may be displayed after this for tests with multiple names unless
 * version(Flute_NoWarnings) has been specified. The program will then enter a
 * loop where it will await input from stdin, process the given command, and 
 * await futher input. The commands are: $(UL
 *     $(LI $(B r $(I test signature)) -
 *        (An r, followed by a space, followed by the signature or name
 *        of a test). Will run the specified test and print the
 *        results to stdout. See "test signature specification" for the
 *        specification of what the test signatures will look like. See "test
 *        result specification" for a specification of what the results will
 *        look like. See "Test Naming" for the specification of how test names
 *        are handled.)
 *     $(LI $(B l) -
 *        (An l alone on a line). Prints a list of all the tests in the project.
 *        One test specification will appear per line. Named tests will appear
 *        as their fully qualified test name. Unnamed tests will appear with
 *        their signature. The order in which tests are printed is the 
 *        alphabetical order of their signatures, and lexical order for unittests
 *        within the same scope (that is, names hav nothing to do with the order).)
 *     $(LI $(B a) -
 *        (An a alone on a line). Will execute all the tests in the
 *        application. For each test, it will write a line containing "Running: "
 *        (without the quotes), then the signature of the test being run on a
 *        line. Then it will write the results of a test. After running all the
 *        tests, a line containing "SUMMARY: " will be written, then all of the following
 *        lines&#58 "PASSED: #/#", "FAILED: #/#", "ERROR: #/#", each preceded by
 *        three spaces, where the first number sign in a line is replaced by the number
 *        of tests that met that condition, and the second in each line is replaced 
 *        by the total number of executed tests. Any tests that caused an internal
 *        error will not be reported in any of the three categories, nor will they be
 *        included in the total. There will be a blank line between each test.)
 *     $(LI $(B x) -
 *        (An x alone on a line). Will exit the program.))
 * 
 * Test_Signature_Specification:
 * A test signature is a way to uniquely identify a unittest in an application.
 * Although linker symbols do this, they are not available to a code-analysis
 * front-end. Thus, a signature form is needed that can be generated by code
 * analysis, easily translated to and from linker symbols, and is (generally) human-
 * readable.
 * 
 * A test signature consists of the fully-qualified name of the test's location,
 * followed by a period, followed by the number of the test in that location in the
 * lexical order the test appears (0-based). For example if you have:
 * ---
 * module foo.bar;
 * 
 * unittest { /+ Test 1 +/ }
 * 
 * class Baz {
 *     unittest { /+ Test 2 +/ }
 * }
 * 
 * unittest { /+ Test 3 +/ }
 * ---
 * there will be three tests: $(UL
 *    $(LI Test 1 is $(B foo.bar.0))
 *    $(LI Test 2 is $(B foo.bar.Baz.0))
 *    $(LI Test 3 is $(B foo.bar.1)))
 * 
 * Test_Names:
 * Instead of using signatures, names can be used to refer to tests as well. Since
 * signatures are often long and difficult to type, this is often the preferred
 * method. To add a name to a test, import org.dsource.descent.unittests.naming
 * and insert use "mixin(test_name($(I test name)));" somewhere in your test body.
 * For example:
 * ---
 * module bacon.eggs;
 * import org.dsource.descent.unittests.naming;
 * 
 * class Sausage {
 *     unittest {
 *         mixin(test_name("spam"));
 *         // ...
 *     }
 * }
 * ---
 * 
 * To refer to a named test, you may either use the signature generated for it or use
 * the test's name. The test's name can either be fully qualified or, if unambiguous,
 * can appear with a colon preceding it. If there's more than one test in the
 * application with the unqualified name, an error will result.
 * In the example above, the test can be referred to as any of: $(UL
 *    $(LI bacon.eggs.Sausage.0)
 *    $(LI bacon.eggs.Sausage.spam)
 *    $(LI :spam))
 * 
 * Wildcards:
 * When specifying a test to run, you may use a "*" wildcard to indicate all the
 * tests in a prticular package (including its subpackages), module, or aggregate.
 * For example, in the foo.bar example above, "$(B foo.*)" and "$(B foo.bar.*)"
 * would refer to all three tests, and "$(B foo.bar.Baz.*)" would refer to only the
 * test within the class. When running a set of tests specified by a wildcard, the
 * result will be the same as that defined under "run all tests".
 * 
 * Test_Result_Specification:
 * After a test is run, there are four possible results: $(UL 
 *    $(LI The test could succeed, in which case a line containing "$(B PASSED)" will be
 *       printed on a line.)
 *    $(LI The test could fail an assertion. A line containing "$(B FAILED)" will be printed,
 *       followed by the stack trace of the exception.)
 *    $(LI The test could throw an exception. A line containing "$(B ERROR)" will be printed,
 *       followed by the stack trace of the exception. The main rationale for
 *       treating test failures differently than exceptions is to allow automated
 *       tools to track failures vs. error conditions. However, the tool can't
 *       differentiate between assertions failed in the tests and assertions failed
 *       in the main program body, so a "$(B FAILED)" message can mean either.)
 *    $(LI An internal error could occur with the test runner (for example, the test is
 *       not found). In this case, a human-readable message that does not begin with
 *       "$(B PASSED)", "$(B FAILED)" or "$(B ERROR)" will be printed on a single line.
 *       If the test is not found, the message will be "$(B Test $(I test signature) not
 *       found)", where "test signature" will be replaced with the signature of the test.
 *       Other error messages may appear and are unspecified.))
 * 
 * Stack_Trace_Specification:
 * The test runner has support for Flectioned's TracedException, but will work
 * correctly even if the thrown exception is not a TracedException. The stack trace
 * will begin with a line containing the name of the thrown exception. If the
 * exception has a message, this will be followed by ": " and then the exception
 * message.
 * 
 * If the exception is an assertion failure (AssertError in Phobos, AssertException
 * in Tango), the line will instead be "$(B Assertion failed in $(I &lt;filename&gt;)
 * at line $(I &lt;line&gt;))" followed by ": " and a message if there is one. The
 * rationale behind rewriting this exception is to smooth over differences between
 * Tango and Phobos, which report their assert errors differently.
 * 
 * If the exception is a subclass of TracedException, this will be followed by the
 * actual stack trace of the exception. Each line of the stack trace will begin with
 * three spaces, which will allow stack traces to be easily visually parsed
 * from the surrounding information and dealt with in automated tools. The stack
 * trace differs from the standard Flectioned stack trace for a TracedException to
 * ease processing.
 * 
 * Each line of the stack trace represents a stack frame that was executing when the
 * exception was thrown. The stack frames will be reported in reverse order (the
 * "unwinding" of the stack). TODO - finish this.
 * 
 * Limitations:
 * $(UL
 *    $(LI No Unicode/internationalization support (planned))
 *    $(LI Untested in low memory situations)
 *    $(LI Only tested with D 1.x (future versions will support D2))
 *    $(LI No test suites/categorization (will possibly be in a future version, but
 *         the Descent front-end should support this when it gets released.))
 *    $(LI Requires Flectioned (not likely to change)))
 * 
 * Bugs:
 * If a class is inside a function, unittests in that class won't work. Keep this in
 * mind when generating signatures in code analysis tools. For example, if you have:
 * ---
 * module foo.bar;
 * 
 * unittest { /+ Test 1 +/ }
 * 
 * void baz() {
 *     class Quux {
 *         unittest { /+ Test 2 +/ }
 *     }
 * }
 * 
 * unittest { /+ Test 3 +/ }
 * ---
 * The middle test is inaccessible via flute. This applies to unittests in anonymous
 * classes, too. I hope to fix this in a future version.
 * 
 * Authors:
 * Robert Fraser (fraserofthenight@gmail.com)
 * 
 * Copyright:
 * 2007 Robert Fraser (fraserofthenight@gmail.com)
 * 
 * Version:
 * Almost 0.1
 * 
 * License:
 * Copyright (c) $(COPYRIGHT)
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at 
 * $(LINK http://www.eclipse.org/legal/epl-v10.html)
 */
module org.dsource.descent.unittests.flute;

/*
 * Implementation Notes:
 * - Flute is designed to be used independently, so should be decoupled from Descent
 *   or any other testing front-end.
 * - Although it should be independent from Descent, since Descent is the primary
 *   front-end, implementation changes should be thoroughly tested with Descent,
 *   and the interface should be crafted in a Descent-friendly way.
 * - The interface should always be well-specified to aid in automated processing,
 *   but should remain human-readable.
 * - Flute should remain in one module, since users shouldn't be expected to add
 *   additional sources to their command line than absolutely necessary. In
 *   particular, the Flute runner and test namer should be completly independent,
 *   so that neither requires te other (since if the user code imports this module,
 *   the application won't run, while if this module imports the naming, the user
 *   will have to specify it on a DMD command line.
 * - Flute should work properly with Tango and Phobos.
 * - Ideally, Flute should work properly with different versions of Flectioned.
 * - Flute should make no assumptions about the user code. In particular, every
 *   effort should be made so that Flute works correctly with:
 *     - Tests that segfault
 *     - Tests that are unnamed
 *     - Low-memory situations (PERHAPS)
 *     - Tests that go into infinite loops/hang
 *     - Unicode/non-ASCII characters in both the test signatures and test names
 *       (and input, obviously). (NEXTVERSION)
 *     - Exception messages which contain more than one line
 *  - Documentation and specification is always more important than implementation.
 *  - Remove any internal unittests before release, so they don't clutter user code
 */

import cn.kuehne.flectioned;

static if(cn.kuehne.flectioned.inTango)
{
	version = inTango;
	
	import tango.stdc.stdio: printf, fflush, stdout;
	import tango.stdc.stdlib: exit, EXIT_SUCCESS, EXIT_FAILURE;
	import tango.io.Console : Cin;
	import tango.core.Exception : AssertException;
	import tango.text.Util : trim;
	import tango.core.Array : tangoFind = find;
	import tango.stdc.ctype : isdigit;
	
	static import tango.text.convert.Integer;
	static if(is(typeof(tango.text.convert.Integer.toString)))
		alias tango.text.convert.Integer.toString itoa;
	else
		alias tango.text.convert.Integer.toUtf8 itoa;
	
	char[] readln() { return Cin.copyln(); }
	int find(char[] haystack, char[] needle) { 
		uint res = tangoFind(haystack, needle); 
		return res == haystack.length ? -1 : res;
	}
}
else static if(cn.kuehne.flectioned.inPhobos)
{
	version = inPhobos;
	
	import std.stdio: printf, fflush, stdout;
	import std.c.stdlib: exit, EXIT_SUCCESS, EXIT_FAILURE;
	import std.stdio : readln;
	import std.string : atoi, format, find, trim = strip;
	import std.ctype : isdigit;
	import std.asserterror : AssertError;
	
	// This should be a standard lib function, so it goes up here
	char[] itoa(int i) { return format("%d", i); }
}
else
{
	static assert(false, "Not in Phobos or Tango");
}

// Needed for clean program exit
extern(C)
{
	void _STD_monitor_staticdtor();
	void _STD_critical_term();
	void gc_term();
	void _moduleDtor();
}

// Find out whether optlink was used as the linker
version(Windows)
	version = optlink;
else version(Win32)
	version = optlink;

// Used to start the program (copied from UnittestWalker)
version(optlink)
	static this() { fluteMain(); }
else
	extern(C) void _moduleUnitTests() { fluteMain(); }

/// A Flectioned marker indicating a unittest
private const char[] UNITTEST_MARKER = ".__unittest";

/// A marker indicating a named test
private const char[] NAMED_TEST_MARKER = ".__setTestName!(__testName_";

/// A string containing version information, printed at the start of the application
private const char[] VERSION_STRING = "flute 0.1";

/*
 * The result of running a test
 */
private enum ResultType
{
	PASSED, /// The test passed succesfully
	FAILED, /// The test failed an assertion
	ERROR,  /// The test threw another type of exception
}

/**
 * Represents the result of running a test and holds the exception that the test
 * threw (if their was one).
 */
private class TestResult
{
	private ResultType type; /// Did the test pass, fail, or error out?
	private Object e;        /// The exception that was thrown, or null if and only if the test passed.
	
	/**
	 * Creates a new test result using an exception
	 * 
	 * Params:
	 *     e = The exception thrown, if one was, or null if the test passed.
	 */
	private this(Object e)
	{
		bool isFailure(Object e)
		{
			version(inTango)
				return null !is (cast(AssertException) e);
			else
				return null !is (cast(AssertError) e);
		}
		
		this.e = e;
		
		if(e)
			type = isFailure(e) ? ResultType.FAILED : ResultType.ERROR;
		else
			type = ResultType.PASSED;
	}
	
	/**
	 * Prints test information in the specified format, including exception
	 * information, if available.
	 */
	private void print()
	{
		// Print the "PASSED", "FAILED", or "ERROR" and the exception. Exceptions
		// are printed in here since assertion failures are formatted differently
		// from general exceptions.
		switch(type)
		{
			case ResultType.PASSED:
				printf("PASSED\n");
				return;
			
			case ResultType.FAILED:
				printf("FAILED\n");
				version(inTango)
				{
					AssertException ae = cast(AssertException) e;
					assert(ae !is null);
					printf("Assertion failed in %.*s at line %d", ae.file, ae.line);
					if(ae.msg && ae.msg.length > 0)
						printf(": %.*s", ae.msg);
					printf("\n");
				}
				else
				{
					// Extract the assertion message from the complete error message
					char[] extractMessage(char[] orig)
					{
						int i = find(orig, ") ");
						if(i > 0 && orig.length > i + 2)
							return orig[i + 2 .. $];
						else
							return null;
					}
					
					AssertError ae = cast(AssertError) e;
					assert(ae !is null);
					printf("Assertion failed in %.*s at line %d", ae.filename, ae.linnum);
					char[] msg = extractMessage(ae.msg);
					if(msg)
						printf(": %.*s", msg);
					printf("\n");
				}
				goto LprintStackTrace;
			
			// PERHAPS this could be cleaned up for standardizing stuff across phobos
			// and tango, i.e. filename/line, etc.
			case ResultType.ERROR:
				printf("ERROR\n");
				static if(is(typeof((new Object).toString)))
					printf("%.*s: %.*s\n", e.classinfo.name, e.toString());
				else
					printf("%.*s: %.*s\n", e.classinfo.name, e.toUtf8());
				goto LprintStackTrace;
		}
		
		LprintStackTrace:
			; // TODO
	}
}

/**
 * Represents a single test entity. Exactly one TestSpecification should be created
 * in the init() function for every test in the application, which can be accessed
 * in different ways (i.e. via simple name, fully qualified name, or signature).
 */
private class TestSpecification
{
	private Function func; /// The Flectioned Function object associated with this test
	private char[] prefix; /// The test's fully-qualified location
	private char[] name;   /// The test's simple name, or null if none
	private uint ordinal;  /// The test's position within its scope.
	
	/**
	 * Creates a new test specification from the provided information. SHould only
	 * be called from the init() function.
	 * 
	 * Params:
	 *     func =    The Flectioned Function object representing the test function
	 *     prefix =  The fully-qualified prefix of the test name
	 *     ordinal = The lexical position of the test within its scope, as defined
	 *               in the test signature specification.
	 */
	private this(Function func, char[] prefix, uint ordinal)
	{
		this.func = func;
		this.prefix = prefix;
		this.ordinal = ordinal;
		this.name = null;
	}
	
	/**
	 * Sets the name of the test. Should only be called once per test, and BEFORE
	 * the test is added to a registry.
	 * 
	 * Params:
	 *     name = The name of the test
	 */
	private void setName(char[] name)
	{
		assert(this.name is null);
		this.name = name;
	}
	
	/**
	 * Runs the test and returns the result of running it. Be warned that the test
	 * may hang, consume tons of memory, segfault, put the program in an invalid
	 * state or otherwise be a little naughty.
	 * 
	 * Returns: the result of running the test
	 */
	private TestResult run()
	{
		try
		{
			(cast(void function())(func.address))();
			return new TestResult(null);
		}
		catch(Object e)
		{
			return new TestResult(e);
		}
	}
	
	/**
	 * Checks whether this test has a name givn to it by the user
	 * 
	 * Returns: true if and only if this test is named
	 */
	private bool isNamed()
	{
		return name !is null;
	}
	
	/**
	 * Gets the signature of the test in the specified format
	 * 
	 * Returns: the signature of the test
	 */
	private char[] getSignature()
	{
		return prefix ~ "." ~ itoa(ordinal);
	}
	
	/**
	 * Gets the fully qualified name of the test in the specified format
	 * 
	 * Returns: The fully qualified name of the test, or null if the test is not
	 *          named.
	 */
	private char[] getFullyQualifiedName()
	{
		if(!name)
			return null;
		
		return prefix ~ "." ~ name;
	}
	
	/**
	 * Checks whether this test is named.
	 * 
	 * Returns: true if and only if this test is named
	 */
	private char[] getSimpleName()
	{
		return name;
	}
}

/**
 * Contains a set of tests which can be accessed by their signatures, fully
 * qualified names, or simple names.
 */
private class TestRegistry
{
	/**
	 * Wrapper struct for returning if a simple name is ambigous. Here because
	 * char[][][char[]] just doesn't look right.
	 */
	private struct Ambiguity
	{
		char[][] fqns = [];
	}
	
	/**
	 * The result of a search in the test registry
	 */
	private struct SearchResult
	{
		/**
		 * The result of the search
		 */
		enum TestFound
		{
			NOT_FOUND,      /// The test is not in the registry
			AMBIGUOUS,      /// The given simple name is ambigous
			FOUND,          /// The test exists in the registry
			MULTIPLE_TESTS, /// It was a wildcard that found more than one test
		}
		
		TestFound found; /// Was the test found?
		
		union
		{
			Ambiguity ambig;        /// If the result is ambigous, the ambiguity struct
			TestSpecification test; /// The test if one was found
			char[][] testNames;     /// If multiple tests were found, the test names
		}
	}
	
	private TestSpecification[char[]] tests_sig; /// All tests, by signature
	private TestSpecification[char[]] tests_fqn; /// Named tests, by fully-qualified name
	private TestSpecification[char[]] tests_sn;  /// Named tests, by simple name
	private Ambiguity[char[]] ambiguities;       /// Contains info on ambigous tests
	
	/**
	 * Adds the test to the registry
	 * 
	 * Params:
	 *     test = The test to add to the registry
	 * Returns: true if and only if the test was successfully added
	 */
	private bool add(TestSpecification test)
	{	
		if(test.getSignature in tests_sig)
			return false;
		
		tests_sig[test.getSignature()] = test;
		if(test.isNamed())
		{	
			tests_fqn[test.getFullyQualifiedName()] = test;
			
			char[] simpleName = test.getSimpleName();
			Ambiguity* ambig = simpleName in ambiguities;
			if(ambig)
			{
				ambig.fqns ~= test.getFullyQualifiedName();
			}
			else if(simpleName in tests_sn)
			{
				Ambiguity newAmbig;
				newAmbig.fqns ~= tests_sn[simpleName].getFullyQualifiedName();
				newAmbig.fqns ~= test.getFullyQualifiedName();
				ambiguities[simpleName] = newAmbig;
				tests_sn.remove(simpleName);
			}
			else
			{
				tests_sn[simpleName] = test;
			}
		}
		return true;
	}
	
	/**
	 * Finds the test given by spec and returns it or null if the test is not in
	 * the registry. If the spec contains a wildcard, null will be returned; for
	 * correct handling of wildcards, use the search function directly.
	 *  
	 * Params:
	 *     spec = The test specification; can be any of: a test signature, a test
	 *            fully-qualified name, or a colon-preceded test simple name. Will
	 *            validate this parameter, so if given bad input, the function will
	 *            simply return null.
	 * Returns: the test given by the specified FQN, signature or simple name or null
	 *          if the test isn't in the registry or the simple name is ambigous or
	 *          if multiple tests were found via a wildcard
	 */
	private TestSpecification get(char[] spec)
	{
		SearchResult result = search(spec);
		return result.found == SearchResult.TestFound.FOUND ? result.test : null;
	}
	
	/**
	 * Finds the test in the registry
	 * 
	 * Params:
	 *     spec = The test specification; can be any of: a test signature, a test
	 *            fully-qualified name, or a colon-preceded test simple name. Will
	 *            validate this parameter, so if given bad input, the function will
	 *            simply return null. Wildcards will be correctly handled, too.
	 * Returns: The result of finding the test
	 */
	private SearchResult search(char[] spec)
	{	
		if(!spec || spec.length < 2)
			goto Lnotfound;
		
		TestSpecification* testPtr;
		if(spec[0] == ':')
		{
			char[] simpleName = spec[1 .. $];
			Ambiguity* ambig = simpleName in ambiguities;
			if(ambig)
			{
				SearchResult result = SearchResult(SearchResult.TestFound.AMBIGUOUS);
				result.ambig = *ambig;
				return result;
			}
			else
			{
				testPtr = simpleName in tests_sn;
			}
		}
		else
		{
			char[] prefix;
			char[] postfix;
			
			// Split spec at the last '.'
			uint i = spec.length - 2;
			for(; i > 0; i--)
			{
				if(spec[i] == '.')
					break;
			}
			if(i == 0)
				goto Lnotfound;
			prefix = spec[0.. i];
			postfix = spec[i + 1 .. $];
			
			if(!prefix || prefix.length < 1 || !postfix || postfix.length < 1)
				goto Lnotfound;
			
			if(postfix[0] == '*')
			{
				if(postfix.length > 1)
					goto Lnotfound;
				
				SearchResult result = SearchResult(SearchResult.TestFound.MULTIPLE_TESTS);
				result.testNames = [];
				foreach(testName; getTestNames())
				{
					if(find(testName, prefix) == 0)
						result.testNames ~= testName;
				}
				
				if(result.testNames.length == 0)
					goto Lnotfound;
				
				return result;
			}
			
			else if(isdigit(postfix[0]))
			{
				testPtr = spec in tests_sig;
			}
			
			else
			{
				testPtr = spec in tests_fqn;
			}
		}
		
		if(!testPtr)
			goto Lnotfound;
		
		SearchResult result = SearchResult(SearchResult.TestFound.FOUND);
		result.test = *testPtr;
		return result;
		
		Lnotfound:
			return SearchResult(SearchResult.TestFound.NOT_FOUND);
	}
	
	/**
	 * Runs a single test, given by the signature, and outputs the results to stdout as
	 * PASSED, FAILED or ERROR, with the exceptions/stack tracing as specified.
	 * 
	 * Params:
	 *     spec = A test specified to run, may be a signature, fully-qualified name
	 *            or simple name preceded by a colon. Will validate input & work
	 *            correctly for invalid input. Wildcards will be correctly handled, too.
	 * Returns: The result of running the test or null if the test failed or if
	 *          multiple tests were specified in spec by a wildcard
	 */
	private TestResult runTest(char[] spec)
	{
		SearchResult found = search(spec);
		switch(found.found)
		{
			case SearchResult.TestFound.NOT_FOUND:
				printf("Test %.*s not found\n", spec);
				return null;
			case SearchResult.TestFound.AMBIGUOUS:
				printf("Simple name %.*s is ambigous, could refer to either %.*s or "
				       "%.*s\n", spec[1 .. $], found.ambig.fqns[0],
				       found.ambig.fqns[1]);
				return null;
			case SearchResult.TestFound.MULTIPLE_TESTS:
				runTests(found.testNames);
				return null;
			case SearchResult.TestFound.FOUND:
				TestResult result = found.test.run();
				assert(result);
				result.print();
				return result;
		}
	}
	
	/**
	 * Gets a list of all the tests in the format specified by the "l" command.
	 * 
	 * Returns: a list of all the tests in the registry with their fully qualified
	 *          nams if they have them, and signatures if not, in the order of their
	 *          signatures.
	 */
	private char[][] getTestNames()
	{
		char[][] tests = [];
		foreach(sig; tests_sig.keys.dup.sort)
		{
			TestSpecification test = tests_sig[sig];
			if(test.isNamed())
				tests ~= test.getFullyQualifiedName();
			else
				tests ~= test.getSignature();
		}
		return tests;
	}
	
	/**
	 * Runs all the tests in the registry
	 */
	private void runTests(char[][] testNames)
	{
		uint passed, failed, error;
		foreach(spec; testNames.sort)
		{
			printf("%.*s\n", spec);
			TestResult result = runTest(spec);
			if(result)
			{
				switch(result.type)
				{
					case ResultType.PASSED:
						passed++;
						break;
					case ResultType.FAILED:
						failed++;
						break;
					case ResultType.ERROR:
						error++;
						break;
				}
			}
			printf("\n");
		}
		
		uint total = passed + failed + error;
		printf("   PASSED: %d/%d\n", passed, total);
		printf("   FAILED: %d/%d\n", failed, total);
		printf("   ERROR: %d/%d\n", error, total);
	}
}

/// The registry of all the tests in the pplication
private TestRegistry registry;

/**
 * Main flute entry point. This will be called during static construction, and
 * should, in turn, begin execution of the program.
 */
private void fluteMain()
{
	printf("%.*s\n", VERSION_STRING);
	fflush(stdout);
	init();
	if(!commandLoop())
		fluteExit();
}

/**
 * Exits the program gracefully (i.e. runs module destructors, kills the garbage
 * collector, etc.). This function doesn't return.
 */
private void fluteExit()
{
	fflush(stdout);
	_moduleDtor();
	gc_term();

	version(linux)
	{
		_STD_critical_term();
		_STD_monitor_staticdtor();
	}
	exit(EXIT_SUCCESS);
}

/**
 * Runs the main flute command loop. Repeatdly reads the user entry of a command
 * and invokes the specified command.
 *
 * Returns: true if the user requested the application be run, false if the user
 *          requested the application exit.
 */
private bool commandLoop()
{
	LnextCommand:
	printf("(flute)\n");
	fflush(stdout);
	char[] line = trim(readln());
	if(line.length < 1)
		goto LnextCommand;
	
	switch(line[0])
	{
	
	// r -- Run a test
	case 'r':
	case 'R':
		if(line.length < 3 || line[1] != ' ')
			goto default;
		char[] sig = line[2 .. $];
		registry.runTest(sig);
		goto LnextCommand;
	
	// l -- list all tests
	case 'l':
	case 'L':
		if(line.length > 1)
			goto default;
		foreach(spec; registry.getTestNames())
			printf("%.*s\n", spec);
		goto LnextCommand;
	
	// a -- Run all tests
	case 'a':
	case 'A':
		if(line.length > 1)
			goto default;
		registry.runTests(registry.getTestNames());
		goto LnextCommand;
	
	// x -- Exit the program
	case 'x':
	case 'X':
		if(line.length > 1)
			goto default;
		return false;
	
	// It's an unknown command -- say so & try again
	default:
		printf("Unrecognized command %.*s\n", line);
		goto LnextCommand;
	}
}

/**
 * Initializes the tests hash by generating signatures for every unittest function.
 */
private void init()
{	
	// Extracts the prefix from a Flectioned unittest symbol
	char[] getPrefix(char[] symbol, char[] marker)
	{
		int i = find(symbol, marker);
		return symbol[0 .. i];
	}
	
	uint[char[]] count; // Counts the current symbol number for a given prefix
	TestSpecification[char[]] foundTests; // Associates flectioned names with tests
	
	registry = new TestRegistry();
	
	// Get all the tests
	// TODO this relies on the fact that symbols are returned in the lexical order
	// they were in the source code, make 100% sure this is so.
	foreach(addr; addresses.keys)
	{
		// find a unittest
		Function f = cast(Function) addresses[addr];
		if(f is null)
			continue;
		
		// If it has __unittest in it, it's a unittest (possibly)
		int i = find(f.overload, UNITTEST_MARKER);
		if(i == -1)
			continue;
		
		// Make sure it's the last part of the signature
		i += UNITTEST_MARKER.length;
		while(i < f.overload.length && isdigit(f.overload[i]))
			i++;
		if(i != f.overload.length)
			continue;
		
		// If the test needs arguments, it's not a test
		if(f.getArguments().length != 0)
			continue;
		
		// If it's a flectioned test, don't add it to the registry
		char[] prefix = getPrefix(f.overload, UNITTEST_MARKER);
		if(find(prefix, "cn.kuehne.flectioned") >= 0)
			continue;
		
		// Generate the test & add it to the registry
		TestSpecification test = new TestSpecification(f, prefix, count[prefix]++);
		foundTests[f.overload] = test;
	}
	
	// Associate names with tests
	foreach(addr; addresses.keys)
	{
		Class c= cast(Class) addresses[addr];
		
		if(c is null)
			continue;
		
		int i = find(c.name, NAMED_TEST_MARKER);
		if(i == -1)
			continue;
		
		char[] funcName = c.name[0 .. i];
		TestSpecification* test = funcName in foundTests;
		if(!test)
			continue;
		
		if(test.isNamed())
		{
			version(Flute_NoWarnings) {}
			else
				printf("Warning: Test %.*s given multiple names, only %.*s used\n", 
					test.getSignature(), test.getSimpleName());
			continue;
		}
		
		i += NAMED_TEST_MARKER.length;
		int j = i;
		for(; j < c.name.length && c.name[j] != ')'; j++) { }
		if(j == c.name.length)
			continue;
		
		test.setName(c.name[i .. j]);
	}
	
	foreach(test; foundTests.values)
		registry.add(test);
}
