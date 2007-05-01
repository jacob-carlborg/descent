/**
 * Ant Task for building D programs.
 *
 * Authors:
 *  Frank Benoit (benoit at tionex dot de)
 *
 * License:
 *  Public Domain
 */
package descent.ant.tasks;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.regex.Pattern;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;

import descent.ant.tasks.D.Debug;
import descent.ant.tasks.D.LinkFlag;
import descent.ant.tasks.D.LinkLib;
import descent.ant.tasks.D.Version;


class DmdLinux extends Dmd{

	DmdLinux(D d) {
		super(d);
	}

	LinkedList<String> temporaryFiles = new LinkedList<String>();
	LinkedList<String> objFiles = new LinkedList<String>();
	
	public void build() {
		switch( dTask.mode ){
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
		
		for( Debug flag : dTask.debugflags ){
			if( flag.value.length() == 0 ){
				cmdline.add( "-debug" );
			}
			else{
				cmdline.add( "-debug="+ flag.value );
			}
		}
		if( dTask.unittest ){
			cmdline.add( "-unittest" );
		}
		for( Version flag : dTask.versionflags ){
			cmdline.add( "-version="+ flag.value );
		}
		for( File inc : dTask.mIncludePaths ){
			cmdline.add( "-I"+ inc.getAbsolutePath() );
			//System.out.println( "-I"+ inc.getAbsolutePath());
		}
		for( File inc : dTask.mIncludedModules ){
			cmdline.add( "-I"+ inc.getAbsolutePath() );
			//System.out.println( "-I"+ inc.getAbsolutePath());
		}
		for( File f : dTask.mMainModules ){
			cmdline.add( f.getAbsolutePath() );
		}
		
		dTask.log( "Calling compiler to get dependencies: ", Project.MSG_INFO );

		File output = dTask.executeCmdCreateOutputFile( cmdline, Pattern.compile(DMD_VERBOSE_ERRORFILTER) );
		
		try {
			getDSourceTree(output );
		} catch (IOException e) {
			throw new BuildException( "BUDs modules file cannot be read." );
		} finally {
			output.delete();
		}			
	}

	private String getCompiler(){
		File compiler = new File( dTask.compilerdir, "dmd/bin/dmd" );
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
		if( dTask.debuginfo ){
			cmdline.add( "-g" );
		}
		if( dTask.debuginfo_c ){
			cmdline.add( "-gc" );
		}
		if( dTask.warnings ){
			cmdline.add( "-w" );
		}
		if( dTask.unittest ){
			cmdline.add( "-unittest" );
		}

		for( Debug flag : dTask.debugflags ){
			if( flag.value.length() == 0 ){
				cmdline.add( "-debug" );
			}
			else{
				cmdline.add( "-debug="+ flag.value );
			}
		}
		for( Version flag : dTask.versionflags ){
			cmdline.add( "-version="+ flag.value );
		}
		for( File inc : dTask.mIncludePaths ){
			cmdline.add( "-I"+ inc.getAbsolutePath() );
			//System.out.println( "-I"+ inc.getAbsolutePath());
		}
		for( File inc : dTask.mIncludedModules ){
			cmdline.add( "-I"+ inc.getAbsolutePath() );
			//System.out.println( "-I"+ inc.getAbsolutePath());
		}
		for( String mod : dTask.mCompileFiles ){
			cmdline.add( mod );
			//System.out.println( "compile : "+mod );
			String srcName = mod;
			String objName = srcName.substring(0, srcName.length()-1 ) + "o";
			objFiles.add( objName );
			temporaryFiles.add( objName );
		}
		
		StringBuilder sb = new StringBuilder();
		for( String s : cmdline ){
			sb.append( " " + s );
		}
		dTask.log( String.format( "Will call compiler with parm count: %d and length %d\n", cmdline.size(), sb.length() ), Project.MSG_VERBOSE );
		
		dTask.log( String.format( "Calling compiler: " ), Project.MSG_INFO );
		Execute.runCommand(dTask, cmdline.toArray(new String[0]));
		
		
	}
	
	private void linkExecutable() {
		LinkedList<String> cmdline = new LinkedList<String>();
		cmdline.add( "gcc" );
		for( String objName : objFiles ){
			cmdline.add( objName );
		}
		if( dTask.debuginfo ){
			cmdline.add( "-g" );
		}

		cmdline.add( "-o" + dTask.destfile.getAbsolutePath() );
		
		for( LinkFlag flag : dTask.linkflags ){
			cmdline.add( "" + flag.value );
		}
		
		for( LinkLib linkLib : dTask.linkLibs ){
			// linkMode != null is already checked
			switch( linkLib.linkType ){
			case STATIC:
				cmdline.add( "" + linkLib.name );
				break;
			case DYNAMIC:
				cmdline.add( "-l" + linkLib.name );
				break;
			}
		}

		if( dTask.stdargs ){
			cmdline.add( "-lm" );
			cmdline.add( "-lphobos" );
			cmdline.add( "-lc" );
			cmdline.add( "-lpthread" );
		}

		dTask.log( "Calling linker: ", Project.MSG_INFO );
		Execute.runCommand(dTask, cmdline.toArray(new String[0]));
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
				dTask.log( String.format( "cannot delete the temporary file %s\n", fileName ), Project.MSG_WARN );
			}
		}			
	}

}