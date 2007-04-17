/**
 * Ant Task for building D programs.
 *
 * Authors:
 *  Frank Benoit (benoit at tionex dot de)
 *
 * License:
 *  Public Domain
 */

package anttasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.BuildException;

abstract class Dmd extends Compiler{
	
	static final String DMD_VERBOSE_ERRORFILTER = "^(import|semantic\\d?|parse)\\s+.*";

	/**
	 * @param d
	 */
	Dmd(D d) {
		super(d);
	}

	public void getDSourceTree( File modulesFile ) throws IOException {
		
		assert( dTask.mMainModules.size() > 0 );

		BufferedReader reader = new BufferedReader( new FileReader( modulesFile ));
		String line = null;
		//TODO - optimize this
		Pattern pattern = Pattern.compile("^import\\s+(.*?)\\s+\\((.*?)\\)");
		
		for( File file : dTask.mMainModules ){
			dTask.mCompileFiles.add( file.getAbsolutePath() );
		}
		
		while( (line=reader.readLine()) != null ){
			Matcher matcher = pattern.matcher(line);
			if( !matcher.matches() ){
				continue;
			}
			String moduleName = matcher.group(2);
			if( moduleName.endsWith(".di")){
				continue;
			}
			System.out.println( "dep mod : "+moduleName );
			
			dTask.mCompileFiles.add(moduleName);
			//boolean found = false;
			//found = findModule(dTask.mIncludedModules, moduleName, found);
			//found = findModule(dTask.mIncludePaths   , moduleName, found);
			//if( !found ){
			//	throw new BuildException( String.format( "Module %s not found.", moduleName ));
			//
			//}
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
			if( dTask.mCompileFqns.contains( moduleName) ){
				throw new BuildException( String.format( "Module %s conflicts with another module.", moduleName ));
			}
			dTask.mCompileFqns.add( moduleName );
			if( dTask.mCompileFiles.contains( moduleName) ){
				// can also occur if more than one main modules are used. Another main module is listed in the deps.
				throw new BuildException( String.format( "Module %s conflicts with another module.", moduleName ));
			}
			dTask.mCompileFiles.add( modFile.getAbsolutePath() );
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