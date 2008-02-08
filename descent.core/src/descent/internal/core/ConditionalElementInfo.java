package descent.internal.core;

/** 
 * Element info for IConditional elements. 
 */
public class ConditionalElementInfo extends MemberElementInfo {
	
	/**
	 * 0: unknown
	 * 1: true ("then" wins)
	 * 2: false ("else" wins)
	 */
	protected int evaluationResult;
	
	public void setEvaluationResult(int evaluationResult) {
		this.evaluationResult = evaluationResult;
	}
	
	public int getEvaluationResult() {
		return evaluationResult;
	}
	
}
