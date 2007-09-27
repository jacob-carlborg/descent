package descent.tests.dstress;

/**
 * Factory for specifications.
 */
public class SpecificationsFactory {
	
	/**
	 * A specification that passes when no problem is found
	 * in the module.
	 */
	public static ISpecification zero(int apiLevel) {
		return new NoProblemsSpecification(apiLevel);
	}
	
	public static ISpecification one(int apiLevel, int problemId, String substring) {
		ProblemsWithSubstringSpecification spec = new ProblemsWithSubstringSpecification(apiLevel);
		spec.addProblem(problemId, substring);
		return spec;
	}
	
	public static ISpecification one(int apiLevel, int problemId, String substring, int offset, int length) {
		ProblemsWithSubstringOffsetAndLengthSpecification spec = new ProblemsWithSubstringOffsetAndLengthSpecification(apiLevel);
		spec.addProblem(problemId, substring, offset, length);
		return spec;
	}
	
	public static ISpecification two(int apiLevel, int problemId1, String substring1, int problemId2, String substring2) {
		ProblemsWithSubstringSpecification spec = new ProblemsWithSubstringSpecification(apiLevel);
		spec.addProblem(problemId1, substring1);
		spec.addProblem(problemId2, substring2);
		return spec;
	}
	
	public static ISpecification two(int apiLevel, int problemId1, String substring1, int offset1, int length1, int problemId2, String substring2, int offset2, int length2) {
		ProblemsWithSubstringOffsetAndLengthSpecification spec = new ProblemsWithSubstringOffsetAndLengthSpecification(apiLevel);
		spec.addProblem(problemId1, substring1, offset1, length1);
		spec.addProblem(problemId2, substring2, offset2, length2);
		return spec;
	}
	
	public static ISpecification three(int apiLevel, int problemId1, String substring1, int problemId2, String substring2, int problemId3, String substring3) {
		ProblemsWithSubstringSpecification spec = new ProblemsWithSubstringSpecification(apiLevel);
		spec.addProblem(problemId1, substring1);
		spec.addProblem(problemId2, substring2);
		spec.addProblem(problemId3, substring3);
		return spec;
	}
	
	public static ISpecification three(int apiLevel, int problemId1, String substring1, int offset1, int length1, int problemId2, String substring2, int offset2, int length2, int problemId3, String substring3, int offset3, int length3) {
		ProblemsWithSubstringOffsetAndLengthSpecification spec = new ProblemsWithSubstringOffsetAndLengthSpecification(apiLevel);
		spec.addProblem(problemId1, substring1, offset1, length1);
		spec.addProblem(problemId2, substring2, offset2, length2);
		spec.addProblem(problemId3, substring3, offset3, length3);
		return spec;
	}
	
	
	public static ISpecification four(int apiLevel, int problemId1, String substring1, int problemId2, String substring2, int problemId3, String substring3, int problemId4, String substring4) {
		ProblemsWithSubstringSpecification spec = new ProblemsWithSubstringSpecification(apiLevel);
		spec.addProblem(problemId1, substring1);
		spec.addProblem(problemId2, substring2);
		spec.addProblem(problemId3, substring3);
		spec.addProblem(problemId4, substring4);
		return spec;
	}
	
	public static ISpecification four(int apiLevel, int problemId1, String substring1, int offset1, int length1, int problemId2, String substring2, int offset2, int length2, int problemId3, String substring3, int offset3, int length3, int problemId4, String substring4, int offset4, int length4) {
		ProblemsWithSubstringOffsetAndLengthSpecification spec = new ProblemsWithSubstringOffsetAndLengthSpecification(apiLevel);
		spec.addProblem(problemId1, substring1, offset1, length1);
		spec.addProblem(problemId2, substring2, offset2, length2);
		spec.addProblem(problemId3, substring3, offset3, length3);
		spec.addProblem(problemId4, substring4, offset4, length4);
		return spec;
	}

}
