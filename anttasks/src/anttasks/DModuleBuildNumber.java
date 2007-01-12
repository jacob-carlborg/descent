/**
 * Ant Task for build number constant in D module
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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

public class DModuleBuildNumber extends Task {

	File file;
	public void setFile( File file ){
		this.file = file;
	}

	String fqn;
	public void setFqn( String value ){
		this.fqn = value;
	}

	
	@Override
	public void execute() throws BuildException {
		if( file == null ){
			throw new BuildException( "the attribute file is not set" );
		}
		if( fqn == null ){
			throw new BuildException( "the attribute fqn is not set" );
		}
		
		int version = readVersion();
		version++;
		writeVersion( version );
		log( String.format( "Buildnumber %d written to module %s", version, fqn ), Project.MSG_INFO );
	}

	private int readVersion() {
		try{ 
			if( !file.canRead() ){
				return 0;
			}
			BufferedReader rd = new BufferedReader( new FileReader( file ));
			String firstLine = rd.readLine();
			if( firstLine == null ){
				return 0;
			}
			Pattern pattern = Pattern.compile( String.format("%s\\((\\d+)\\)",FIRSTLINECOMMENT) );
			Matcher matcher = pattern.matcher(firstLine);
			if( !matcher.matches() ){
				throw new BuildException( String.format( "Buildnumber file %s seems not to contain valid data", file.getAbsolutePath() ));
			}
			return Integer.parseInt(matcher.group(1));
		}
		catch( FileNotFoundException e ){
			return 0;
		} catch (IOException e) {
			throw new BuildException( String.format( "Buildnumber file %s exists bug IO-Exceptions occured while reading it.", file.getAbsolutePath() ));
		}
	}
	final String FIRSTLINECOMMENT = "// D build number module ";
	private void writeVersion(int version) {
		try {
			PrintWriter wr = new PrintWriter( new FileWriter( file ));
			wr.printf( "%s(%d)\n", FIRSTLINECOMMENT, version );
			wr.println();
			wr.printf( "module %s;\n", fqn );
			wr.println();
			wr.printf( "public const buildnumber = %d;\n", version );
			wr.println();
			wr.println();
			wr.flush();
			wr.close();
		} catch (IOException e) {
			throw new BuildException( "cannot write buildnumber file " + file.getAbsolutePath() );
		}
	}


}




