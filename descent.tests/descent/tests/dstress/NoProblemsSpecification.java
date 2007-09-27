package descent.tests.dstress;

import descent.internal.compiler.parser.Module;

public class NoProblemsSpecification implements ISpecification {
	
	private final int apiLevel;

	public NoProblemsSpecification(int apiLevel) {
		this.apiLevel = apiLevel;		
	}

	public int getApiLevel() {
		return apiLevel;
	}

	public void validate(char[] source, Module module) throws Exception {
		if (module.problems != null && module.problems.size() != 0) {
			throw new Exception("Expected no problems, but found: " + module.problems);
		}
	}

}
