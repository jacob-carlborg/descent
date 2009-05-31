package descent.internal.core.ctfe;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IVariable;

import descent.core.ctfe.IDebugElementFactory;
import descent.core.ctfe.IDescentValue;
import descent.internal.compiler.parser.ArrayInitializer;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.Initializer;
import descent.internal.compiler.parser.IntegerExp;

public class DescentArrayInitializerValue extends DescentDebugElement implements IDescentValue {

	private final String fName;
	private final ArrayInitializer fArray;
	private final IDebugElementFactory fElementFactory;
	private IVariable[] fVariables;
	private final int fStackFrame;	

	public DescentArrayInitializerValue(IDebugTarget target, IDebugElementFactory elementFactory, int stackFrame, String name, ArrayInitializer value) {
		super(target);
		this.fElementFactory = elementFactory;
		this.fStackFrame = stackFrame;
		this.fName = name;
		this.fArray = value;
	}

	public String getReferenceTypeName() throws DebugException {
		return fName;
	}

	public String getValueString() throws DebugException {
		return Utils.getTypeName(fArray.type.next) + "[" + fArray.dim +"]";
	}
	
	public IVariable[] getVariables() throws DebugException {
		if (fVariables == null) {
			fVariables = new IVariable[(int)fArray.dim];
			
			int index = 0;
			for (int i = 0; i < fArray.index.size(); i++) {
				Expression idx = fArray.index.get(i);
				Initializer init = fArray.value.get(i);
				if (idx != null && idx instanceof IntegerExp) {
					index = ((IntegerExp) idx).toInteger(null).intValue();
				}
				fVariables[index] = fElementFactory.newVariable(fStackFrame, "[" + index + "]", init);
				index++;
			}
			
			for (int i = 0; i < fVariables.length; i++) {
				if (fVariables[i] == null) {
					fVariables[i] = fElementFactory.newVariable(fStackFrame, "[" + i + "]", Utils.getDefaultValue(fArray.type.next));
				}
			}
		}
		return fVariables;
	}

	public boolean hasVariables() throws DebugException {
		return fArray.index != null && !fArray.index.isEmpty();
	}

	public boolean isAllocated() throws DebugException {
		return true;
	}
	
	public boolean isLazy() {
		return fArray != null;
	}
	
	public String getDetail() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		if (fArray.value != null && !fArray.value.isEmpty()) {
			for (int i = 0; i < fArray.value.size(); i++) {
				if (i != 0)
					sb.append(", ");
				Expression idx = fArray.index.get(i);
				if (idx != null) {
					sb.append(idx);
					sb.append(": ");
				}
				sb.append(fArray.value.get(i));
			}
		}
		sb.append(")");
		return sb.toString();
	}
	
}
