/**
 * Ant Task for building D programs.
 *
 * Authors:
 *  Frank Benoit (benoit at tionex dot de)
 *  Charles Sanders
 *
 * License:
 *  Public Domain
 */
package anttasks;

/**
 * To use this anttask, it must be in the classpath of ANT.
 * Within eclipse this can be done in
 * Window->Preferences: ANT->Runtime->Classpath->Global Entries->Add Folder (add the anttask 'bin' folder)
 * 
 * To make the task public to ANT a task definition is needed.
 * Either do it Eclipse globally:
 * Window->Preferences: ANT->Runtime->Classpath->Tasks->Add Task (Name: D, Folder choose the bin folder.)
 * or add the definition to the build.xml
 * <taskdef classname="anttasks.D" name="D" />
 * 
 * To run ant from the commandline, ant.jar, ant-launcher.jar and this compiled class need to be on the 
 * classpath. On linux you can use a script with this content:
 * #!/bin/sh
 * export ECLIPSE_DIR=/opt/eclipse
 * export ANT_DIR=$ECLIPSE_DIR/plugins/org.apache.ant_1.6.5
 * export ANT_LIB_DIR=$ANT_DIR/lib
 * java -cp ~/descent/anttasks/bin/:$ANT_LIB_DIR/ant-launcher.jar:$ANT_LIB_DIR/ant.jar org.apache.tools.ant.launch.Launcher $*
 * 
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.HashSet;
import java.util.LinkedList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.DirSet;
import org.apache.tools.ant.types.FileSet;

/*
 * mode="object"
 * 	compile all supplied sources into object files
 * mode="executable"
 * 	compile all supplied sources into object files and call the linker for building the executable

<D
  	type        = "dmd-linux|dmd-windows|gdb"
	mode        = "objects|executable|library-static|library-dynamic"
	compilerdir = "path"
  	header      = "true|false*"
  	headerdir   = "~/hdrdir"
  	headername  = "filename.di" 
  	ddoc        = "true|false"
  	ddocdir     = "~/ddoc"
  	ddocname    = "filename.html"
  	debuginfo   = "true|false*"
  	debuginfo_c = "true|false*"
  	optimize    = "true|false*"
  	profile     = "true|false*"
  	quiet       = "true|false*"
  	release     = "true|false*"
  	unittest    = "true|false*"
  	verbose     = "true|false*"
  	warnings    = "true|false*"
  	cleanup     = "true*|false"
  	stdargs     = "true*|false"
  	mapfile     = "file"
  	deffile     = "file"
  	resfile     = "file"
	>
	<debug/>
	<debug   value="1"/>
	<version value="1"/>
	
	<!-- The main modules -->
	<mainmodules>
		<fileset file="main.d"/>
	</mainmodules>
	
	<!-- Modules for compilation and linking, if imported -->
	<includemodules>
		<dirset dir="${dater.dir}">
			<include name="lib/util"/>
		</dirset>
	</includemodules>

	<!-- Imported modules, only for declarations, no compile/link (libs) -->
	<includepath>
		<dirset file="${dater.dir}/dater/lib/duit" />
	</includepath>

	<!-- list flags for the linker -->
	<linkflag value="-L/usr/lib" />

	
	<!-- list the libs, the linker shall link -->
	<linklib type="static|dynamic" name="name" />
	
</D>
 
 */

public class D extends Task {

	LinkedList<File> mMainModules     = new LinkedList<File>();
	LinkedList<File> mIncludedModules = new LinkedList<File>();
	LinkedList<File> mIncludePaths    = new LinkedList<File>();

	HashSet<String> mCompileFiles = new HashSet<String>();
	HashSet<String> mCompileFqns  = new HashSet<String>();

	enum Mode {
		/// not valid, the mode has to be defined
		NOTSET,
		
		/// only build the object files
		OBJECTS,
		
		/// compile the mainmodules and all dependent modules. Link an executable
		EXECUTABLE,
		
		/// compile the mainmodules and link a static library
		LIBRARY_STATIC,
		
		/// compile the mainmodules and link a dynamic library
		LIBRARY_DYNAMIC;
	}
	
	String type;
	public void setType( String type ){
		this.type = type;
	}
	Mode mode = Mode.NOTSET;
	public void setMode( String mode ){
		if( mode.equals( "objects" ) ){
			this.mode = Mode.OBJECTS;
		}
		else if( mode.equals( "executable" ) ){
			this.mode = Mode.EXECUTABLE;
		}
		else if( mode.equals( "library-static" ) ){
			this.mode = Mode.LIBRARY_STATIC;
		}
		else if( mode.equals( "library-dynamic" ) ){
			this.mode = Mode.LIBRARY_DYNAMIC;
		}
		else {
			throw new BuildException( String.format("mode can only be set to \"objects\", \"executable\" or \"library\"."));
		}
	}
	private boolean compile = true;
	public void setCompile( boolean value ){
		this.compile = value;
	}
	private boolean isWindows(){
		return Os.isFamily( "windows" );
	}

	String mapfile;
	public void setMapFile( File value ){
		if( !isWindows() ){
			throw new BuildException( "the mapfile option is available only on windows" );
		}
		mapfile= value.getAbsolutePath();
	}
	
	String deffile;
	public void setDefFile( File value ){
		if( !isWindows() ){
			throw new BuildException( "the mapfile option is available only on windows" );
		}
		deffile= value.getAbsolutePath();
	}
	
	String resfile;
	public void setResFile( File value ){
		if( !isWindows() ){
			throw new BuildException( "the mapfile option is available only on windows" );
		}
		resfile= value.getAbsolutePath();
	}
	
	
	String compilerdir;
	public void setCompilerdir( File value ){
		compilerdir = value.getAbsolutePath();
	}
	private boolean header = false;
	public void setHeader( boolean value ){
		this.header = value;
	}
	private boolean ddoc = false;
	public void setDdoc( boolean value ){
		this.ddoc = value;
	}
	boolean debuginfo = false;
	public void setDebuginfo( boolean value ){
		this.debuginfo = value;
	}
	boolean debuginfo_c = false;
	public void setDebuginfo_c( boolean value ){
		this.debuginfo_c = value;
	}
	private boolean cleanup = true;
	public void setCleanup( boolean value ){
		this.cleanup = value;
	}
	boolean warnings = false;
	public void setWarnings( boolean value ){
		this.warnings = value;
	}
	boolean stdargs = true;
	public void setStdargs( boolean value ){
		this.stdargs = value;
	}
	File destfile;
	public void setDestfile( File value ){
		this.destfile = value;
	}

	// --- Nested ---
	LinkedList<LinkFlag> linkflags = new LinkedList<LinkFlag>();
	public LinkFlag createLinkflag(){
		LinkFlag result = new LinkFlag();
		linkflags.add( result );
		return result;
	}
	LinkedList<LinkLib> linkLibs = new LinkedList<LinkLib>();
	public LinkLib createLinklib(){
		LinkLib result = new LinkLib();
		linkLibs.add( result );
		return result;
	}
	LinkedList<Version> versionflags = new LinkedList<Version>();
	public Version createVersion(){
		Version result = new Version();
		versionflags.add( result );
		return result;
	}
	LinkedList<Debug> debugflags = new LinkedList<Debug>();
	public Debug createDebug(){
		Debug result = new Debug();
		debugflags.add( result );
		return result;
	}
	LinkedList<IncludePath> includepaths = new LinkedList<IncludePath>();
	public IncludePath createIncludepath(){
		IncludePath result = new IncludePath();
		includepaths.add( result );
		return result;
	}

	// modules that must be compiled and linked, independent from any dependency calculation.
	// this is at least the module with the main function.
	// Later, in case we get reflection, this can hold other modules as well.
	// In case of a library, all modules shall be compiled and linked, so list them as mainmodules.
	LinkedList<MainModules> mainModuless = new LinkedList<MainModules>();
	public MainModules createMainModules(){
		MainModules result = new MainModules();
		mainModuless.add( result );
		return result;
	}

	// modules that are compiled and linked if the dependency calculation detects a dependency from a main module.
	LinkedList<IncludeModules> includeModuless = new LinkedList<IncludeModules>();
	public IncludeModules createIncludeModules(){
		IncludeModules result = new IncludeModules();
		includeModuless.add( result );
		return result;
	}

	@Override
	public void execute() throws BuildException {

		log( String.format("Building %s\n", destfile     ), Project.MSG_INFO );

		try{
		Compiler compiler = null;
		if( type == null ){
			throw new BuildException("type is not set");
		}
		else if( type.equals("gdc")){
			compiler = new Gdc(this);
		}
		else if( type.equals("dmd-linux")){
			compiler = new DmdLinux(this);
		}
		else if( type.equals("dmd-windows")){
			compiler = new DmdWindows(this);
		}
		else{
			throw new BuildException("type must be one of dmd/gdc");
		}
		
		log( String.format("D Task: compile     = %s\n", compile     ), Project.MSG_VERBOSE );
		log( String.format("D Task: compilerdir = %s\n", compilerdir ), Project.MSG_VERBOSE );
		log( String.format("D Task: mode        = %s\n", mode.toString() ), Project.MSG_VERBOSE );
		log( String.format("D Task: header      = %s\n", header      ), Project.MSG_VERBOSE );
		log( String.format("D Task: ddoc        = %s\n", ddoc        ), Project.MSG_VERBOSE );
		log( String.format("D Task: debuginfo   = %s\n", debuginfo   ), Project.MSG_VERBOSE );
		log( String.format("D Task: debuginfo_c = %s\n", debuginfo_c ), Project.MSG_VERBOSE );
		log( String.format("D Task: destfile    = %s\n", destfile    ), Project.MSG_VERBOSE );
		log( String.format("D Task: cleanup     = %s\n", cleanup     ), Project.MSG_VERBOSE );
		log( String.format("D Task: warnings    = %s\n", warnings    ), Project.MSG_VERBOSE );
		log( String.format("D Task: stdargs     = %s\n", stdargs     ), Project.MSG_VERBOSE );
		if( mapfile != null ){
			log( String.format("D Task: mapfile     = %s\n", mapfile     ), Project.MSG_VERBOSE );
		}
		if( deffile != null ){
			log( String.format("D Task: deffile     = %s\n", deffile     ), Project.MSG_VERBOSE );
		}
		if( resfile != null ){
			log( String.format("D Task: resfile     = %s\n", resfile     ), Project.MSG_VERBOSE );
		}
		
		for( MainModules mainModules : mainModuless ){
			for( FileSet fileset : mainModules.mods ){
				DirectoryScanner scanner = fileset.getDirectoryScanner( getProject() );
				scanner.scan();
				String[] names = scanner.getIncludedFiles();
				for( String name : names ){
					File srcFile = new File( scanner.getBasedir(), name );
					mMainModules.add(srcFile );
					log( String.format("D Task: main module = %s\n", srcFile.getAbsolutePath() ), Project.MSG_VERBOSE );
					if( !name.endsWith( ".d" )){
						throw new BuildException( String.format( "The source file %s does not end with \".d\".", name ));
					}
				}
			}
		}
		for( IncludeModules modules : includeModuless ){
			for( DirSet dirset : modules.paths ){
				DirectoryScanner scanner = dirset.getDirectoryScanner( getProject() );
				scanner.scan();
				String[] names = scanner.getIncludedDirectories();
				for( String name : names ){
					File srcFile = new File( scanner.getBasedir(), name );
					mIncludedModules.add(srcFile );
					log( String.format("D Task: includepath = %s\n", srcFile.getAbsolutePath() ), Project.MSG_VERBOSE );
					if( !srcFile.isDirectory()){
						throw new BuildException( String.format( 
								"The include path %s is not an existing directory", srcFile.getAbsolutePath() ));
					}
				}
			}
		}
		for( IncludePath modules : includepaths ){
			for( DirSet dirset : modules.paths ){
				DirectoryScanner scanner = dirset.getDirectoryScanner( getProject() );
				scanner.scan();
				String[] names = scanner.getIncludedDirectories();
				for( String name : names ){
					File srcFile = new File( scanner.getBasedir(), name );
					mIncludePaths.add(srcFile );
					log( String.format("D Task: includepath = %s\n", srcFile.getAbsolutePath() ), Project.MSG_VERBOSE );
					if( !srcFile.isDirectory()){
						throw new BuildException( String.format( 
								"The include path %s is not an existing directory", srcFile.getAbsolutePath() ));
					}
				}
			}
		}

		for( LinkFlag flag : linkflags ){
			log( String.format("D Task: linkflag    = %s\n", flag.value ), Project.MSG_VERBOSE );
		}
		for( LinkLib linkLib : linkLibs ){
			if( linkLib.linkType == null ){
				throw new BuildException( "linklib needs a type" );
			}
			switch( linkLib.linkType ){
			case STATIC:
				log( String.format("D Task: link static = %s\n", linkLib.name ), Project.MSG_VERBOSE );
				break;
			case DYNAMIC:
				log( String.format("D Task: link dynamic= %s\n", linkLib.name ), Project.MSG_VERBOSE );
				break;
			}
		}

		try{
			compiler.calcDependencies();
			compiler.build();
		}
		finally{
			if( cleanup ){
				compiler.cleanup();
			}
		}
	
		}
		catch( NullPointerException e ){
			e.printStackTrace();
			throw e;
		}
	}
	
	File executeCmdCreateOutputFile( LinkedList<String> cmdlist ){
		String[] cmdparts = cmdlist.toArray(new String[0]);
		try {
			File t = File.createTempFile("AntMsgs", "log" );
			FileOutputStream ostr = new FileOutputStream( t );
			log(Commandline.describeCommand(cmdparts),
                    Project.MSG_VERBOSE);
			Execute exe = new Execute(new PumpStreamHandler(ostr));
			exe.setAntRun(getProject());
			exe.setCommandline(cmdparts);
			int retval = exe.execute();
			if (Execute.isFailure(retval)) {
				ostr.close();
				BufferedReader rd = new BufferedReader( new FileReader( t ));
				String line;
				while((line=rd.readLine()) != null ){
					log( line, Project.MSG_ERR );
				}
				rd.close();
				throw new BuildException(cmdparts[0]
				                                 + " failed with return code " + retval, getLocation());
			}
			ostr.close();
			return t;
		} catch (java.io.IOException exc) {
			throw new BuildException("Could not launch " + cmdparts[0] + ": "
					+ exc, getLocation());
		}
	}
	
	void executeCmd( LinkedList<String> cmdlist ){
		Execute.runCommand(this, cmdlist.toArray(new String[0]));
	}
	
	public class Version {
		String value;
		public void setValue(String value ){
			this.value = value;
		}
	}
	public class Debug {
		String value = "";
		public void setValue(String value ){
			this.value = value;
		}
	}
	public class LinkFlag {
		String value;
		public void setValue(String value ){
			this.value = value;
		}
	}


	public static class LinkLib {
		enum LinkType{
			STATIC, DYNAMIC
		}
		
		String name;
		public void setName(String value ){
			this.name = value;
		}

		LinkType linkType;
		public void setType(String value ){
			if( "static".equals(value)){
				linkType = LinkType.STATIC;
			}
			else if( "dynamic".equals(value)){
				linkType = LinkType.DYNAMIC;
			}
			else{
				throw new BuildException( "only 'static' or 'dynamic' is allowed for linklib type." );
			}
		}
}

	public class MainModules {
		LinkedList<FileSet> mods = new LinkedList<FileSet>();
		public FileSet createFileSet(){
			FileSet result = new FileSet();
			mods.add( result );
			return result;
		}
	}
	public class IncludeModules {
		LinkedList<DirSet> paths = new LinkedList<DirSet>();
		public DirSet createDirSet(){
			DirSet result = new DirSet();
			paths.add( result );
			return result;
		}
	}
	
	public class IncludePath {
		LinkedList<DirSet> paths = new LinkedList<DirSet>();
		public DirSet createDirSet(){
			DirSet result = new DirSet();
			paths.add( result );
			return result;
		}
	}
}
