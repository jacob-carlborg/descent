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
		if (value instanceof IEvaluationResult[]) {
			IEvaluationResult[] results = (IEvaluationResult[]) value;
			
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			for (int i = 0; i < results.length; i++) {
				if (i != 0) {
					sb.append(", ");
				}
				sb.append(results[i]);
			}
			sb.append("]");
			return sb.toString();
		}
		return value.toString();
	}

}
