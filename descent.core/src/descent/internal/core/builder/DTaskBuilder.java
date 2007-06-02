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
import descent.core.dom.AST;
import descent.internal.compiler.parser.Lexer;
import descent.internal.compiler.parser.TOK;

/**
 * Uses the lexer to grab the task tags in a source file, and creates
 * task resource markers with them.
 */
public class DTaskBuilder extends IncrementalProjectBuilder implements IResourceDeltaVisitor {
	
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
			delta.accept(this);
		}
		return null;
	}

	private void fullBuild(IProject project, IProgressMonitor monitor) throws CoreException {
		IResource[] members = project.members();
		checkTasks(members, monitor);
	}
	
	private void checkTasks(IResource[] resources, IProgressMonitor monitor) throws CoreException {
		monitor.beginTask("", 100 * resources.length);
		for(IResource resource : resources) {
			checkTasks(resource, new SubProgressMonitor(monitor, 100));
		}
		monitor.done();
	}
	
	private void checkTasks(IResource resource, IProgressMonitor monitor) throws CoreException {
		if (resource.getType() == IResource.FILE) {
			monitor.beginTask("", 1);
			checkTasks((IFile) resource);
			monitor.done();
		} else if (resource instanceof IContainer) {
			IResource[] members = ((IContainer) resource).members();
			checkTasks(members, monitor);
		}
		monitor.done();
	}
	
	private void checkTasks(IFile file) throws CoreException{
		IJavaElement element = JavaCore.create(file);
		if (element != null && element.getElementType() == IJavaElement.COMPILATION_UNIT) {
			IProject project = getProject();
			IJavaProject javaProject = JavaCore.create(project);
			
			ICompilationUnit unit = (ICompilationUnit) element;
			String source = unit.getSource();
			
			Lexer lexer = new Lexer(source, 
					true /* tokenize comments */, 
					false /* tokenize pragmas */, 
					false /* tokenize whitespace */, 
					true /* record line separators */, 
					AST.LATEST);
			lexer.taskTags = getTaskTags(javaProject);
			lexer.taskPriorities = getTaskPriorities(javaProject);
			lexer.isTaskCaseSensitive = getTaskCaseSensitive(javaProject);
			while(lexer.nextToken() != TOK.TOKeof) {
				
			}
			
			file.deleteMarkers(IJavaModelMarker.TASK_MARKER, false, IResource.DEPTH_INFINITE);
			
			if (lexer.foundTaskMessages != null) {
				for(int i = 0; i < lexer.foundTaskCount; i++) {
					IMarker marker = file.createMarker(IJavaModelMarker.TASK_MARKER);
					marker.setAttribute(IMarker.MESSAGE, new String(CharOperation.concat(lexer.foundTaskTags[i], lexer.foundTaskMessages[i], ' ')));
					marker.setAttribute(IMarker.CHAR_START, lexer.foundTaskPositions[i][0]);
					marker.setAttribute(IMarker.CHAR_END, lexer.foundTaskPositions[i][1]);
					marker.setAttribute(IMarker.LINE_NUMBER, lexer.getLineNumber(lexer.foundTaskPositions[i][0]));
					marker.setAttribute(IMarker.USER_EDITABLE, Boolean.FALSE);
					char[] priority = lexer.foundTaskPriorities[i];
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
		}
	}

	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource resource = delta.getResource();
		if (resource.getType() == IResource.FILE) {
			checkTasks((IFile) resource);
		}
		return true;
	}

	private char[][] getTaskTags(IJavaProject project) {
		String taskTags = project.getOption(JavaCore.COMPILER_TASK_TAGS, true);
		return CharOperation.splitOn(',', taskTags.toCharArray());
	}
	
	private char[][] getTaskPriorities(IJavaProject project) {
		String taskPriorities = project.getOption(JavaCore.COMPILER_TASK_PRIORITIES, true);
		return CharOperation.splitOn(',', taskPriorities.toCharArray());
	}
	
	private boolean getTaskCaseSensitive(IJavaProject project) {
		return project.getOption(JavaCore.COMPILER_TASK_CASE_SENSITIVE, true).equals(JavaCore.ENABLED);
	}

}
