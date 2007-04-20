package descent.internal.core.builder;


import java.io.ByteArrayInputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Map;

import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import descent.internal.core.util.Util;
import org.eclipse.ui.*;
import org.eclipse.ui.console.*;

public class DAntBuilder extends IncrementalProjectBuilder {

    public DAntBuilder() {
	// TODO Auto-generated constructor stub
    }

    private MessageConsole findConsole(String name) {
	ConsolePlugin plugin = ConsolePlugin.getDefault();
	IConsoleManager conMan = plugin.getConsoleManager();
	IConsole[] existing = conMan.getConsoles();
	for (int i = 0; i < existing.length; i++)
	    if (name.equals(existing[i].getName()))
		return (MessageConsole) existing[i];
	//no console found, so create a new one
	MessageConsole myConsole = new MessageConsole(name, null);
	conMan.addConsoles(new IConsole[]{myConsole});
	return myConsole;
    }
    //	 
    @Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
	throws CoreException {
	if (kind == INCREMENTAL_BUILD || kind == FULL_BUILD) { // we skip AUTO_BUILDS

	    Util.log(null, "Starting compilation ");
			
	    clean(monitor);
			
			
	    IProject project = getProject();
	    DResourceVisitor visitor;
	    // for now just delete the file and create
	    if (true ) { //!project.getFile("build.xml").exists()) {
				
		IFile buildFile = project.getFile("build.xml");
				
		buildFile.delete(true, monitor);
		visitor = new DResourceVisitor();
				
		project.accept(visitor);
				
				
		DAntFileCreator dant = new DAntFileCreator(visitor.projectFiles);
				
		String buildXml = dant.create(project);
		Util.log(null, buildXml);
		Util.log(null, args.toString());				
		buildFile.create(new ByteArrayInputStream(buildXml.getBytes() ) , true, monitor );
		
		Project antProject = new Project();
		antProject.setUserProperty("ant.file",buildFile.getFullPath().toOSString());
		antProject.init();
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		antProject.addReference("ant.projecthelper", helper);
		//helper.parse(antProject,buildFile.); 
		//				
				
				
		DefaultLogger log = new DefaultLogger();
		MessageConsole mc = findConsole(IConsoleConstants.ID_CONSOLE_VIEW);
		MessageConsoleStream ms = new MessageConsoleStream(mc); 
		PrintStream ps = new PrintStream ( ms );
		log.setOutputPrintStream( ps);
		log.setErrorPrintStream(ps);
		antProject.addBuildListener(log);
				
		antProject.executeTarget("compileProject");
				
	    }
			
			
	    forgetLastBuiltState();
	}
		
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
	    + "\" default=\"compile\" basedir=\".\">\n";
	antText += "<taskdef classname=\"anttasks.D\" name=\"D\" />\n\n";
	antText += "<taskdef classname=\"anttasks.DModuleBuildNumber\" name=\"DBldNum\" />\n";
	antText += "<taskdef classname=\"anttasks.DModuleBuildNumber\" name=\"foreach\" />\n\n";
	antText += "<property name=\"compiler.dir\" value=\"C:\\\" />\n";
	antText += "<property name=\"compiler.type\" value=\""
	    + compilerType + "\"/>\n";

	antText += "<property name=\"project.dir\" value=\""
	    + project.getLocation().removeLastSegments(1) + "\" />\n";
	antText += "<!-- <property name=\"tango.dir\" value=\"C:\\path\\to\\tango\"/> --> \n\n\n";
		
	String macrodef = "<macrodef name=\"compile\" >\n";		
	macrodef += "<sequential>\n";
	macrodef += "<D \n";
	macrodef += "type        = \"${compiler.type}\"\n";
	macrodef += "mode        = \"executable\"\n";
	macrodef += "compilerdir = \"${compiler.dir}\"\n";
	macrodef += "destfile    = \"${project.dir}/" + project.getName() + ".exe\"\n";
	macrodef += "cleanup     = \"true\"\n";
	macrodef += ">\n";
	if ( !isWindows ) macrodef += "<version value=\"Posix\" />\n";
	macrodef += "\n";
	macrodef += "<!-- The main modules -->\n";
	macrodef += "<mainmodules>\n";
	for ( IPath path : files )
	    {
		macrodef += "<fileset file=\"${project.dir}" + path.toOSString() + "\"/>\n";
	    }
	macrodef += "</mainmodules>\n";
	macrodef += "\n";
	macrodef += "<!-- Modules for compilation and linking, if imported -->\n";
	macrodef += "<includemodules>\n";
	macrodef += "<dirset file=\"${project.dir}\" />\n";
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
