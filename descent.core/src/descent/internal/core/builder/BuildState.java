package descent.internal.core.builder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import descent.core.ICompilationUnit;

public class BuildState {
	
	/*
	 * Means that a module depends on the given list of modules.  
	 */
	private Map<ICompilationUnit, List<ICompilationUnit>> dependencies;
	
	private Queue<BuildRequest> queue;
	
	public BuildState() {
		this.queue = new LinkedList<BuildRequest>();
		this.dependencies = new HashMap<ICompilationUnit, List<ICompilationUnit>>();
	}
	
	public boolean hasPendingRequests() {
		return !this.queue.isEmpty();
	}
	
	public BuildRequest pop() {
		return this.queue.remove();
	}
	
	public void add(BuildRequest request) {
		this.queue.offer(request);
	}
	
	public List<ICompilationUnit> getDependencies(ICompilationUnit unit) {
		return this.dependencies.get(unit);
	}
	
	public void setDependencies(ICompilationUnit unit, List<ICompilationUnit> dependencies) {
		this.dependencies.put(unit, dependencies);
	}
	
	public void clearDependencies(ICompilationUnit unit) {
		this.dependencies.remove(unit);
	}

}
