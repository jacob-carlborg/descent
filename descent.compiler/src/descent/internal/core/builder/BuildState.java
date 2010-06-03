package descent.internal.core.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import descent.core.ICompilationUnit;

public class BuildState {
	
	/*
	 * Means that a module depends on a given list of modules.  
	 */
	private Map<ICompilationUnit, List<ICompilationUnit>> dependencies;
	
	/*
	 * Means that a list of modules depend on a given module.
	 */
	private Map<ICompilationUnit, List<ICompilationUnit>> reverseDependencies;
	
	private Queue<BuildRequest> queue;
	
	public BuildState() {
		this.queue = new LinkedList<BuildRequest>();
		this.dependencies = new HashMap<ICompilationUnit, List<ICompilationUnit>>();
		this.reverseDependencies = new HashMap<ICompilationUnit, List<ICompilationUnit>>();
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
	
	public List<ICompilationUnit> getCompilationUnitsThatDependOn(ICompilationUnit unit) {
		return this.reverseDependencies.get(unit);
	}
	
	public void setDependencies(ICompilationUnit unit, List<ICompilationUnit> dependencies) {
		// First remove old dependencies from the reverse lookup
		if (dependencies != null) {
			for(ICompilationUnit dependency : dependencies) {
				List<ICompilationUnit> list = this.reverseDependencies.get(dependency);
				if (list != null) {
					list.remove(unit);
				}
			}
		}
		
		this.dependencies.put(unit, dependencies);
		
		// Then add the new dependencies to the reverse lookup
		if (dependencies != null) {
			for(ICompilationUnit dependency : dependencies) {
				List<ICompilationUnit> list = this.reverseDependencies.get(dependency);
				if (list == null) {
					list = new ArrayList<ICompilationUnit>();
					this.reverseDependencies.put(dependency, list);
				}
				list.add(unit);
			}
		}
	}
	
	public void clearDependencies(ICompilationUnit unit) {
		this.dependencies.remove(unit);
	}

}
