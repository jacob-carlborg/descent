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

package descent.ant.tasks;

import java.io.File;
import java.io.FileNotFoundException;
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


class DmdWindows extends Dmd{

		DmdWindows(D d) {
			super(d);
		}

		LinkedList<String> objFiles = new LinkedList<String>();
		LinkedList<String> temporaryFiles = new LinkedList<String>();
		
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
			
			for( Version flag : dTask.versionflags ){
				cmdline.add( "-version="+ flag.value );
			}
			for( File inc : dTask.mIncludePaths ){
				cmdline.add( "-I"+ inc.getAbsolutePath() );
			}
			for( File inc : dTask.mIncludedModules ){
				cmdline.add( "-I"+ inc.getAbsolutePath() );
			}
			for( File f : dTask.mMainModules ){				
				cmdline.add( f.getAbsolutePath() );
			}
			StringBuilder sb = new StringBuilder();
			for( String s : cmdline ){
				sb.append( " " + s );
			}
			
			dTask.log( "Calling compiler to get dependencies: " + sb.toString() , Project.MSG_INFO );

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
			File compiler = new File( dTask.compilerdir, "dmd\\bin\\dmd.exe" );
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
				cmdline.add( "\"" + mod + "\"");
				//System.out.println( "compile : "+mod );
				String srcName = mod;
				String objName = srcName.substring(0, srcName.length()-1 ) + "obj";
				try {
					objFiles.add( WindowsFilenameConverter.checkForSpaces(objName ) );
				} catch (FileNotFoundException e) {
					throw new BuildException( String.format( "Cannot find compile file : %s", mod ));
				}
				temporaryFiles.add(objName);
			}
		
			
			
			dTask.log(  "Calling compiler " + cmdline.toString(), Project.MSG_INFO );
			Execute.runCommand(dTask, cmdline.toArray(new String[0]));
			
			
		}
		
		private void linkExecutable() {
			
			if ( dTask.destfile == null ) throw new BuildException("You must specify a target name for your executable via 'destfile' property");
			
			LinkedList<String> cmdline = new LinkedList<String>();
			cmdline.add( dTask.compilerdir + "dm\\bin\\link.exe");
			
			StringBuilder objectBuilder = new StringBuilder();
			
			for( String objName : objFiles ){
				objectBuilder.append( objName + "+");
			
			}
			
//			 strip off the last '+'
			if ( objectBuilder.charAt(objectBuilder.length() - 1 ) == '+'){
				objectBuilder.delete(objectBuilder.length() - 1	, objectBuilder.length());
			}
			
			if ( dTask.destfile != null )	{
				try {
					objectBuilder.append("," + WindowsFilenameConverter.checkForSpaces(dTask.destfile.getParentFile().getAbsolutePath() ) + File.separator + dTask.destfile.getName() );
				} catch (FileNotFoundException e) {
					throw new BuildException( "Cannot find the directory for the destfile" );
				}
			}
			else {
				objectBuilder.append(",");
			}
			
			if ( dTask.mapfile != null ) {
				objectBuilder.append("," + dTask.mapfile );
			}
			else {
				objectBuilder.append(",");
			}
			
			objectBuilder.append(",user32+kernel32+");
			
			for (LinkLib lib : dTask.linkLibs) {
				objectBuilder.append(lib.name + "+");
			}
			
//			 strip off the last '+'
			if ( objectBuilder.charAt(objectBuilder.length() - 1 ) == '+') {
				objectBuilder.delete(objectBuilder.length() - 1	, objectBuilder.length());
			}
			
			objectBuilder.append("/noi;");
			
			if ( dTask.deffile != null ) {
				objectBuilder.append("," + dTask.deffile );
				
			}
			else objectBuilder.append(",");
			
			if ( dTask.resfile != null) {
				objectBuilder.append("," + dTask.resfile);
			}
			else {
				objectBuilder.append(",");
			}
			
			if ( dTask.linkflags.size() > 0 ) {
				objectBuilder.append("/");
			}
		
			for( LinkFlag flag : dTask.linkflags ){
				objectBuilder.append(flag.value + ";");
			}

			// strip off the last ';'
			
			if ( objectBuilder.charAt(objectBuilder.length() - 1 ) == ';') {
				objectBuilder.delete(objectBuilder.length() - 1	, objectBuilder.length());
			}
					
			cmdline.add(objectBuilder.toString());
			
			
			StringBuilder sb = new StringBuilder();
			for( String s : cmdline ){
				sb.append( " " + s );
			}
			String libraryPath = dTask.compilerdir + "dmd\\lib;" + dTask.compilerdir + "dm\\lib";
			
			dTask.log( String.format("Setting Library path to : %s", libraryPath) , Project.MSG_INFO );
			String[] libOpts = new String[1];
			libOpts[0] = "LIB=" + libraryPath ;
 			
			Execute exec = new Execute();
			exec.setEnvironment(libOpts);
			
			dTask.log( "Calling linker " + cmdline.toString() , Project.MSG_INFO );
			exec.setCommandline(cmdline.toArray(new String[0]));
			try {
				exec.execute();
			} catch (IOException e) {
				throw new BuildException( "Linker can't be started" );
			}
			dTask.log( String.format("Built %s", dTask.destfile.getAbsolutePath()), Project.MSG_INFO );
		}

		private void linkStaticLibrary() {
			
			if ( dTask.destfile == null ) throw new BuildException("You must specify a target name for your library via 'destfile' property");
			
			LinkedList<String> cmdline = new LinkedList<String>();
			cmdline.add( dTask.compilerdir + "dm\\bin\\lib.exe");
			cmdline.add("-c");
			cmdline.add(dTask.destfile.getName());
			
			for( LinkFlag flag : dTask.linkflags ){
				cmdline.add(flag.value);
			}
		
			for( String objName : objFiles ){
				cmdline.add( objName );
			}
			dTask.log( "Calling lib " + cmdline.toString(), Project.MSG_INFO );
			
			Execute.runCommand(dTask, cmdline.toArray(new String[0]));
			
			dTask.log( String.format("Built %s", dTask.destfile.getAbsolutePath()), Project.MSG_INFO );
			
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