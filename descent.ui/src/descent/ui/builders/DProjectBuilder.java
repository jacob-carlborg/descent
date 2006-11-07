package descent.ui.builders;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import descent.ui.DescentUI;

public class DProjectBuilder extends IncrementalProjectBuilder {
	
	private IPath binPath = new Path("bin");
	private IPath srcPath = new Path("src");
	private IPath diPath = new Path("di");
	
	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		IResourceDelta delta = getDelta(getProject());
		if (delta == null) {
			fullBuild(monitor);
		} else {
			incrementalBuild(delta, monitor);
		}
		return null;
	}

	private void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) {
		try {
			delta.accept(new Visitor());
		} catch (CoreException e) {
			DescentUI.log(e);
		}
	}

	private void fullBuild(IProgressMonitor monitor) {
	}
	
	private void clean(IFile file) {
		try {
			IFile outputFile = getOutputFile(file);
			if (outputFile == null) return;
			
			outputFile.delete(true, null);
			
			IContainer parent = outputFile.getParent();
			while(parent != null && parent.members().length == 0) {
				if (parent.getFullPath().removeFirstSegments(1).equals(binPath)) 
					return;
				
				parent.delete(true, null);
				parent = parent.getParent();
			}
		} catch (Exception e) {
			DescentUI.log(e);
		}
	}
	
	private void compile(IFile file) {
		try {
			// Delete previous errors
			file.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
			
			// Get the compile command
			String cmd = getCompilerCommand(file);
			
			System.out.println(cmd);
			
			// Execute it and wait
			Process process = Runtime.getRuntime().exec(cmd, null, null);
			
			InputStream input = process.getInputStream();
			InputStreamReader reader = new InputStreamReader(input);
			BufferedReader buffered = new BufferedReader(reader);
			
			String filename = file.getRawLocation().toOSString();
			
			String line;
			while((line = buffered.readLine()) != null) {
				System.out.println(line);
				if (line.startsWith(filename)) {
					boolean toBreak = createProblem(file, line, IMarker.SEVERITY_ERROR);
					if (toBreak) {
						break;
					}
				} else if (line.startsWith("warning")) {
					createProblem(file, line, IMarker.SEVERITY_WARNING);
				}
			}
			
			// No hace falta...
			IFolder folder = getBinFolder(file);
			if (folder != null) {
				folder.refreshLocal(1, null);
			}
			
			folder = getDiFolder(file);
			if (folder != null) {
				folder.refreshLocal(1, null);
			}
		} catch (Exception e) {
			DescentUI.log(e);
		}
	}
	
	private String getCompilerCommand(IFile file) throws CoreException {
		DCompileCommand cmd = new DCompileCommand();
		cmd.setDmdPath(DescentUI.getDefault().getDMDCompilerPath());
		cmd.setEnableWarnings(true);
		
		// Veo cuál es el path de output de di
		IFolder diFolder = getDiFolder(file);
		if (!diFolder.exists()) {
			diFolder.create(true, true, null);
		}
		cmd.setGenerateDInterfaceFiles(true);
		cmd.setDInterfaceFileOutputDirectory(diFolder.getRawLocation().toOSString());
		
		// Veo cuál es el path de output
		IFolder outputFolder = getBinFolder(file);
		if (!outputFolder.exists()) {
			outputFolder.create(true, true, null);
		}
		cmd.setOutputDirectory(outputFolder.getRawLocation().toOSString());
		
		cmd.addImportPath(getImportPath(getProject()).toOSString());
		cmd.addImportPath(getDiPath(getProject()).toOSString());
		for(IProject refrencedProject : getProject().getReferencedProjects()) {
			cmd.addImportPath(getImportPath(refrencedProject).toOSString());
			cmd.addImportPath(getDiPath(refrencedProject).toOSString());
		}
		
		cmd.addFile(file.getRawLocation().toOSString());
		cmd.setCompileOnly(true);		
		
		return cmd.toString();
	}
	
	private IFolder getBinFolder(IFile file) {
		return getFolder(file, "bin");
	}
	
	private IFolder getDiFolder(IFile file) {
		return getFolder(file, "di");
	}
	
	private IFolder getFolder(IFile file, String name) {
		IPath resourcePath = file.getFullPath();
		IPath filePath = resourcePath.removeFirstSegments(2);
		IPath folderPath = filePath.removeLastSegments(1);
		IPath binPath = new Path(name);
		
		IPath outputPath = binPath.append(folderPath);
		return getProject().getFolder(outputPath);
	}
	
	private IFile getOutputFile(IFile file) throws CoreException {
		IPath resourcePath = file.getFullPath();
		IPath filePath = resourcePath.removeFirstSegments(2);
		IPath outputFolderPath = binPath.append(filePath.removeFileExtension().addFileExtension("obj"));
		return file.getProject().getFile(outputFolderPath);
	}
	
	private IPath getImportPath(IProject project) {
		return project.getFolder(srcPath).getRawLocation();
	}
	
	private IPath getDiPath(IProject project) {
		return project.getFolder(diPath).getRawLocation();
	}
	
	private boolean createProblem(IResource resource, String line, int severity) throws CoreException {
		int firstParen = line.indexOf('(');
		int secondParen = line.indexOf(')');
		String numPart = line.substring(firstParen + 1, secondParen);
		int lineNumber = Integer.parseInt(numPart);
		String message = line.substring(secondParen + 3);
		
		IMarker marker = resource.createMarker(IMarker.PROBLEM);
		marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		marker.setAttribute(IMarker.MESSAGE, message);
		marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
		marker.setAttribute(IMarker.SEVERITY, severity);
		
		// TODO: Este mensaje se repite indefinidamente, creo... 
		if (message.equals("found 'EOF' instead of statement")) {
			return true;
		} else {
			return false;
		}
	}
	
	// TODO: do not double parse, see how JDT do it
	/*
	private ICompilationUnit getCompilationUnit(IFile file) throws CoreException, IOException {
		IParser parser = DescentCore.getDefault().getParser();
		
		InputStream stream = file.getContents();
		StringBuilder s = new StringBuilder();
		byte[] data = new byte[1024];
		while(stream.available() > 0) {
			int len = stream.read(data, 0, 1024);
			s.append(new String(data, 0, len));
		}
		
		return parser.parseCompilationUnit(s.toString());
	}
	*/
	
	private class Visitor implements IResourceDeltaVisitor {
		
		public boolean visit(IResourceDelta delta) {
			IResource resource = delta.getResource();
			
			if (resource.getType() != IResource.FILE) {
				return true;
			}
			
			if (!resource.getFileExtension().equals("d")) {
				return false;
			}
			
			switch(delta.getKind()) {
				case IResourceDelta.ADDED:
				case IResourceDelta.CHANGED:
					IPath rawLocation = resource.getRawLocation();
					File file =  rawLocation.toFile();
					if (file.exists() && file.isFile()) {
						// Check that the path of the resource is inside 
						// the "src" folder
						IPath resourcePath = resource.getFullPath();
						IPath projectPath = getProject().getFullPath();
						projectPath = projectPath.append(new Path("src"));
						if (projectPath.isPrefixOf(resourcePath)) {
							compile((IFile) resource);
							return false;
						}
					}
					break;
				case IResourceDelta.REMOVED:
					clean((IFile) resource);
					break;
			}
			
			return true;
		}
		
	}

}
