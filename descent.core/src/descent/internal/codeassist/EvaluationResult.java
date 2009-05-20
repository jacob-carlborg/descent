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
			if (kind == TUPLE) {
				sb.append("Tuple!(");
			} else {
				sb.append("[");
			}
			for (int i = 0; i < results.length; i++) {
				if (i != 0) {
					sb.append(", ");
					if (results[i].getKind() == IEvaluationResult.ARRAY) {
						sb.append("\n  ");
					}
				}
				sb.append(results[i]);
			}
			if (kind == TUPLE) {
				sb.append(")");
			} else {
				sb.append("]");
			}
			return sb.toString();
		}
		if (kind == CHAR_ARRAY || kind == DCHAR_ARRAY || kind == WCHAR_ARRAY) {
			return "\"" + value.toString() + "\"";
		}
		return value.toString();
	}

}
