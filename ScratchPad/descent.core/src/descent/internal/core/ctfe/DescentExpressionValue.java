package descent.internal.core.ctfe;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IVariable;

import descent.core.ctfe.IDebugElementFactory;
import descent.core.ctfe.IDescentValue;
import descent.internal.compiler.parser.ArrayLiteralExp;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.StructLiteralExp;
import descent.internal.compiler.parser.TypeStruct;
import descent.internal.compiler.parser.VarDeclaration;
import descent.internal.compiler.parser.VarExp;

public class DescentExpressionValue extends DescentDebugElement implements IDescentValue {

	private final String fName;
	private final Expression fExpression;
	private final IDebugElementFactory fElementFactory;
	private IVariable[] fVariables;
	private final int fStackFrame;	

	public DescentExpressionValue(IDebugTarget target, IDebugElementFactory elementFactory, int stackFrame, String name, Expression value) {
		super(target);
		this.fElementFactory = elementFactory;
		this.fStackFrame = stackFrame;
		this.fName = name;
		this.fExpression = value;
	}

	public String getReferenceTypeName() throws DebugException {
		return fName;
	}

	public String getValueString() throws DebugException {
		if (fExpression instanceof VarExp) {
			VarExp varExp = (VarExp) fExpression;
			return Utils.getTypeName(varExp.type);
		} else if (fExpression instanceof StructLiteralExp) {
			StructLiteralExp exp = (StructLiteralExp) fExpression;
			return exp.sd.ident.toString();
		} else if (fExpression instanceof ArrayLiteralExp) {
			ArrayLiteralExp exp = (ArrayLiteralExp) fExpression;
			int size = exp.elements == null ? 0 : exp.elements.size(); 
			return Utils.getTypeName(exp.elements.get(0).type) + "[" + size + "]";
		}
		return fExpression.toString();
	}
	
	public IVariable[] getVariables() throws DebugException {
		if (fVariables == null) {
			List<IVariable> vars = new ArrayList<IVariable>();
			if (fExpression instanceof VarExp) {
				VarExp varExp = (VarExp) fExpression;
				if (varExp.type instanceof TypeStruct) {
					TypeStruct struct = (TypeStruct) varExp.type;
					for(VarDeclaration var : struct.sym.fields) {
						vars.add(fElementFactory.newVariable(fStackFrame, var.ident.toString(), Utils.getDefaultValue(var.type)));
					}
				}
			} else if (fExpression instanceof StructLiteralExp) {
				StructLiteralExp exp = (StructLiteralExp) fExpression;
				for (int i = 0; i < exp.sd.fields.size(); i++) {
					vars.add(fElementFactory.newVariable(fStackFrame, exp.sd.fields.get(i).ident.toString(), exp.elements.get(i)));
				}
			} else if (fExpression instanceof ArrayLiteralExp) {
				ArrayLiteralExp exp = (ArrayLiteralExp) fExpression;
				for (int i = 0; i < exp.elements.size(); i++) {
					vars.add(fElementFactory.newVariable(fStackFrame, "[" + String.valueOf(i) + "]", exp.elements.get(i)));
				}
			}
			fVariables = vars.toArray(new IVariable[vars.size()]);
		}
		return fVariables;
	}

	public boolean hasVariables() throws DebugException {
		if (fExpression instanceof VarExp) {
			VarExp varExp = (VarExp) fExpression;
			if (varExp.type instanceof TypeStruct) {
				TypeStruct struct = (TypeStruct) varExp.type;
				return struct.sym.fields != null && !struct.sym.fields.isEmpty();
			}
		} else if (fExpression instanceof StructLiteralExp) {
			StructLiteralExp exp = (StructLiteralExp) fExpression;
			return exp.sd.fields != null && !exp.sd.fields.isEmpty();
		} else if (fExpression instanceof ArrayLiteralExp) {
			ArrayLiteralExp exp = (ArrayLiteralExp) fExpression;
			return exp.elements != null && !exp.elements.isEmpty();
		}
		return false;
	}

	public boolean isAllocated() throws DebugException {
		return true;
	}
	
	public boolean isLazy() {
		return fExpression != null;
	}
	
	public String getDetail() {
		StringBuilder sb = new StringBuilder();
		Utils.appendDetail(fExpression, sb, 0);
		return sb.toString();
	}

}
