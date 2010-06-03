package descent.internal.core.ctfe;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IVariable;

import descent.core.ctfe.IDebugElementFactory;
import descent.core.ctfe.IDescentValue;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.TupleDeclaration;
import descent.internal.compiler.parser.Type;

public class DescentTupleValue extends DescentDebugElement implements IDescentValue {

	private final String fName;
	private final TupleDeclaration fTuple;
	private final IDebugElementFactory fElementFactory;
	private IVariable[] fVariables;
	private final int fStackFrame;	

	public DescentTupleValue(IDebugTarget target, IDebugElementFactory elementFactory, int stackFrame, String name, TupleDeclaration value) {
		super(target);
		this.fElementFactory = elementFactory;
		this.fStackFrame = stackFrame;
		this.fName = name;
		this.fTuple = value;
	}

	public String getReferenceTypeName() throws DebugException {
		return fName;
	}

	public String getValueString() throws DebugException {
		return getDetail();
	}
	
	public IVariable[] getVariables() throws DebugException {
		if (fVariables == null) {
			List<IVariable> vars = new ArrayList<IVariable>();
			for (int i = 0; i < fTuple.objects.size(); i++) {
				ASTDmdNode node = fTuple.objects.get(i);
				if (node instanceof Expression) {
					vars.add(fElementFactory.newVariable(fStackFrame, "[" + i + "]", (Expression) node)); 
				} else if (node instanceof Type) {
					vars.add(fElementFactory.newVariable(fStackFrame, "[" + i + "]", (Type) node));
				}
			}
			fVariables = vars.toArray(new IVariable[vars.size()]);
		}
		return fVariables;
	}

	public boolean hasVariables() throws DebugException {
		return fTuple.objects != null && !fTuple.objects.isEmpty();
	}

	public boolean isAllocated() throws DebugException {
		return true;
	}
	
	public boolean isLazy() {
		return fTuple != null;
	}
	
	public String getDetail() {
		StringBuilder sb = new StringBuilder();
		sb.append("Tuple!(");
		if (fTuple.objects != null && !fTuple.objects.isEmpty()) {
			for (int i = 0; i < fTuple.objects.size(); i++) {
				if (i != 0)
					sb.append(", ");
				sb.append(fTuple.objects.get(i));
			}
		}
		sb.append(")");
		return sb.toString();
	}
	
}
