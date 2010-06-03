package descent.internal.codeassist;

import descent.core.IEvaluationResult;
import descent.core.IStructLiteral;

public class StructLiteral implements IStructLiteral {

	private final String name;
	private final String[] names;
	private final IEvaluationResult[] values;

	public StructLiteral(String name, String[] names, IEvaluationResult[] values) {
		this.name = name;
		this.names = names;
		this.values = values;		
	}

	/*
	 * (non-Javadoc)
	 * @see descent.core.IStructLiteral#getName()
	 */
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * @see descent.core.IStructLiteral#getNames()
	 */
	public String[] getNames() {
		return names;
	}

	/*
	 * (non-Javadoc)
	 * @see descent.core.IStructLiteral#getValues()
	 */
	public IEvaluationResult[] getValues() {
		return values;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("struct ");
		sb.append(name);
		sb.append(" {\n");
		for (int i = 0; i < names.length; i++) {
			if (i != 0) {
				sb.append(",\n");
			}
			sb.append("  ");
			sb.append(names[i]);
			sb.append(" = ");
			sb.append(values[i]);
		}
		sb.append("\n}");
		return sb.toString();
	}

}
