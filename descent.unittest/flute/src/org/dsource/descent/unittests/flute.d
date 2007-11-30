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
 * UnittestWalker is a better bet.
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
 *        CRLF pair.)
 *    $(LI A "prompt" is the characters "> " (greater-than, space), following the end
 *        of a line, after which no more will be written to standard output. The
 *        program will read a single line from standard input (a line in this
 *        case is defined by the properties of the host system).))
 * 
 * Interface:
 * The interface is well-defined. That is, while it is designed to be human-
 * readable, it is fully specified and can hopefully be processed by automated
 * testing tools. The interface may change between versions.
 * 
 * When the program is executed, one or more lines containing version information
 * will be displayed, followed by a prompt. For this version, the line will be
 * "$(B flute 0.1)". At the prompt, any of the following commands can be run: $(UL
 *     $(LI $(B r $(I test signature)) -
 *        (A lowercase r, followed by a space, followed by the
 *        signature of a test). Will run the specified test and print the
 *        results to stdout. See "test signature specification" for the
 *        specification of what the test signatures will look like. See "test
 *        result specification" for a specification of what the results will
 *        look like. After running the test, a prompt will be displayed for
 *        entry of another command.)
 *     $(LI $(B l) -
 *        (A lowercase l alone on a line). Will list the signatures of all the
 *        tests in the application, followed by a prompt for a new command. The tests
 *        will be listed one signature per line, in alphabetical order. Also,
 *        this list will generally be really long (since there are standard library
 *        and Flectioned tests in there), and none will be fun to type, so there's
 *        another reason to use an invoker.)
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
 * Although linker symbols do this, they are not availible to a code-analysis
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
 *    $(LI Only works with D 1.x)
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
 * there will be two tests: $(UL
 *    $(LI Test 1 is $(B foo.bar.0))
 *    $(LI Test 3 is $(B foo.bar.1)))
 * The middle test is inaccesible via flute. This applies to unittests in anonymous
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
 * http://www.eclipse.org/legal/epl-v10.html
 */
module org.dsource.descent.unittests.flute;

// TODO - i18n - How does this work with Unicode?

import cn.kuehne.flectioned;

static if(cn.kuehne.flectioned.inTango)
{
	version = inTango;
	
	import tango.stdc.stdio: printf, fflush, stdout;
	import tango.stdc.stdlib: exit, EXIT_SUCCESS, EXIT_FAILURE;
	
	// TODO imports for tango stuff
	
	alias Cin.readln readln;
}
else static if(cn.kuehne.flectioned.inPhobos)
{
	version = inPhobos;
	
	import std.stdio: printf, fflush, stdout;
	import std.c.stdlib: exit, EXIT_SUCCESS, EXIT_FAILURE;
	import std.stdio : readln;
	import std.string : atoi, format, trim = strip;
	import std.asserterror : AssertError;
	
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

/**
 * A carriage-return/line-feed pair
 */
const char[] CRLF = "\r\n"; // TODO - use this instead of "\n"

/**
 * A Flectioned marker indicating that it's a unittest
 */
const char[] marker = ".__unittest";

/**
 * An enum containing the result of running a test
 */
enum TestResult
{
	/**
	 * The test passed
	 */
	PASSED,
	
	/**
	 * The test failed with an AssertionError being thrown
	 */
	FAILED,
	
	/**
	 * The test failed with another exception type being thrown
	 */
	ERROR,
	
	/**
	 * There was an internal error in flute running the test
	 */
	INTERNAL_ERROR,
}

/**
 * A map of test signatures to Flectioned function objects
 */
Function[char[]] tests;

/**
 * Main flute entry point. This will be called during static construction, and
 * should, in turn, begin execution of the program.
 */
void fluteMain()
{
	init();
	printVersionInfo();
	while(commandLoop()) { }
	fluteExit();
}

/**
 * Prints information about the current version.
 */
void printVersionInfo()
{
	printf("flute 0.1\n");
}

/**
 * Exits the program gracefully (i.e. runs module destructors, kills the garbage
 * collector, etc.). THis function doesn't return.
 */
void fluteExit()
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
 * Runs the main flute command loop. Prints the prompt, reads user entry of a
 * command, and invokes the specified command.
 *
 * Returns: false if the user entered the exit command, true otherwise.
 */
bool commandLoop()
{
	printf("> ");
	fflush(stdout);
	char[] line = trim(readln());
	if(line.length < 1)
		return true;
	
	switch(line[0])
	{
	
	// r -- Run a test
	case 'r':
	case 'R':
		if(line.length < 3 || line[1] != ' ')
			goto default;
		char[] sig = line[2 .. $];
		runTest(sig);
		return true;
	
	// l -- list all tests
	case 'l':
	case 'L':
		if(line.length > 1)
			goto default;
		foreach(sig; tests.keys.dup.sort)
		{
			printf("%.*s\n", sig);
		}
		return true;
	
	// a -- Run all tests
	case 'a':
	case 'A':
		if(line.length > 1)
			goto default;
		runAllTests();
		return true;
	
	// x -- Exit the program
	case 'x':
	case 'X':
		if(line.length > 1)
			goto default;
		return false;
	
	// It's an unknown command -- say so & try again
	default:
		printf("Unrecognized command %.*s\n", line);
		return true;
	}
	
	assert(false);
}

/**
 * Runs a single test, given by the signature, and outputs the results to stdout as
 * PASSED, FAILED or ERROR, or will output the associated internal error message if
 * it was one of those.
 * 
 * Params:
 *     sig = the signature of the test
 * Returns: The result of running the test
 */
TestResult runTest(char[] sig)
{	
	Function* test = sig in tests;
	if(!test)
	{
		printf("Test %.*s not found\n", sig);
		return TestResult.INTERNAL_ERROR;
	}
	Exception e = run(*test);
	if(!e)
	{
		printf("PASSED\n");
		return TestResult.PASSED;
	}
	
	TestResult result;
	if(isFailure(e))
	{
		printf("FAILED\n");
		result = TestResult.FAILED;
	}
	else
	{
		printf("ERROR\n");
		result = TestResult.ERROR;
	}
	
	printException(e);
	return result;
}

/**
 * Checks whether the given exception is an assertion failure.
 * 
 * Params:
 *     e = The exception to check
 * Returns: true if and only if the given exception is an assertion failure
 */
bool isFailure(Exception e)
{
	version(inTango)
		return null !is (cast(AssertException) e);
	else
		return null !is (cast(AssertError) e);
}

/**
 * Prints the exception information in the specified format and calls
 * printStackTrace for traced exceptions.
 * 
 * Params:
 *     e = The exception to print information for
 */
void printException(Exception e)
{
	TracedException te;
	
	// If it's a failure, print a standardized (Tango vs. Phobos) message
	if(isFailure(e))
	{
		version(inTango)
		{
			// TODO
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
	}
	
	// If it's a traced exception, extract the message and stack trace in the
	// specified format
	//else if(null !is (te = cast(TracedException) e))
	//{
	//  TODO
	//	printStackTrace(te);
	//}
	
	// Otherwise, simply print the exception
	else
	{
		// PERHAPS this could be cleaned up for standardizing stuff across phobos
		// and tango, i.e. filename/line, etc.
		version(inTango)
			printf("%.*s: %.*s\n", e.classinfo.name, e.toUtf8());
		else
			printf("%.*s: %.*s\n", e.classinfo.name, e.toString());
	}
}

/**
 * Prints the stack trace for a traced exception in the specified format.
 * 
 * Params:
 *     e = The exception to print the stack trace for
 */
void printStackTrace(TracedException e)
{
	// TODO
}

/**
 * Runs the specified test, catches any exceptions and returns those exceptions.
 * Returns null if no exceptions were thrown.
 * 
 * Params:
 *     test = The unit test Function object to run
 * Returns: Any exception that was thrown, or null if none were.
 */
Exception run(Function test)
{
	try
	{
		(cast(void function())(test.address))();
	}
	catch(Exception e)
	{
		return e;
	}
	return null;
}

/**
 * Initialzes the tests hash by generating signatures for every unittest function.
 */
void init()
{
	// Extracts the prefix from a Flectioned unittest symbol
	char[] getPrefix(char[] symbol)
	{
		int i = find(symbol, marker);
		return symbol[0 .. i];
	}
	
	uint[char[]] count; // Counts the current symbol number for a given prefix
	
	// TODO this relies on the fact that symbols are returned in the lexical order
	// they were in the source code, make 100% sure this is so.
	foreach(addr; addresses.keys.dup.sort)
	{
		// find a unittest
		Function f = cast(Function) addresses[addr];
		if(f is null)
			continue;
		
		// If it has __unittest in it, it's a unittest (possibly)
		int i = find(f.overload, marker);
		if(i == -1)
			continue;
		
		// Make sure it's the last part of the signature
		i += marker.length;
		while(i < f.overload.length && isdigit(f.overload[i]))
			i++;
		if(i != f.overload.length)
			continue;
		
		// If the test needs arguments, it's not a test
		if(f.getArguments().length != 0)
			continue;
		
		// Generate the signature and add it to the tests array
		char[] prefix = getPrefix(f.overload);
		char[] signature = prefix ~ "." ~ itoa(count[prefix]++);
		tests[signature] = f;
	}
}

/**
 * Runs all the tests in the application
 */
void runAllTests()
{
	uint passed, failed, error;
	foreach(sig; tests.keys.dup.sort)
	{
		printf("%.*s\n", sig);
		TestResult result = runTest(sig);
		switch(result)
		{
			case TestResult.PASSED:
				passed++;
				break;
			case TestResult.FAILED:
				failed++;
				break;
			case TestResult.ERROR:
				error++;
				break;
			case TestResult.INTERNAL_ERROR:
				// Don't log it
				break;
		}
		printf("%.*s", CRLF);
	}
	
	uint total = passed + failed + error;
	printf("   PASSED: %d/%d\n", passed, total);
	printf("   FAILED: %d/%d\n", failed, total);
	printf("   ERROR: %d/%d\n", error, total);
}