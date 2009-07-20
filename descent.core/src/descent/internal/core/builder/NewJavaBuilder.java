package descent.internal.core.builder;

import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.IJavaModelMarker;
import descent.core.IJavaProject;
import descent.core.JavaCore;
import descent.core.compiler.CharOperation;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ASTNodeEncoder;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.Parser;

/**
 * Uses the lexer to grab the task tags in a source file, and creates
 * task resource markers with them.
 */
public class NewJavaBuilder extends IncrementalProjectBuilder {
	
	private static BuildState buildState = new BuildState();
	
	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
		IProject project = getProject();
		IResourceDelta delta = getDelta(project);
		if (delta == null) {
			if (monitor == null) {
				monitor = new NullProgressMonitor();
			}
			fullBuild(project, monitor);
		} else {
			IJavaProject javaProject = JavaCore.create(project);
			delta.accept(new JavaBuilderVisitor(javaProject.getApiLevel(), isShowSemanticErrors()));
		}
		
		while(buildState.hasPendingRequests()) {
			BuildRequest request = buildState.pop();
			build(request);
		}
		
		return null;
	}

	private void build(BuildRequest request) {
		
	}

	private void fullBuild(IProject project, IProgressMonitor monitor) throws CoreException {
		IJavaProject javaProject = JavaCore.create(project);
		IResource[] members = project.members();
		build(javaProject, isShowSemanticErrors(), members, new ASTNodeEncoder(javaProject.getApiLevel()), monitor);
	}
	
	private boolean isShowSemanticErrors() {
		IJavaProject activeProject = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()).getActiveProject();
		String showSemanticErrorsOpt = JavaCore.getOption(JavaCore.COMPILER_SHOW_SEMANTIC_ERRORS);
		if (activeProject == null) {
			showSemanticErrorsOpt = JavaCore.getOption(JavaCore.COMPILER_SHOW_SEMANTIC_ERRORS); 
		} else {
			showSemanticErrorsOpt = activeProject.getOption(JavaCore.COMPILER_SHOW_SEMANTIC_ERRORS, true);
		}
		
		return !showSemanticErrorsOpt.equals("0");
	}
	
	private class JavaBuilderVisitor implements IResourceDeltaVisitor {
		
		private final boolean showSemanticErrors;
		private final ASTNodeEncoder encoder;
		
		public JavaBuilderVisitor(int apiLevel, boolean showSemanticErrors) {
			this.showSemanticErrors = showSemanticErrors;
			this.encoder = new ASTNodeEncoder(apiLevel);
		}

		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			if (resource.getType() == IResource.FILE) {
				switch(delta.getKind()) {
				case IResourceDelta.ADDED:
				case IResourceDelta.CHANGED:
					IFile file = (IFile) resource;
					build(JavaCore.create(file.getProject()), showSemanticErrors, file, encoder);
					break;
				case IResourceDelta.REMOVED:
					break;
				}
			}
			return true;
		}
	}
	
	/**
	 * Recursively associate tasks and problems to the given resources.
	 */
	public static void build(IJavaProject javaProject, boolean showSemanticErrors,
			IResource[] resources, ASTNodeEncoder encoder, IProgressMonitor monitor) throws CoreException {
		monitor.beginTask("", 100 * resources.length);
		for(IResource resource : resources) {
			if (monitor.isCanceled()) {
				return;
			}
			
			build(javaProject, showSemanticErrors, resource, encoder, new SubProgressMonitor(monitor, 100));
		}
		monitor.done();
	}
	
	/**
	 * Recursively associate tasks and problems to the given resource.
	 */
	public static void build(IJavaProject javaProject, boolean showSemanticErrors, IResource resource, ASTNodeEncoder encoder, IProgressMonitor monitor) throws CoreException {
		if (resource.getType() == IResource.FILE) {
			monitor.beginTask("", 1);
			build(javaProject, showSemanticErrors, (IFile) resource, encoder);
			monitor.done();
		} else if (resource instanceof IContainer) {
			IResource[] members = ((IContainer) resource).members();
			build(javaProject, showSemanticErrors, members, encoder, monitor);
		}
		monitor.done();
	}
	
	/**
	 * Associate tasks and problems to the given file, 
	 * if that file is an ICompilationUnit.
	 */
	public static void build(IJavaProject javaProject, boolean showSemanticErrors,  IFile file, ASTNodeEncoder encoder) throws CoreException {
		IJavaElement element = JavaCore.create(file);
		if (element != null && element.getElementType() == IJavaElement.COMPILATION_UNIT) {
			ICompilationUnit unit = (ICompilationUnit) element;
			buildState.add(new BuildRequest(unit, file, encoder));
			
//			removeTasks(file);
//			removeProblems(file);
//			
//			
//			findTaskTags(javaProject, showSemanticErrors, file, unit, encoder);
//			compile(javaProject, file, unit);
//			
//			if (unit.hasMainMethod()) {
//				createExecutable(javaProject, file, unit);
//			}
		}
	}

//	private static void compile(IJavaProject javaProject, IFile file, ICompilationUnit unit) throws CoreException {
//		try {
//			String inFilename = file.getLocation().toOSString();
//			String workingDir = javaProject.getResource().getLocation().toOSString(); 
//			String binDir = workingDir + "\\bin";
//			String outFilename = unit.getFullyQualifiedName() + ".obj";;
//			
//			CompilerCommand command = new CompilerCommand();
//			command.setCommand("dmd");
//			command.setCompile(true);
//			command.addInputFile(inFilename);
//			command.setOutputDir(binDir);
//			command.setOutputFile(outFilename);
//			
//			String cmd = command.toString();
//			
//			System.out.println(cmd);
//			
//			Process process = Runtime.getRuntime().exec(cmd, null, new File(workingDir));
//			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//			String line;
//			
//			while((line = reader.readLine()) != null) {
//				System.out.println(line);
//			}
//		} catch (Exception e) {
//			System.out.println(e);
//		}
//	}
//	
//	private static void createExecutable(IJavaProject javaProject, IFile file, ICompilationUnit unit) {
//		String[] dependencies = findDepdenecies(javaProject, file, unit);
//		
//		try {
//			String workingDir = javaProject.getResource().getLocation().toOSString(); 
//			String binDir = workingDir + "\\bin";
//			String inFilename = binDir + "\\" + unit.getFullyQualifiedName() + ".obj";
//			String outFilename = unit.getFullyQualifiedName() + ".exe";
//			
//			CompilerCommand command = new CompilerCommand();
//			command.setCommand("dmd");
//			command.addInputFile(inFilename);
//			for(String dependency : dependencies) {
//				command.addInputFile(dependency);
//			}
//			command.setOutputDir(binDir);
//			command.setOutputFile(outFilename);
//			
//			String cmd = command.toString();
//			
//			System.out.println(cmd);
//			
//			Process process = Runtime.getRuntime().exec(cmd, null, new File(binDir));
//			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//			String line;
//			
//			while((line = reader.readLine()) != null) {
//				System.out.println(line);
//			}
//		} catch (Exception e) {
//			System.out.println(e);
//		}
//	}
//
//	private static String[] findDepdenecies(IJavaProject javaProject, IFile file, ICompilationUnit unit) {
//		return new String[] { "C:\\ary\\programacion\\java\\eclipse.3.0\\workspace-d\\test\\bin\\other.obj " };
//	}
//
//	private static void findTaskTags(IJavaProject javaProject, boolean showSemanticErrors, IFile file, ICompilationUnit unit, ASTNodeEncoder encoder) throws CoreException {
//		String source = unit.getSource();
//		
//		IPackageFragmentRoot root = (IPackageFragmentRoot) unit.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
//		char[] filename = unit.getPath().removeFirstSegments(root.getPath().segmentCount()).toString().toCharArray();
//		
//		Parser parser = new Parser(
//				Util.getApiLevel(javaProject), 
//				source.toCharArray(), 
//				0, 
//				source.length(),
//				getTaskTags(javaProject),
//				getTaskPriorities(javaProject),
//				getTaskCaseSensitive(javaProject),
//				filename,
//				encoder
//				);
//		Module module = parser.parseModuleObj();
//		module.moduleName = unit.getFullyQualifiedName();
//		
//		if (showSemanticErrors) {
//			CompilationUnitResolver.resolve(module, javaProject, unit.getOwner(), parser.encoder);
//		}
//		
//		associateTaskTags(file, parser);
////		associateProblems(file, module);
//	}

	/**
	 * Removes any task markers from the given file.
	 */
	public static void removeTasks(IFile file) throws CoreException {
		file.deleteMarkers(IJavaModelMarker.TASK_MARKER, false, IResource.DEPTH_INFINITE);
	}
	
	/**
	 * Removes any problem markers from the given file.
	 */
	public static void removeProblems(IFile file) throws CoreException {
		file.deleteMarkers(IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER, false, IResource.DEPTH_INFINITE);
	}
	
	private static void associateTaskTags(IFile file, Parser parser) throws CoreException {
		if (parser.foundTaskMessages == null) {
			return;
		}
		
		for(int i = 0; i < parser.foundTaskCount; i++) {
			IMarker marker = file.createMarker(IJavaModelMarker.TASK_MARKER);
			marker.setAttribute(IMarker.MESSAGE, new String(CharOperation.concat(parser.foundTaskTags[i], parser.foundTaskMessages[i], ' ')));
			marker.setAttribute(IMarker.CHAR_START, parser.foundTaskPositions[i][0]);
			marker.setAttribute(IMarker.CHAR_END, parser.foundTaskPositions[i][1] + 1); // for markers it's + 1
			marker.setAttribute(IMarker.LINE_NUMBER, parser.getLineNumber(parser.foundTaskPositions[i][0]));
			marker.setAttribute(IMarker.USER_EDITABLE, Boolean.FALSE);
			char[] priority = parser.foundTaskPriorities[i];
			switch(priority[0]) {
			case 'H': // HIGH
				marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
				break;
			case 'N': // NORMAL
				marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
				break;
			case 'L': // LOW
				marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_LOW);
				break;
			}
		}
	}
	
	private static void associateProblems(IFile file, Module module) throws CoreException {
		if (module.problems == null) {
			return;
		}
		
		for(IProblem problem : module.problems) {
			if (problem.getID() == IProblem.Task) {
				continue;
			}
			
			IMarker marker = file.createMarker(IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER);
			marker.setAttribute(IMarker.MESSAGE, problem.getMessage());
			marker.setAttribute(IMarker.SEVERITY, problem.isError() ? IMarker.SEVERITY_ERROR : IMarker.SEVERITY_WARNING);
			marker.setAttribute(IMarker.CHAR_START, problem.getSourceStart());
			marker.setAttribute(IMarker.CHAR_END, problem.getSourceEnd() + 1); // for markers it's + 1
			marker.setAttribute(IMarker.LINE_NUMBER, problem.getSourceLineNumber());
		}
	}
	
	private static char[][] getTaskTags(IJavaProject project) {
		String taskTags = project.getOption(JavaCore.COMPILER_TASK_TAGS, true);
		return CharOperation.splitOn(',', taskTags.toCharArray());
	}
	
	private static char[][] getTaskPriorities(IJavaProject project) {
		String taskPriorities = project.getOption(JavaCore.COMPILER_TASK_PRIORITIES, true);
		return CharOperation.splitOn(',', taskPriorities.toCharArray());
	}
	
	private static boolean getTaskCaseSensitive(IJavaProject project) {
		return project.getOption(JavaCore.COMPILER_TASK_CASE_SENSITIVE, true).equals(JavaCore.ENABLED);
	}

}
