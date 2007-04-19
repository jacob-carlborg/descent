package descent.internal.core.builder;

import java.util.*;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;

import descent.internal.core.util.Util;

public class DAntBuilder extends IncrementalProjectBuilder {

	public DAntBuilder() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		if (kind == INCREMENTAL_BUILD || kind == FULL_BUILD) { // we skip AUTO_BUILDS

			Util.log(null, "Starting compilation ");
			
			clean(monitor);
			
			
			IProject project = getProject();
			DResourceVisitor visitor;

			if (true ) { //!project.getFile("build.xml").exists()) {
				visitor = new DResourceVisitor();
				
				project.accept(visitor);
				
				
				DAntFileCreator dant = new DAntFileCreator(visitor.projectFiles);
				
				Util.log(null, dant.create(project));
				
				
				
				
			}
			
			

		}
		
		return null;
	}

}

class DResourceVisitor implements IResourceVisitor {
	public ArrayList<IPath> projectFiles = new ArrayList<IPath>();

	public boolean visit(IResource resource) throws CoreException {
		// TODO Auto-generated method stub

		Util.log(null, resource.getFullPath().toString());
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
		String macrodef = "<macrodef name=\"compile\" >";
		macrodef += "<attribute name=\"bldtrg\" />";
		macrodef += "<sequential>";
		macrodef += "<D ";
		macrodef += "type        = \"${compiler.type}\"";
		macrodef += "mode        = \"executable\"";
		macrodef += "compilerdir = \"${compiler.dir}\"";
		macrodef += "destfile    = \"${project.dir}/@{bldtrg}.exe\"";
		macrodef += "cleanup     = \"true\"";
		macrodef += ">";
		macrodef += "<!--				<version value=\"Posix\" /> -->";
		macrodef += "";
		macrodef += "<!-- The main modules -->";
		macrodef += "<mainmodules>";
		macrodef += "<fileset file=\"${project.dir}/@{bldtrg}.d\"/>";
		macrodef += "</mainmodules>";
		macrodef += "";
		macrodef += "<!-- Modules for compilation and linking, if imported -->";
		macrodef += "<includemodules>";
		macrodef += "<dirset file=\"${project.dir}\" />";
		macrodef += "<dirset file=\"${tango.dir}\" />";
		macrodef += "</includemodules>";
		macrodef += "";
		macrodef += "<!-- Imported modules, only for declarations, no compile/link (libs) -->";
		macrodef += "<includepath>";
		macrodef += "</includepath>";
		macrodef += "</D>";
		macrodef += "</sequential>";
		macrodef += "</macrodef>";

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
		

		antText += "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
		antText += "<project name=\"" + project.getName()
				+ "\" default=\"compile\" basedir=\".\">";
		antText += "<taskdef classname=\"anttasks.D\" name=\"D\" />";
		antText += "<taskdef classname=\"anttasks.DModuleBuildNumber\" name=\"DBldNum\" />";
		antText += "<taskdef classname=\"anttasks.DModuleBuildNumber\" name=\"foreach\" />";
		antText += "<property name=\"compiler.dir\" value=\"C:\\\" />";
		antText += "<property name=\"compiler.type\" value=\""
				+ compilerType + "\"/>";

		antText += "<property name=\"project.dir\" value=\""
				+ project.getFullPath().toOSString() + "\" />";
		antText += "<property name=\"tango.dir\" value=\"C:\\path\\to\\tango\"";
		
		antText += macrodef;
		
		
			

		return antText;
	}

}
