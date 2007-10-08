package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

// DMD 1.020
public class Param {

	public char obj;		// write object file
	public char link;		// perform link
	public char trace;		// insert profiling hooks
	public boolean quiet;		// suppress non-error messages
	public boolean verbose;	// verbose compile
	public char symdebug;	// insert debug symbolic information
	public char optimize;	// run optimizer
	public char cpu;		// target CPU
	public boolean isX86_64;	// generate X86_64 bit code
	public char isLinux;	// generate code for linux
	public char isWindows;	// generate code for Windows
	public char scheduler;	// which scheduler to use
	public boolean useDeprecated = false;	// allow use of deprecated features
	public boolean useAssert = true;	// generate runtime code for assert()'s
	public boolean useInvariants = true;	// generate class invariant checks
	public boolean useIn = true;		// generate precondition checks
	public boolean useOut = true;	// generate postcondition checks
	public boolean useArrayBounds = true; // generate array bounds checks
	public boolean useSwitchError = true; // check for switches without a default
	public boolean useUnitTests = true;	// generate unittest code
	public boolean useInline;	// inline expand functions
	public boolean release;	// build release version
	public boolean preservePaths;	// !=0 means don't strip path from source file
	public boolean warnings = true;	// enable warnings
	public char pic;		// generate position-independent-code for shared libs
	public char cov;		// generate code coverage data
	public char nofloat;	// code should not pull in floating point support
	public int Dversion;	// D version number

	public String argv0;	// program name
	public List<String> imppath; // array of char*'s of where to look for import modules
	public List<String> fileImppath;	// array of char*'s of where to look for file import modules
	public String objdir;	// .obj file output directory
	public String objname;	// .obj file output name

	public char doDocComments;	// process embedded documentation comments
	public String docdir;	// write documentation file to docdir directory
	public String docname;	// write documentation file to docname
	public List<String> ddocfiles;	// macro include files for Ddoc

	public char doHdrGeneration;	// process embedded documentation comments
	public String hdrdir;		// write 'header' file to docdir directory
	public String hdrname;		// write 'header' file to docname

	public long debuglevel;	// debug level
	public List<char[]> debugids;		// debug identifiers

	public long versionlevel;	// version level
	public List<char[]> versionids;		// version identifiers

	public boolean dump_source;

    // Hidden debug switches
	public char debuga;
	public char debugb;
	public char debugc;
	public char debugf;
	public char debugr;
	public char debugw;
	public char debugx;
	public char debugy;

	public char run;		// run resulting executable
	public int runargs_length;
	public  String[] runargs;	// arguments for executable
	
	public Param() {
		versionids = new ArrayList<char[]>();
		versionids.add("DigitalMars".toCharArray());
		versionids.add("Windows".toCharArray());
		versionids.add("Win32".toCharArray());
		versionids.add("X86".toCharArray());
		versionids.add("LittleEndian".toCharArray());
		versionids.add("D_InlineAsm".toCharArray());
		versionids.add("D_InlineAsm_X86".toCharArray());
		versionids.add("all".toCharArray());
	}

    // Linker stuff
    /*
    Array *objfiles;
    Array *linkswitches;
    Array *libfiles;
    char *deffile;
    char *resfile;
    char *exefile;
    */
	
}
