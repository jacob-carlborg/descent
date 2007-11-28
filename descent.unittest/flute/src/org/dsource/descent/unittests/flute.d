/++
 + Summary:
 + Flute is an interactive tool for executing D unit tests. It is based on Thomas
 + Kuehne's $(LINK2 http://flectioned.kuehne.cn/#unittest,UnittestWalker). Like
 + UnittestWalker, it requires $(LINK2 http://flectioned.kuehne.cn,Flectioned) to work
 + correctly, and right now is only compatible with D version 1.x. It extends
 + UnittestWalker by providing an interactive (stdin/stdout-based) way to find and
 + execute unit tests.
 + 
 + However, while flute can be used interactively, it is mainly
 + designed to be extended by automated testing tools, such as 
 + $(LINK2 http://www.dsource.org/projects/descent,descent.unittest) or build tools,
 + and hopefully be integrated into a project like CruiseControl, etc. It also allows
 + for execution of tests one at a time or execution of selected tests, which makes
 + test automation more versatile (especially if running all the tests takes a long
 + time.)
 + 
 + Usage:
 + Flute must be statically linked against an application, just like UnittestWalker.
 + TODO - more here.
 + 
 + Definitions: $(UL
 +    $(LI A "line" is any number of ASCII characters (that may contain CR, LF or a
 +        CRLF pair, although they will only do so if the host program uses
 +        them, for example in the text for an assert() statement) followed by a
 +        CRLF pair.)
 +    $(LI A "prompt" is the characters "> " (greater-than, space), following the end
 +        of a line, after which no more will be written to standard output. The
 +        program will read a single line from standard input (a line in this
 +        case is defined by the properties of the host system).))
 + 
 + Interface:
 + The interface is well-defined. That is, while it is designed to be human-
 + readable, it is fully specified and can hopefully be processed by automated
 + testing tools. The interface may change between versions.
 + 
 + When the program is executed, one or more lines containing version information
 + (so far in an unspecified format) will be displayed, followed by a prompt. At
 + this prompt, any of the following commands can be run: $(UL
 +     $(LI $(B r [test signature]) -
 +        (A lowercase r, followed by a space, followed by the
 +        signature of a test). Will run the specified test and print the
 +        results to stdout. See "test signature specification" for the
 +        specification of what the test signatures will look like. See "test
 +        result specification" for a specification of what the results will
 +        look like. After running the test, a prompt will be displayed for
 +        entry of another command.)
 +     $(LI $(B h) -
 +        (A lowercase h alone on a line). Will display an unspecified help message
 +        (which is an unspecified number of lines) followed by a prompt for a new
 +        command.)
 +     $(LI $(B l) -
 +        (A lowercase l alone on a line). Will list the signatures of all the
 +        tests in the application, followed by a prompt for a new command. The tests
 +        will be listed one signature per line.)
 +     $(LI $(B a) -
 +        (An a alone on a line). Will execute all the tests in the
 +        application. For each test, it will write a line containing "Running: "
 +        (without the quotes), then the signature of the test being run on a
 +        line. Then it will write the results of a test. After running all the
 +        tests, a line containing "SUMMARY: " will be written, then all of the following
 +        lines&#58 "PASSED: #/#", "FAILED: #/#", "ERROR: #/#", each preceded by
 +        two spaces, where the first number sign in a line is replaced by the number
 +        of tests that met that condition, and the second in each line is replaced 
 +        by the total number of executed tests. Any tests that caused an internal
 +        error will not be reported in any of the three categories, nor will they be
 +        included in the total.)
 +     $(LI $(B x) -
 +        (An x alone on a line). Will exit the program.)
 + )
 + 
 + Test_Signature_Specification:
 + TODO - this.
 + 
 + Test_Result_Specification:
 + After a test is run, there are four possible results: $(UL 
 +    $(LI The test could succeed, in which case a line containing "$(B PASSED)" will be
 +       printed on a line.)
 +    $(LI The test could fail an assertion. A line containing "$(B FAILED)" will be printed,
 +       followed by the stack trace of the AssertionFailedException.)
 +    $(LI The test could throw an exception. A line containing "$(B ERROR)" will be printed,
 +       followed by the stack trace of the exception. The main rationale for
 +       treating test failures differently than exceptions is to allow automated
 +       tools to track failures vs. error conditions. However, the tool can't
 +       differentiate between assertions failed in the tests and assertions failed
 +       in the main program body, so a "$(B FAILED)" message can mean either.)
 +    $(LI An internal error could occur with the test runner (for example, the test is
 +       not found). In this case, a human-readable message that does not begin with
 +       "$(B PASSED)", "$(B FAILED)" or "$(B ERROR)" will be printed on a single line.
 +       If the test is not found, the message will be "Test [test signature] not found",
 +       where "[test signature]" will be replaced with the signature of the test.
 +       Other error messages may appear and are unspecified.)
 + )
 + 
 + Stack_Trace_Specification:
 + The test runner has support for Flectioned's TracedException, but will work
 + correctly even if the thrown exception is not a TracedException. The stack trace
 + will begin with a line containing the name of the thrown exception. If the
 + exception has a message, this will be followed by ": " and then the exception
 + message.
 + 
 + If the exception is a subclass of TracedException, this will be followed by the
 + actual stack trace of the exception. Each line of the stack trace will begin with
 + three spaces, which will allow stack traces to be easily visually parsed
 + from the surrounding information and dealt with in automated tools.
 + 
 + Each line of the stack trace represents a stack frame that was executing when the
 + exception was thrown. The stack frames will be reported in reverse order (the
 + "unwinding" of the stack). TODO - finish this.
 + 
 + Authors: Robert Fraser (fraserofthenight@gmail.com)
 + Copyright: 2007 Robert Fraser (fraserofthenight@gmail.com)
 + Version: 0.1
 + 
 + License:
 + Copyright (c) $(COPYRIGHT)
 + 
 + All rights reserved.
 + 
 + Redistribution and use in source and binary forms, with or without
 + modification, is permitted provided that the following conditions are met:
 + 
 + 1. Redistributions of source code must retain the above copyright notice,
 +    this list of conditions and the following disclaimer.
 + 
 + 2. Redistributions in binary form must reproduce the above copyright
 +    notice, this list of conditions and the following disclaimer in the
 +    documentation and/or other materials provided with the distribution.
 + 
 + THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 + AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 + IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 + ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 + LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 + CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 + SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 + INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 + CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 + ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 + POSSIBILITY OF SUCH DAMAGE.
 +/
module org.dsource.descent.unittests.flute;

import cn.kuehne.flectioned;

static if(cn.kuehne.flectioned.inTango)
{
	import tango.stdc.stdio: printf, fflush, stdout;
	import tango.stdc.stdlib: exit, EXIT_SUCCESS, EXIT_FAILURE;
}
else static if(cn.kuehne.flectioned.inPhobos)
{
	import std.stdio: printf, fflush, stdout;
	import std.c.stdlib: exit, EXIT_SUCCESS, EXIT_FAILURE;
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

/++
 + Main flute entry point. This will be called during static construction, and
 + should, in turn, begin execution of the program.
 +/
void fluteMain()
{
	printf("Flute version 0.1\n");
	fflush(stdout);
	goto Lexit;

	// Copied from UnittestWalker; needed to exit the program
	Lexit:
		_moduleDtor();
		gc_term();
	
		version(linux)
		{
			_STD_critical_term();
			_STD_monitor_staticdtor();
		}
		exit(EXIT_FAILURE);
}
