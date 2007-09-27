package descent.tests.dstress;

import java.util.ArrayList;
import java.util.List;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.Module;

public class ProblemsWithSubstringOffsetAndLengthSpecification implements ISpecification {
	
	private final int apiLevel;
	private List<Integer> problemIds;
	private List<String> substrings;
	private List<Integer> offsets;
	private List<Integer> lengths;

	public ProblemsWithSubstringOffsetAndLengthSpecification(int apiLevel) {
		this.apiLevel = apiLevel;
		this.problemIds = new ArrayList<Integer>();
		this.substrings = new ArrayList<String>();
		this.offsets = new ArrayList<Integer>();
		this.lengths = new ArrayList<Integer>();
	}
	
	public void addProblem(int problemId, String substring, int offset, int length) {
		this.problemIds.add(problemId);
		this.substrings.add(substring);
		this.offsets.add(offset);
		this.lengths.add(length);
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
			
			if (problem.getSourceStart() != index + offsets.get(i)) {
				throw new Exception("Problem number " + i + " expected to have offset " + (index + offsets.get(i)) + " but was " + problem.getSourceStart());
			}
		
			if ((problem.getSourceEnd() - problem.getSourceStart() + 1) != lengths.get(i)) {
				throw new Exception("Problem number " + i + " expected to have length " + lengths.get(i) + " but was " + (problem.getSourceEnd() - problem.getSourceStart() + 1));
			}
		}
	}

}
