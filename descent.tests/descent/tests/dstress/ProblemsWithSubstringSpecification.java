package descent.tests.dstress;

import java.util.ArrayList;
import java.util.List;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.Module;

public class ProblemsWithSubstringSpecification implements ISpecification {

	private final int apiLevel;
	private List<Integer> problemIds;
	private List<String> substrings;

	public ProblemsWithSubstringSpecification(int apiLevel) {
		this.apiLevel = apiLevel;
		this.problemIds = new ArrayList<Integer>();
		this.substrings = new ArrayList<String>();
	}
	
	public void addProblem(int problemId, String substring) {
		this.problemIds.add(problemId);
		this.substrings.add(substring);
	}

	public int getApiLevel() {
		return apiLevel;
	}

	public void validate(char[] source, Module module) throws Exception {
		String sourceString = new String(source);
		if (module.problems == null || module.problems.size() != problemIds.size()) {
			throw new Exception("Expected " + problemIds.size() + " problem(s), but found: " + module.problems);
		}

		for(int i = 0; i < problemIds.size(); i++) {
			String substring = substrings.get(i);
			int index = sourceString.indexOf(substring);
			IProblem problem = module.problems.get(i);
			if (problem.getID() != problemIds.get(i)) {
				throw new Exception("Problem number " + i + " expected to have id " + problemIds.get(i) + " but was " + problem.getID());
			}
			
			if (problem.getSourceStart() != index) {
				throw new Exception("Problem number " + i + " expected to have offset " + index + " but was " + problem.getSourceStart());
			}
		
			if (problem.getSourceEnd() != index + substring.length() - 1) {
				throw new Exception("Problem number " + i + " expected to have length " + substring.length() + " but was " + (problem.getSourceEnd() - problem.getSourceStart() + 1));
			}
		}
	}

}
