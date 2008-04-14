/**
 * Small reflection engine designed to replace flectioned. This decision was
 * made mostly because flectioned is no longer being maintained, as well as
 * to remove a dependancy. It is not designed as a drop-in replacement for 
 * flectioned, but instead as a way to provide stack tracing and symbol 
 * addressing for the Flute runtime component. Thus, it is NOT an
 * ideal reflection system for general-purpose use.
 * 
 * Much of the code in here is adapted from Flectioned, so big ups to Thomas
 * Kuehne for making this possible.
 * 
 * Authors:
 * Robert Fraser (fraserofthenight@gmail.com)
 * Thomas Kuehne (thomas@kuehne.cn)
 * 
 * Copyright:
 * 2007-2008 Robert Fraser (fraserofthenight@gmail.com)
 * 2006-2007 Thomas Kuehne (thomas@kuehne.cn)
 * 
 * Version:
 * 0.6
 * 
 * License:
 * Code written by Thomas Kuehne for Flectioned (marked in thr file) is 
 * made availible under this liscense:
 * 
 * Copyright (c) 2006-2007 Thomas Kuehne (thomas@kuehne.cn)
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * All other code is made availible under this liscense:
 * 
 * Copyight (c) 2007-2008 Robert Fraser (fraserofthenight@gmail.com)
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at 
 * $(LINK http://www.eclipse.org/legal/epl-v10.html)
 */
module org.dsource.descent.flute.reflect;

import org.dsource.descent.flute.shared;

// TODO remove when done ad-hoc testing
import std.stdio : writef, writefln;

public class Symbol
{
	/**
	 * The mangled name of this symbol
	 */
	public string mangledName;
	
	/**
	 * Human-readable fully-qualified name of the symbol
	 */
	public string name;
	
	/**
	 * Start address of they symbol
	 */
	public void* addr;
	
	/**
	 * Size of the symbol
	 */
	public size_t size;
}

public class Function : Symbol
{
	
}

public struct StackTraceElement
{
	/**
	 * The address that was executing at the time.
	 */
	void* addr;
	
	/**
	 * The function that was executing at the time, or null if the function
	 * could not be determined.
	 */
	Function func;
}

/**
 * Alias for an array of stack trace elements. The stack trace elements will be
 * ordered from where the exception was thrown down.
 */
public alias StackTraceElement[] StackTrace;

version(DigitalMars)
{
	/**
	 * DMD reorders the parameters to the exception constructor, so order them
	 * the same as GDC creates.
	 */
	private Exception createTracedExceptionDmd(char[] msg, Exception e)
	{
		return createTracedException(e, msg);
	}
}

/**
 * Replaces the constructor of object.Exception to create a traced exception
 */
private Exception createTracedException(Exception e, char[] msg)
{	
	e.msg = msg;
	traces[e] = createStackTrace(e);
	return e;
}

/**
 * This function was written by Thomas Kuehne for use in Flectioned. Walks the
 * stack to create a stack trace.
 * 
 * Params:
 *     e = 
 * Returns:
 */
private StackTrace createStackTrace(Exception e)
{
	// PERHAPS use StalkWalk on Windows... that looks like a huge PITA, though
	
	// Returns true if a pointer is a valid read pointer
	bool mayRead(size_t addr)
	{
		version(Windows)
		{
			return !IsBadReadPtr(cast(PVOID) addr, (void*).sizeof);
		}
	}
	
	const size_t TRACE_PREALLOC = 16;
	StackTrace trace = new StackTraceElement[TRACE_PREALLOC];
	trace.length = 0;
	
	// Get the base pointer of this function to start at
	size_t bp;
	static if((void*).sizeof == 4)
	{
		asm
		{
			mov bp, EBP;
		}
	}
	else static if(8 == (void*).sizeof)
	{
		uint a, b;
		asm
		{
			// gdc-0.23's X86_64 asm is broken
			db 0x48, 0x89, 0xE8; // mov rax, rbp
			db 0x48, 0x89, 0xE9; // mov rcx, rbp
			db 0x48, 0xc1, 0xc0, 0x20; // rol rax, 32
			mov a, EAX;
			mov b, ECX;
		}
		bp = a;
		bp <<= 32;
		bp |= b;
	}
	else
	{
		static assert(0, "Unhandled pointer size");
	}
	
	size_t tmp;
	void* caller;

	while(bp)
	{	
		tmp = bp + size_t.sizeof;
		if(!mayRead(tmp))
			break;
		
		caller = cast(void*) *(cast(size_t*) tmp);
		if(!mayRead(bp))
			break;
		
		bp = *(cast(size_t*) bp);
		
		if((null is caller) || (0 == bp))
			break;
		
		StackTraceElement ste;
		ste.addr = cast(void*) bp;
		ste.func = cast(Function) internalFindSymbol(caller);
		trace ~= ste;
	}
	
	return trace;
}

private Symbol internalFindSymbol(void* addr)
{
	foreach(sym; symbols_)
	{
		if(sym.addr <= addr && addr < sym.addr + sym.size)
			return sym;
		return null;
	}
}

// Since symbol creation ans symbol lookup must be possible outside the scope
// of the class (since exception tracing functions need C llinkage), the class
// has few instance variables. That said, all the external interfaces to this
// module should go through ReflectionProvider
private StackTrace[Exception] traces;
private Symbol[] symbols_;
private Function[] unittests_;

//------------------------------------------------------------------------------
private ReflectionProvider reflectionProvider;

/**
 * Gets the reflection provider for this OS. On the first call, will
 * enumerate symbols and intialize run-time reflection to provide a new
 * instance of a reflection provcider. All future calls may return a cached
 * instance, but are not garunteed to, so for this reason, modules needing
 * reflection should maintain their own copies of this reflection provider
 * instance.
 */
public ReflectionProvider getReflectionProvider()
{
	if(null is reflectionProvider)
	{
		version(Windows)
		{
			reflectionProvider = new WindowsReflectionProvider();
		}
	}
	
	return reflectionProvider;
}

public abstract class ReflectionProvider
{	
	/**
	 * Gets the list of all symbols in the binary
	 */
	public final Symbol[] functions()
	{
		return symbols_;
	}
	
	/**
	 * Gets the list of all unittests in the binary. These will be returned by
	 * the functions() method as well.
	 */
	public final Function[] unittests()
	{
		return unittests_;
	}
	
	/**
	 * Attempts to look up the file/line in the debug info given an execution
	 * address.
	 * 
	 * Params:
	 *     addr = the execution address to look up
	 *     line = the line that was executing
	 *     file = the file that was executing, as reported by the debug info
	 * Returns: true if and only if the lookup was succesful, false otherwise.
	 */
	public abstract bool getLine(void* addr, out int line, out char[] file);
	
	/**
	 * Finds the stack trace for the given exception, or returns null if no 
	 * stack trace is availible.
	 * 
	 * Params:
	 *     e = the exception to get the stack trace for
	 * Returns: the stack trace of the exception or null if no stack trace
	 *          is available.
	 */
	public final StackTrace getStackTrace(Exception e)
	{
		if(!e)
			return null;
		StackTrace* ex = e in traces;
		return ex ? *ex : null;
	}
	
	/**
	 * Finds the symbol the given address is in
	 * 
	 * Params:
	 *     addr = the address to look up
	 * Returns: the symbol the address is in, or null if the symbol could not
	 *          be found.
	 */
	public final Symbol findSymbol(void* addr)
	{
		return internalFindSymbol(addr);
	}
	
	protected final void addSymbol(string name, void* addr, size_t size, bool isPublic = true)
	{	
		bool isUnittest(string sym)
		{
			const string UNITTEST_MARKER = ".__unittest";
			
			// Check if it has the marker in it
			int i = find(sym, UNITTEST_MARKER);
			if(0 > i)
				return false;
			
			// Check if the marker is the last part of the name
			i += UNITTEST_MARKER.length;
			while(i < sym.length && isdigit(sym[i]))
				i++;
			if(i != sym.length)
				return false;
			
			return true;
		}
		
		Symbol sym = createSymbol(name, addr, size, isPublic);
		
		if(null !is sym)
		{
			symbols_ ~= sym;
			Function fn = cast(Function) sym;
			if(isPublic && fn && isUnittest(fn.name))
				unittests_ ~= fn;
		}
	}
	
	/**
	 * Written by Thomas Kuehne. Parses the mangled name to create the actual
	 * symbol class.
	 */
	private final Symbol createSymbol(string orig, void* addr, size_t size, bool isPublic)
	{
		Symbol sym;
		char[] name = orig.dup;
		
		// Ignore DMD-generated foreach bodies
		if(name.find("__foreachbody") > 0)
			goto LdontInclude;
		
		writefln("%s => %s", name, demangle(name));
		
		if(isPublic && (3 < name.length) && ('D' == name[0]) && isdigit(name[1]))
		{
			// bug #637: internal symbols are missing the leading underscore
			// http://d.puremagic.com/issues/show_bug.cgi?id=637
			name = "_" ~ name;
		}
		
		// TODO finish porting
		
		LdontInclude:
			return null;
			
	}
	
	/**
	 * Lifted from Flectioned by Thomas Kuehne and not modified very much.
	 * Redirects a function call from one location to another. On Windows,
	 * this is done by rewriting the memory of the running process, so I'm
	 * surprised it works on Vista, but it does, so no complaints!
	 * 
	 * Authors:
	 * Thomas Kuehne
	 * 
	 * Params:
	 *     from = the old function address
	 *     to = the new function address
	 */
	protected final void redirectFunctionCall(void* from, void* to)
	{
		// compute ASM
		ubyte[6] cmd;
		cmd[0] = 0xE9; // jmp
		cmd[5] = 0xC3; // retn

		size_t new_dest = cast(size_t) to;
		new_dest = new_dest - (cast(size_t)from + 5);
		int offset = cast(int)cast(ptrdiff_t)new_dest;

		cmd[1 .. 1 + offset.sizeof] = (cast(ubyte*)&offset)[0 .. offset.sizeof];

		// save original
		ubyte[] original = (cast(ubyte*)from)[0 .. cmd.length].dup;

		// write asm
		procWrite(from, cmd);

		return original;
	}
	
	/**
	 * Rewrites the data in a particular location in memory of the current
	 * process to the given data. Also lifted from Flectioned, and abstract here
	 * because it's done in an OS-specific manner. Should not be used when
	 * intoxicated. May cause serious injuries or death if used improperly.
	 * 
	 * Params:
	 *     addr = the address to write to
	 *     data = the data to be written
	 */
	protected abstract void procWrite(void* addr, ubyte[] data);
}

// Windows-specific functuionality
//------------------------------------------------------------------------------
version(Windows)
{
	// Taste my tears, Microsoft... They are the tears of the many who have been
	// forced to work with your terrible APIs (SQL Server/DP aside, of course,
	// we have the bestest APIs evar).
	
	import std.c.windows.windows;
	
	// Additional windows/D function definitions
	//--------------------------------------------------------------------------
	private enum
	{
		MAX_MODULE_NAME32 = 255,
		TH32CS_SNAPMODULE = 0x00000008,
		SYMOPT_LOAD_LINES = 0x10,
	}
	
	private extern(Windows) struct MODULEENTRY32
	{
		DWORD  dwSize;
		DWORD  th32ModuleID;
		DWORD  th32ProcessID;
		DWORD  GlblcntUsage;
		DWORD  ProccntUsage;
		BYTE  *modBaseAddr;
		DWORD  modBaseSize;
		HMODULE hModule;
		char   szModule[MAX_MODULE_NAME32 + 1];
		char   szExePath[MAX_PATH];
	}
	
	private extern(Windows) struct IMAGEHLP_LINE
	{
		DWORD SizeOfStruct;
	    PVOID Key; 
	    DWORD LineNumber; 
	    PTSTR FileName; 
	    DWORD Address;
	}
	alias IMAGEHLP_LINE* PIMAGEHLP_LINE;
	
	private extern(Windows) BOOL Module32First(HANDLE, MODULEENTRY32*);
	private extern(Windows) BOOL Module32Next(HANDLE, MODULEENTRY32*);
	private extern(Windows) HANDLE CreateToolhelp32Snapshot(DWORD,DWORD);
	
	// Phobos doesn't have these; Tango does
	static if(!is(typeof(IsBadReadPtr)))
		private extern(Windows) BOOL IsBadReadPtr(LPCVOID, UINT);
	static if(!is(typeof(SetLastError)))
		private extern(Windows) VOID SetLastError(DWORD);
	static if(!is(typeof(WriteProcessMemory)))
		private extern(Windows) BOOL WriteProcessMemory(HANDLE, void*, void*, size_t, size_t*);
	
	// D's object constructor
	private extern(C) void D6object9Exception5_ctorMFAaZC9Exception();
	private alias D6object9Exception5_ctorMFAaZC9Exception exceptionCtor;
	
	//--------------------------------------------------------------------------
	
	// Handle to the current process
	private HANDLE proc;
	
	// The imagehlp library and its needed functions
	private HMODULE imagehlp;
	private extern(Windows) DWORD function(DWORD) SymSetOptions;
	private extern(Windows) BOOL function(HANDLE, PCSTR, BOOL) SymInitialize;
	private extern(Windows) BOOL function(HANDLE) SymCleanup;
	private extern(Windows) DWORD function(HANDLE, HANDLE, PCSTR, PCSTR, DWORD, DWORD) SymLoadModule;
	private extern(Windows) BOOL function(HANDLE, DWORD, void*, void*) SymEnumerateSymbols;
	private extern(Windows) BOOL function(HANDLE, DWORD, PDWORD, PIMAGEHLP_LINE) SymGetLineFromAddr;
	
	// Callback function for adding symbols
	private extern(Windows) int symCallback(LPSTR name, ULONG addr, ULONG size, PVOID context)
	{
		(cast(WindowsReflectionProvider) context).
				addSymbol(name[0 .. strlen(name)], cast(void*) addr, size);
		return true;
	}
	
	private final class WindowsReflectionProvider : ReflectionProvider
	{
		// When the initialization completes, this is set to true. This exists
		// to avoid AVs if the GC tries to collect an uninitialized instance.
		private bool shouldDelete = false;
		
		private this()
		{
			loadLibrary();
			enumerateSymbols();
			initExceptionTracing();
		}
		
		private void loadLibrary()
		{
			// Load the imagehlp library and load its functions
			imagehlp = LoadLibraryA("imagehlp.dll");
			if(!imagehlp)
				throw new Exception("Failed to load imagehlp.dll");
			SymSetOptions = cast(typeof(SymSetOptions)) GetProcAddress(imagehlp, "SymSetOptions");
			if(!SymSetOptions)
				throw new Exception("Failed to load SymSetOptions");
			SymInitialize = cast(typeof(SymInitialize)) GetProcAddress(imagehlp, "SymInitialize");
			if(!SymInitialize)
				throw new Exception("Failed to load SymInitialize");
			SymCleanup = cast(typeof(SymCleanup)) GetProcAddress(imagehlp, "SymCleanup");
			if(!SymCleanup)
				throw new Exception("Failed to load SymCleanup");
			SymLoadModule = cast(typeof(SymLoadModule)) GetProcAddress(imagehlp, "SymLoadModule");
			if(!SymLoadModule)
				throw new Exception("Failed to load SymLoadModule");
			SymEnumerateSymbols = cast(typeof(SymEnumerateSymbols)) GetProcAddress(imagehlp, "SymEnumerateSymbols");
			if(!SymEnumerateSymbols)
				throw new Exception("Failed to load SymEnumerateSymbols");
			SymGetLineFromAddr = cast(typeof(SymGetLineFromAddr)) GetProcAddress(imagehlp, "SymGetLineFromAddr");
			if(!SymGetLineFromAddr)
				throw new Exception("Failed to load SymGetLineFromAddr");
		}
		
		private void enumerateSymbols()
		{
			MODULEENTRY32 moduleEntry;
			moduleEntry.dwSize = moduleEntry.sizeof;
			char buffer[4096];
			
			// Get the current function
			proc = GetCurrentProcess();
			if(!proc)
				throw new Exception("GetCurrentProcess() returned null");
			
			// Create a snapshot of the process (for great happiness)
			HANDLE snapshot = CreateToolhelp32Snapshot(TH32CS_SNAPMODULE, 0);
			if(!snapshot)
				throw new Exception("CreateToolHelp32Snapshot failed");
			
			// We need to have SYMOPT_LOAD_LINES on, since we're using that to
			// get line information ins stack traces
			SymSetOptions(SYMOPT_LOAD_LINES);
			
			// Initialize the symbol enumeration for the process
			if(!SymInitialize(proc, null, FALSE))
				throw new Exception("SymInitialize failed");
			
			// Enumerate through the modules...
			if(!Module32First(snapshot, &moduleEntry))
				throw new Exception("Module32First Failed");
			do
			{	
				// Get the module file (if we can't, we won't be able to
				// enumerate the symbols
				if(!GetModuleFileNameA(moduleEntry.hModule, buffer.ptr, buffer.length))
					continue;
				
				// Load up the module itself
				DWORD baseAddr = SymLoadModule(proc, HANDLE.init, buffer.ptr, null, 0, 0);
				if(!baseAddr)
					continue;
				
				SymEnumerateSymbols(proc, baseAddr, cast(void*) &symCallback, cast(void*) this);
			}
			while(Module32Next(snapshot, &moduleEntry));
			
			shouldDelete = true;
		}
		
		private void initExceptionTracing()
		{
			version(DigitalMars)
			{
				redirectFunctionCall(cast(void*) &exceptionCtor, 
						cast(void*) &createTracedExceptionDmd);
			}
			else
			{
				static assert(false, "This compiler is not supported on this OS");
			}
		}
		
		public final bool getLine(void* addr, out int line, out char[] file)
		{
			if(!shouldDelete || !addr)
				goto Lunknown;
			
			IMAGEHLP_LINE lineInfo;
			DWORD displacement;
			lineInfo.SizeOfStruct = lineInfo.sizeof;
			
			if(!SymGetLineFromAddr(proc, cast(DWORD) addr, &displacement, &lineInfo))
				goto Lunknown;
			
			line = lineInfo.LineNumber;
			file = lineInfo.FileName[0 .. strlen(lineInfo.FileName)];
			return true;
			
			Lunknown:
				return false;
		}
		
		protected final void procWrite(void* addr, ubyte[] data)
		{
			size_t written = 0;
			SetLastError(0);
			if(!WriteProcessMemory(proc, addr, data.ptr, data.length, &written)
					|| written != data.length)
			{
				throw new Exception(format("Writing to process failed: %d", GetLastError()));
			}
		}
		
		private ~this()
		{
			if(shouldDelete)
				SymCleanup(proc);
		}
	}
}
else
{
	static assert(false, "Flute reflection has not been enabled on this OS.");
}
	
public void main(string[] args)
{
	auto provider = getReflectionProvider();
	
	/+ writefln();
	writefln("Functions:");
	writefln("----------");
	foreach(fn; provider.functions)
		writefln("%s", fn.mangledName);
	
	writefln();
	writefln("Exception:");
	writefln("----------");
	try
	{
		throw new Exception("Lollerskates!");
	}
	catch(Exception e)
	{
		StackTrace trace = provider.getStackTrace(e);
		if(null !is trace)
			foreach(StackTraceElement ste; trace)
				writefln("0x%s", toHex(ste.addr));
		else
			writefln("Exception was not traced");
	}
	
	writefln();
	writefln("Access violation:");
	writefln("-----------------");
	try
	{
		Object o;
		writefln(o.classinfo.name);
	}
	catch(Exception e)
	{
		StackTrace trace = provider.getStackTrace(e);
		if(null !is trace)
			foreach(StackTraceElement ste; trace)
				writefln("0x%s", toHex(ste.addr));
		else
			writefln("Exception was not traced");
	} +/
}