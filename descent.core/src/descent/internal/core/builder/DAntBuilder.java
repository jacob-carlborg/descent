package descent.internal.core.builder;


import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Map;

import org.apache.tools.ant.Project;
import org.eclipse.ant.core.AntRunner;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
public class DAntBuilder extends IncrementalProjectBuilder {

    public DAntBuilder() {
	// TODO Auto-generated constructor stub
    }


    
    private IFile createBuildFile(IProgressMonitor monitor)
    {
	    IProject project = getProject();
	    DResourceVisitor visitor;
	    // for now just delete the file and create
	    
				
		IFile buildFile = project.getFile("build.xml");
				
		try {
			buildFile.delete(true, monitor);
			visitor = new DResourceVisitor();
			
			project.accept(visitor);
					
					
			DAntFileCreator dant = new DAntFileCreator(visitor.projectFiles);
					
			String buildXml = dant.create(project);
			//Util.log(null, buildXml);
						
			buildFile.create(new ByteArrayInputStream(buildXml.getBytes() ) , true, monitor );
			buildFile.touch(monitor);
		}

		 catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return buildFile;
    	
	    
    }
    //	 
    
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
	throws CoreException {
		
		
	if (kind == INCREMENTAL_BUILD || kind == FULL_BUILD) { // we skip AUTO_BUILDS

	    //Util.log(null, "Starting compilation ");
			
	    clean(monitor);
		AntRunner runner = new AntRunner();
		IFile buildFile = createBuildFile(monitor);
		runner.setBuildFileLocation(buildFile.getLocation().toOSString());
		runner.setArguments("-verbose");
		//runner.addBuildListener("DAntBuildListener");
		runner.addBuildLogger("org.apache.tools.ant.DefaultLogger");		
		runner.setMessageOutputLevel(Project.MSG_DEBUG );
		runner.run(monitor);
		
	}
		
	    /*
			
	    IFile buildFile = createBuildFile(monitor);
	    Util.log(null, buildFile.getLocation().toOSString() );
		
		Project antProject = new Project();
		antProject.setUserProperty("ant.file",buildFile.getLocation().toOSString() );
		antProject.init();
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		antProject.addReference("ant.projecthelper", helper);
		//helper.parse(antProject,buildFile.); 
		//				
				
				
		DefaultLogger log = new DefaultLogger();
		MessageConsole mc = findConsole(IConsoleConstants.ID_CONSOLE_VIEW);
		MessageConsoleStream ms = new MessageConsoleStream(mc); 
		PrintStream ps = new PrintStream ( ms );
		log.setOutputPrintStream( System.out);
		log.setErrorPrintStream(System.err);
		antProject.addBuildListener(log);
				
		antProject.executeTarget("compileProject");
				
	    }
			
			
	    forgetLastBuiltState();
	    */
	
		
	return null;
    }

}

class DResourceVisitor implements IResourceVisitor {
    public ArrayList<IPath> projectFiles = new ArrayList<IPath>();

    public boolean visit(IResource resource) throws CoreException {
	// TODO Auto-generated method stub
	if ( resource.getType() == IResource.FILE)
	    {
			
		if ( resource.getFileExtension().toLowerCase().equals("d"))
		    {
			projectFiles.add(resource.getFullPath());			
		    }
	    }
	return true;
    }

}
// TODO , search path for location of dmd
class DAntFileCreator {

    public static String antWindowsHeader = "";

    public static String antLinuxHeader = "";

    ArrayList<IPath> files;

    public DAntFileCreator(ArrayList<IPath> files) {
	this.files = files;

    }

    public String create(IProject project) {
		
	String antText = "";
	String compilerType = "";
		
	boolean isWindows = true;
		
	if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") == -1) {
	    isWindows = false;
	}

	if (isWindows) {
	    compilerType = "dmd-windows";
	}
	else
	    {
		compilerType = "dmd-linux";
	    }
		
		


		

	antText += "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";
	antText += "<project name=\"" + project.getName()
	    + "\" default=\"compileProject\" basedir=\".\">\n";
	antText += "<taskdef classname=\"descent.ant.tasks.D\" name=\"D\" />\n\n";
	antText += "<taskdef classname=\"descent.ant.tasks.DModuleBuildNumber\" name=\"DBldNum\" />\n";
	antText += "<taskdef classname=\"descent.ant.tasks.DModuleBuildNumber\" name=\"foreach\" />\n\n";
	antText += "<property name=\"compiler.dir\" value=\"C:\\\" />\n";
	antText += "<property name=\"compiler.type\" value=\""
	    + compilerType + "\"/>\n";

	antText += "<property name=\"project.dir\" value=\""
	    + project.getLocation().removeLastSegments(1) + "\" />\n";
	antText += "<!-- <property name=\"tango.dir\" value=\"C:\\path\\to\\tango\"/> --> \n\n\n";
		
	String macrodef = "<macrodef name=\"compile\" >\n";		
	macrodef += "<sequential>\n";
  	macrodef += "<!--   <D \n";
  	macrodef += "       type        = \"dmd-linux|dmd-windows|gdb\"\n";
	macrodef += "  		mode        = \"objects|executable|library-static|library-dynamic\"\n";
	macrodef += "  		compilerdir = \"path\"\n";
	macrodef += "  	  	header      = \"true|false*\"\n";
	macrodef += "  	  	headerdir   = \"~/hdrdir\"\n";
	macrodef += "  	  	headername  = \"filename.di\" \n";
	macrodef += "  	  	ddoc        = \"true|false\"\n";
	macrodef += "  	  	ddocdir     = \"~/ddoc\"\n";
	macrodef += "  	  	ddocname    = \"filename.html\"\n";
	macrodef += "  	  	debuginfo   = \"true|false*\"\n";
	macrodef += "  	  	debuginfo_c = \"true|false*\"\n";
	macrodef += "  	  	optimize    = \"true|false*\"\n";
	macrodef += "  	  	profile     = \"true|false*\"\n";
	macrodef += "  	  	quiet       = \"true|false*\"\n";
	macrodef += "  	  	release     = \"true|false*\"\n";
	macrodef += "  	  	unittest    = \"true|false*\"\n";
	macrodef += "  	  	verbose     = \"true|false*\"\n";
	macrodef += "  	  	warnings    = \"true|false*\"\n";
	macrodef += "  	  	cleanup     = \"true*|false\"\n";
	macrodef += "  	  	stdargs     = \"true*|false\"\n";
	macrodef += "  	  	unittest    = \"true|false*\"\n";
	macrodef += "  	  	mapfile     = \"file.map\"\n";
	macrodef += "  	  	deffile     = \"file.def\"\n";
	macrodef += "  	  	resfile     = \"file.res\"\n";
	macrodef += "  	  	destfile    = \"target_file_name.exe\"\n -->";
	macrodef += "<D \n";
	macrodef += "type        = \"${compiler.type}\"\n";
	macrodef += "mode        = \"executable\"\n";
	macrodef += "compilerdir = \"${compiler.dir}\"\n";
	macrodef += "destfile    = \"${project.dir}/" + project.getName() + "/" + project.getName() + ".exe\"\n";
	macrodef += "cleanup     = \"true\"\n";

	macrodef += ">\n";
	
	
	macrodef += "<!-- <debug   value=\"1\"/> -->\n\n ";
	macrodef += "<!-- list flags for the linker\n";
	macrodef += "<linkflag value=\"-L/usr/lib\" /> -->\n\n";
	
	macrodef += "<!-- list the libs, the linker shall link \n";
	macrodef += "<linklib type=\"static|dynamic\" name=\"name\" /> -->\n\n";
	
	if ( !isWindows ) macrodef += "<version value=\"Posix\" />\n";
	else macrodef += "<!-- <version value=\"YourVersion\"/> --> \n";
	
	macrodef += "\n";
	macrodef += "<!-- The main modules -->\n";
	macrodef += "<mainmodules>\n";
	for ( IPath path : files )
	    {
		macrodef += "<fileset file=\"${project.dir}" + path.toOSString()+ "\"/>\n";
	    }
	macrodef += "</mainmodules>\n";
	// TODO charles, the following two lines gives an error saying that this tag is not allowed here,
	// please check it
	macrodef += "\n\n<!-- Any packages you want to exclude -->";
	macrodef += "<excludePackage value=\"phobos\" />\n\n\n";  
	macrodef += "<!-- Modules for compilation and linking, if imported -->\n";
	macrodef += "<includemodules>\n";
	macrodef += "<dirset file=\"${project.dir}\" />\n";
	macrodef += "<dirset file=\"${project.dir}/" + project.getName() + "\" />\n";
	macrodef += "<dirset file=\"${tango.dir}\" />\n";
	macrodef += "</includemodules>\n";
	macrodef += "\n";
	macrodef += "<!-- Imported modules, only for declarations, no compile/link (libs) -->\n";
	macrodef += "<includepath>\n";
	macrodef += "</includepath>\n";
	macrodef += "</D>\n";
	macrodef += "</sequential>\n";
	macrodef += "</macrodef>\n\n\n";
		
	antText += macrodef;
		
		
	antText += "\n\n<target name=\"compileProject\" >\n";
	antText += "<compile/>\n";
	antText += "</target>\n";
	antText += "</project>\n";
	
	

	
	

	return antText;
    }

}
