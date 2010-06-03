package descent.internal.core.ctfe;

import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IVariable;

import descent.core.compiler.CharOperation;
import descent.core.ctfe.IDebugElementFactory;
import descent.core.ctfe.IDescentValue;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Initializer;
import descent.internal.compiler.parser.StructInitializer;
import descent.internal.compiler.parser.VarDeclaration;

public class DescentStructInitializerValue extends DescentDebugElement implements IDescentValue {

	private final String fName;
	private final StructInitializer fStruct;
	private final IDebugElementFactory fElementFactory;
	private IVariable[] fVariables;
	private final int fStackFrame;	

	public DescentStructInitializerValue(IDebugTarget target, IDebugElementFactory elementFactory, int stackFrame, String name, StructInitializer value) {
		super(target);
		this.fElementFactory = elementFactory;
		this.fStackFrame = stackFrame;
		this.fName = name;
		this.fStruct = value;
	}

	public String getReferenceTypeName() throws DebugException {
		return fName;
	}

	public String getValueString() throws DebugException {
		return fStruct.ad.ident.toString();
	}
	
	public IVariable[] getVariables() throws DebugException {
		if (fVariables == null) {
			fVariables = new IVariable[fStruct.ad.fields.size()];
			
			int index = 0;
			for (int i = 0; i < fStruct.field.size(); i++) {
				IdentifierExp ident = fStruct.field.get(i);
				if (ident == null) {
					ident = fStruct.ad.fields.get(index).ident;
				} else {
					index = getIndexOf(ident, fStruct.ad.fields);
				}
				Initializer value = fStruct.value.get(i);
				fVariables[index] = fElementFactory.newVariable(fStackFrame, ident.toString(), value);
				index++;
			}
			
			for (int i = 0; i < fVariables.length; i++) {
				if (fVariables[i] == null) {
					VarDeclaration field = fStruct.ad.fields.get(i);
					fVariables[i] = fElementFactory.newVariable(fStackFrame, field.ident.toString(), Utils.getDefaultValue(field.type));
				}
			}
		}
		return fVariables;
	}

	private int getIndexOf(IdentifierExp ident, List<VarDeclaration> fields) {
		for(int i = 0; i < fields.size(); i++) {
			if (CharOperation.equals(fields.get(i).ident.ident, ident.ident)) {
				return i;
			}
		}
		return -1;
	}

	public boolean hasVariables() throws DebugException {
		return fStruct.ad.fields != null && !fStruct.ad.fields.isEmpty();
	}

	public boolean isAllocated() throws DebugException {
		return true;
	}
	
	public boolean isLazy() {
		return fStruct != null;
	}
	
	public String getDetail() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		if (fStruct.value != null && !fStruct.value.isEmpty()) {
			for (int i = 0; i < fStruct.value.size(); i++) {
				if (i != 0)
					sb.append(", ");
				IdentifierExp idx = fStruct.field.get(i);
				if (idx != null) {
					sb.append(idx);
					sb.append(": ");
				}
				sb.append(fStruct.value.get(i));
			}
		}
		sb.append("}");
		return sb.toString();
	}
	
}
