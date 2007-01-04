/**
 * Ant Task for building D programs.
 *
 * Authors:
 *  Frank Benoit
 *
 * License:
 *  Copyright (c) 2006  Frank Benoit
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 * 
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 *  DEALINGS IN THE SOFTWARE.
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
 */

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;
import org.apache.tools.ant.types.DirSet;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.util.TeeOutputStream;

/*
 * mode="object"
 * 	compile all supplied sources into object files
 * mode="executable"
 * 	compile all supplied sources into object files and call the linker for building the executable

<D
  	type        = "dmd|gdb"
	mode        = "objects|executable|library|no-compile"
	compilerdir = ""
  	header      = "true|false"
  	headerdir   = "~/hdrdir"
  	headername  = "filename.di" 
  	ddoc        = "true|false"
  	ddocdir     = "~/ddoc"
  	ddocname    = "filename.html"
  	debuginfo   = "true|false"
  	debuginfo_c = "true|false"
  	optimize    = "true|false"
  	profile     = "true|false"
  	quiet       = "true|false"
  	release     = "true|false"
  	unittest    = "true|false"
  	verbose     = "true|false"
  	warnings    = "true|false"
  	cleanup     = "true|false"
	>
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
	<linkflag   ... >
</D>
 
 */

public class D extends Task {

	LinkedList<File> mMainModules     = new LinkedList<File>();
	LinkedList<File> mIncludedModules = new LinkedList<File>();
	LinkedList<File> mIncludePaths    = new LinkedList<File>();

	HashSet<String> mCompileFiles = new HashSet<String>();
	HashSet<String> mCompileFqns  = new HashSet<String>();

	enum Mode {
		NOTHING,OBJECTS,EXECUTABLE,LIBRARY_STATIC,LIBRARY_DYNAMIC;
	}
	
	String type;
	public void setType( String type ){
		this.type = type;
	}
	private Mode mode = Mode.NOTHING;
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
	private String compilerdir;
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
	private boolean debuginfo = false;
	public void setDebuginfo( boolean value ){
		this.debuginfo = value;
	}
	private boolean debuginfo_c = false;
	public void setDebuginfo_c( boolean value ){
		this.debuginfo_c = value;
	}
	private boolean cleanup = true;
	public void setCleanup( boolean value ){
		this.cleanup = value;
	}
	private boolean warnings = false;
	public void setWarnings( boolean value ){
		this.warnings = value;
	}
	private File destfile;
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
		try{
		Compiler compiler = null;
		if( type == null ){
			throw new BuildException("type is not set");
		}
		else if( type.equals("gdc")){
			compiler = new Gdc();
		}
		else if( type.equals("dmd-linux")){
			compiler = new DmdLinux();
		}
		else if( type.equals("dmd-windows")){
			compiler = new DmdWindows();
		}
		else{
			throw new BuildException("type must be one of dmd/gdc");
		}
		
		System.out.format("D Task: compile     = %s\n", compile     );
		System.out.format("D Task: compilerdir = %s\n", compilerdir );
		System.out.format("D Task: mode        = %s\n", mode.toString() );
		System.out.format("D Task: header      = %s\n", header      );
		System.out.format("D Task: ddoc        = %s\n", ddoc        );
		System.out.format("D Task: debuginfo   = %s\n", debuginfo   );
		System.out.format("D Task: debuginfo_c = %s\n", debuginfo_c );
		System.out.format("D Task: destfile    = %s\n", destfile    );
		System.out.format("D Task: cleanup     = %s\n", cleanup     );
		System.out.format("D Task: warnings    = %s\n", warnings    );
		
		
		for( MainModules mainModules : mainModuless ){
			for( FileSet fileset : mainModules.mods ){
				DirectoryScanner scanner = fileset.getDirectoryScanner( getProject() );
				scanner.scan();
				String[] names = scanner.getIncludedFiles();
				for( String name : names ){
					File srcFile = new File( scanner.getBasedir(), name );
					mMainModules.add(srcFile );
					System.out.format("D Task: main module = %s\n", srcFile.getAbsolutePath() );
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
					System.out.format("D Task: includepath = %s\n", srcFile.getAbsolutePath() );
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
					System.out.format("D Task: includepath = %s\n", srcFile.getAbsolutePath() );
					if( !srcFile.isDirectory()){
						throw new BuildException( String.format( 
								"The include path %s is not an existing directory", srcFile.getAbsolutePath() ));
					}
				}
			}
		}

		for( LinkFlag flag : linkflags ){
			System.out.format("D Task: linkflag    = %s\n", flag.value );
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
			Execute exe = new Execute(new PumpStreamHandler(ostr));
			exe.setAntRun(getProject());
			exe.setCommandline(cmdparts);
			int retval = exe.execute();
			if (Execute.isFailure(retval)) {
				ostr.close();
				BufferedReader rd = new BufferedReader( new FileReader( t ));
				String line;
				while((line=rd.readLine()) != null ){
					System.err.println(line);
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
	
	abstract class Compiler{
		public abstract void calcDependencies();
		public abstract void build();
		public abstract void cleanup();
	}
	
	abstract class Dmd extends Compiler{
		public void getDSourceTree( File modulesFile ) throws IOException {
			
			assert( mMainModules.size() > 0 );

			BufferedReader reader = new BufferedReader( new FileReader( modulesFile ));
			String line = null;
			Pattern pattern = Pattern.compile("^import +([a-zA-Z0-9_.]+)$");
			
			for( File file : mMainModules ){
				mCompileFiles.add( file.getAbsolutePath() );
			}
			
			while( (line=reader.readLine()) != null ){
				Matcher matcher = pattern.matcher(line);
				if( !matcher.matches() ){
					continue;
				}
				String moduleName = matcher.group(1);
				if( moduleName.equals("object")){
					continue;
				}
				System.out.println( "dep mod : "+moduleName );
				
				boolean found = false;
				found = findModule(mIncludedModules, moduleName, found);
				found = findModule(mIncludePaths   , moduleName, found);
				if( !found ){
					throw new BuildException( String.format( "Module %s not found.", moduleName ));
				}
			}
		}

		private boolean findModule(LinkedList<File> incpath, String moduleName, boolean found) {
			for( File root : incpath ){
				
				File hdrFile = testModuleExist(root, moduleName, "di" );
				if( hdrFile != null ){
					found = true;
				}
				
				File modFile = testModuleExist(root, moduleName, "d" );
				if( modFile == null ){
					continue;
				}
				if( mCompileFqns.contains( moduleName) ){
					throw new BuildException( String.format( "Module %s conflicts with another module.", moduleName ));
				}
				mCompileFqns.add( moduleName );
				if( mCompileFiles.contains( moduleName) ){
					// can also occur if more than one main modules are used. Another main module is listed in the deps.
					throw new BuildException( String.format( "Module %s conflicts with another module.", moduleName ));
				}
				mCompileFiles.add( modFile.getAbsolutePath() );
				found = true;
			}
			return found;
		}
		
		private File testModuleExist( File root, String moduleName, String extension ){
			String relModPath = moduleName.replace('.', File.separatorChar );
			File srcFile = new File(root, relModPath + "." + extension );
			if( srcFile.canRead() && srcFile.isFile() ){
				return srcFile;
			}
			return null;
		}

	}
	
	class DmdWindows extends Dmd{
		public void build() {
			throw new BuildException( "DMD for windows not yet implemented" );
		}
		public void calcDependencies() {
		}
		public void cleanup() {
		}
	}
	class DmdLinux extends Dmd{
	
		LinkedList<String> temporaryFiles = new LinkedList<String>();
		LinkedList<String> objFiles = new LinkedList<String>();
		
		public void build() {
			switch( mode ){
			case OBJECTS:
				compile();
				break;
			case EXECUTABLE:
				compile();
				linkExecutable();
				break;
			case LIBRARY_STATIC:
				compile();
				linkStaticLibrary();
				break;
			case LIBRARY_DYNAMIC:
				compile();
				linkDynamicLibrary();
				break;
			default:
			}
		}

		public void calcDependencies(){
			LinkedList<String> cmdline = new LinkedList<String>();
			cmdline.add( getCompiler() );
			cmdline.add( "-c" );
			cmdline.add( "-o-" );
			cmdline.add( "-v" );
			
			for( Debug flag : debugflags ){
				if( flag.value.length() == 0 ){
					cmdline.add( "-debug" );
				}
				else{
					cmdline.add( "-debug="+ flag.value );
				}
			}
			
			for( Version flag : versionflags ){
				cmdline.add( "-version="+ flag.value );
			}
			for( File inc : mIncludePaths ){
				cmdline.add( "-I"+ inc.getAbsolutePath() );
				System.out.println( "-I"+ inc.getAbsolutePath());
			}
			for( File inc : mIncludedModules ){
				cmdline.add( "-I"+ inc.getAbsolutePath() );
				System.out.println( "-I"+ inc.getAbsolutePath());
			}
			for( File f : mMainModules ){
				cmdline.add( f.getAbsolutePath() );
			}
			
			System.out.println( "Calling compiler to get dependencies: " );

			File output = executeCmdCreateOutputFile( cmdline );
			
			try {
				getDSourceTree(output );
			} catch (IOException e) {
				throw new BuildException( "BUDs modules file cannot be read." );
			} finally {
				output.delete();
			}
			
			
			for( String mod : mCompileFqns ){
				System.out.println( " compile module: " + mod );
			}
		}

		private String getCompiler(){
			File compiler = new File( compilerdir, "dmd/bin/dmd" );
			if( !compiler.isFile() ){
				throw new BuildException( String.format( "Compiler %s is not an existing file", compiler.getAbsolutePath() ));
			}
			return compiler.getAbsolutePath();
		}
		private void compile() {
			LinkedList<String> cmdline = new LinkedList<String>();
			cmdline.add( getCompiler() );
			cmdline.add( "-c" );
			cmdline.add( "-op" );
			if( debuginfo ){
				cmdline.add( "-g" );
			}
			if( debuginfo_c ){
				cmdline.add( "-gc" );
			}
			if( warnings ){
				cmdline.add( "-w" );
			}
			
			for( Debug flag : debugflags ){
				if( flag.value.length() == 0 ){
					cmdline.add( "-debug" );
				}
				else{
					cmdline.add( "-debug="+ flag.value );
				}
			}
			for( Version flag : versionflags ){
				cmdline.add( "-version="+ flag.value );
			}
			for( File inc : mIncludePaths ){
				cmdline.add( "-I"+ inc.getAbsolutePath() );
				System.out.println( "-I"+ inc.getAbsolutePath());
			}
			for( File inc : mIncludedModules ){
				cmdline.add( "-I"+ inc.getAbsolutePath() );
				System.out.println( "-I"+ inc.getAbsolutePath());
			}
			for( String mod : mCompileFiles ){
				cmdline.add( mod );
				System.out.println( "compile : "+mod );
				String srcName = mod;
				String objName = srcName.substring(0, srcName.length()-1 ) + "o";
				objFiles.add( objName );
				temporaryFiles.add( objName );
			}
			
			StringBuilder sb = new StringBuilder();
			for( String s : cmdline ){
				sb.append( " " + s );
			}
			System.out.format( "Will call compiler with parm count: %d and length %d\n", cmdline.size(), sb.length() );
			
			System.out.println( "Calling compiler: " );
			Execute.runCommand(D.this, cmdline.toArray(new String[0]));
			
			
		}
		
		private void linkExecutable() {
			LinkedList<String> cmdline = new LinkedList<String>();
			cmdline.add( "gcc" );
			for( String objName : objFiles ){
				cmdline.add( objName );
			}
			if( debuginfo ){
				cmdline.add( "-g" );
			}

			cmdline.add( "-o" + destfile.getAbsolutePath() );
			
			for( LinkFlag flag : linkflags ){
				cmdline.add( "" + flag.value );
			}

			System.out.println( "Calling linker: " );
			Execute.runCommand(D.this, cmdline.toArray(new String[0]));
		}

		private void linkStaticLibrary() {
			LinkedList<String> cmdline = new LinkedList<String>();
		}

		private void linkDynamicLibrary() {
			LinkedList<String> cmdline = new LinkedList<String>();
		}

		public void cleanup() {
			for( String fileName : temporaryFiles ){
				File tempFile = new File( fileName );
				if( !tempFile.exists() ){
					continue;
				}
				if( !tempFile.delete() ){
					System.out.format( "cannot delete the temporary file %s\n", fileName );
				}
			}			
		}

	}
	class Gdc extends Compiler{
		public void build() {
			throw new BuildException( "GDC not yet implemented" );
		}
		public void calcDependencies() {
		}

		public void cleanup() {
		}
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
