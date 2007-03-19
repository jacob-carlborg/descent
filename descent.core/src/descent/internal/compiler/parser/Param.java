package descent.internal.compiler.parser;

import java.util.List;

public class Param {

	char obj;		// write object file
    char link;		// perform link
    char trace;		// insert profiling hooks
    char quiet;		// suppress non-error messages
    char verbose;	// verbose compile
    char symdebug;	// insert debug symbolic information
    char optimize;	// run optimizer
    char cpu;		// target CPU
    char isX86_64;	// generate X86_64 bit code
    char isLinux;	// generate code for linux
    char isWindows;	// generate code for Windows
    char scheduler;	// which scheduler to use
    boolean useDeprecated = false;	// allow use of deprecated features
    char useAssert;	// generate runtime code for assert()'s
    char useInvariants;	// generate class invariant checks
    char useIn;		// generate precondition checks
    char useOut;	// generate postcondition checks
    char useArrayBounds; // generate array bounds checks
    char useSwitchError; // check for switches without a default
    char useUnitTests;	// generate unittest code
    char useInline;	// inline expand functions
    char release;	// build release version
    char preservePaths;	// !=0 means don't strip path from source file
    boolean warnings;	// enable warnings
    char pic;		// generate position-independent-code for shared libs
    char cov;		// generate code coverage data
    char nofloat;	// code should not pull in floating point support
    int Dversion;	// D version number

    String argv0;	// program name
    List<String> imppath; // array of char*'s of where to look for import modules
    List<String> fileImppath;	// array of char*'s of where to look for file import modules
    String objdir;	// .obj file output directory
    String objname;	// .obj file output name

    char doDocComments;	// process embedded documentation comments
    String docdir;	// write documentation file to docdir directory
    String docname;	// write documentation file to docname
    List<String> ddocfiles;	// macro include files for Ddoc

    char doHdrGeneration;	// process embedded documentation comments
    String hdrdir;		// write 'header' file to docdir directory
    String hdrname;		// write 'header' file to docname

    int debuglevel;	// debug level
    List<String> debugids;		// debug identifiers

    int versionlevel;	// version level
    List<String> versionids;		// version identifiers

    boolean dump_source;

    // Hidden debug switches
    char debuga;
    char debugb;
    char debugc;
    char debugf;
    char debugr;
    char debugw;
    char debugx;
    char debugy;

    char run;		// run resulting executable
    int runargs_length;
    String[] runargs;	// arguments for executable

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
