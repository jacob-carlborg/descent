/**
 * Ant Task for converting binary files into D source code.
 *
 * Authors:
 *  Frank Benoit (benoit at tionex dot de)
 *
 * License:
 *  Public Domain
 */
package descent.ant.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class BinToD extends Task {

	File file;
	public void setFile( File file ){
		this.file = file;
	}

	String fqn;
	public void setFqn( String fqn ){
		this.fqn = fqn;
	}
	
	LinkedList<Resource> resources = new LinkedList<Resource>();
	public Resource createResource(){
		Resource result = new Resource();
		resources.add( result );
		return result;
	}

	public class Resource {
		File file;
		public void setFile( File file ){
			this.file = file;
		}

		String id;
		public void setId( String id ){
			this.id = id;
		}
		
		String variable;
		public void setVariable( String variable ){
			this.variable = variable;
		}
		
		String version;
		public void setVersion( String version ){
			this.version = version;
		}
		
	}
	
	@Override
	public void execute() throws BuildException {
		
		try {
			PrintWriter   mWriter = new PrintWriter( new FileWriter( file ));

			mWriter.println("//");
			mWriter.println("// This file was generated with bintod");
			mWriter.println("//");

			mWriter.format("module %s;\n", fqn );
			mWriter.println("");
			mWriter.println("");

			// Write id to variable mapper
			mWriter.println("public ubyte[] getDataById( char[] aId ){");
			for (Resource res : resources ) {
			    mWriter.format("    version( %s ){\n", res.version);
			    mWriter.format("        if( aId == \"%s\" ) return %s;\n",res.id, res.variable);
			    mWriter.println("    }");
			}
			mWriter.println("    return null; // aId did not match");
			mWriter.println("}");
			mWriter.println("");

			// Write the data
			for( Resource res: resources) {
			    mWriter.format("version( %s ){\n", res.version);
			    mWriter.format("    public ubyte[] %s = [ cast(ubyte)", res.variable);
			    FileInputStream dataFc = new FileInputStream( res.file );
			    int c, idx = 0;
			    boolean first = true;
			    String showline = "";
			    while( (c=dataFc.read()) != -1 ){
			    	
			        if ((idx % 16) == 0) {
			        	if( !first ){
				            mWriter.print(", // ");
				            mWriter.print(showline);
				            showline = "";
			        	}
			        	first = false;
			            mWriter.print("\n        ");
			        }
			        else {
			            mWriter.print(", ");
			        }
			        mWriter.format("0x%02X", c ); //
			        if( Character.isLetterOrDigit(c) || "{}[]`~!@#$%^&*()_+=-\\|´\"\';:/?.>,<".indexOf(c) >= 0 ){
			        	showline += (char)c;
			        }
			        else {
			        	showline += ' ';
			        }
			        idx++;
			    }
			    mWriter.print("];");
			    for( ;idx%16 != 0; idx++  ){
				    mWriter.print("      ");
			    }
			    mWriter.print("// ");
	            mWriter.println(showline);
			    mWriter.println("}");
			    mWriter.println();
			    mWriter.println();
			}
			mWriter.close();
			System.out.println( "writing file " + file.getAbsolutePath() );
		} catch (FileNotFoundException e) {
			throw new BuildException( e.getMessage() );
		} catch (IOException e) {
			throw new BuildException( e.getMessage() );
		}
	}
}




