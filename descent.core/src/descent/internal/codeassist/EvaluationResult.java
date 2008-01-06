package descent.internal.codeassist;

import descent.core.IEvaluationResult;

public class EvaluationResult implements IEvaluationResult {
	
	private final Object value;
	private final int kind;

	public EvaluationResult(Object value, int kind) {
		this.value = value;
		this.kind = kind;
	}

	public int getKind() {
		return kind;
	}

	public Object getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return value.toString();
	}

}
