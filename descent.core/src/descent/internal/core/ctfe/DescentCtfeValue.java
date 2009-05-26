package descent.internal.core.ctfe;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

import descent.internal.compiler.parser.ArrayLiteralExp;
import descent.internal.compiler.parser.ComplexExp;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.IntegerExp;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.NullExp;
import descent.internal.compiler.parser.RealExp;
import descent.internal.compiler.parser.StructLiteralExp;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeAArray;
import descent.internal.compiler.parser.TypeDArray;
import descent.internal.compiler.parser.TypeStruct;
import descent.internal.compiler.parser.VarDeclaration;
import descent.internal.compiler.parser.VarExp;
import descent.internal.compiler.parser.complex_t;
import descent.internal.compiler.parser.real_t;

public class DescentCtfeValue extends DescentCtfeDebugElement implements IValue {

	private final String fName;
	private final Expression fExpression;
	private final CtfeDebugger fDebugger;
	private IVariable[] fVariables;
	private final int fStackFrame;	

	public DescentCtfeValue(IDebugTarget target, CtfeDebugger debugger, int stackFrame, String name, Expression value) {
		super(target);
		this.fDebugger = debugger;
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
			return getTypeName(varExp.type);
		} else if (fExpression instanceof StructLiteralExp) {
			StructLiteralExp exp = (StructLiteralExp) fExpression;
			return exp.sd.ident.toString();
		} else if (fExpression instanceof ArrayLiteralExp) {
			ArrayLiteralExp exp = (ArrayLiteralExp) fExpression;
			return getTypeName(exp.elements.get(0).type) + "[]";
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
						vars.add(fDebugger.newVariable(fStackFrame, var.ident.toString(), getDefaultValue(var.type)));
					}
				}
			} else if (fExpression instanceof StructLiteralExp) {
				StructLiteralExp exp = (StructLiteralExp) fExpression;
				for (int i = 0; i < exp.sd.fields.size(); i++) {
					vars.add(fDebugger.newVariable(fStackFrame, exp.sd.fields.get(i).ident.toString(), exp.elements.get(i)));
				}
			} else if (fExpression instanceof ArrayLiteralExp) {
				ArrayLiteralExp exp = (ArrayLiteralExp) fExpression;
				for (int i = 0; i < exp.elements.size(); i++) {
					vars.add(fDebugger.newVariable(fStackFrame, "[" + String.valueOf(i) + "]", exp.elements.get(i)));
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
	
	private Expression getDefaultValue(Type type) {
		switch(type.ty) {
		case Tbit:
		case Tbool:
		case Tchar:
		case Tdchar:
		case Twchar:
		case Tenum:
		case Tint16:
		case Tint32:
		case Tint64:
		case Tint8:
		case Tuns16:
		case Tuns32:
		case Tuns64:
		case Tuns8:
			return new IntegerExp(Loc.ZERO, 0, type);
		case Tfloat32:
		case Tfloat64:
		case Tfloat80:
			return new RealExp(Loc.ZERO, real_t.ZERO, type);
		case Tcomplex32:
		case Tcomplex64:
		case Tcomplex80:
		case Timaginary32:
		case Timaginary64:
		case Timaginary80:
			return new ComplexExp(Loc.ZERO, complex_t.ZERO, type);
		case Tarray:
		case Taarray:
		case Tsarray:
			return new NullExp(Loc.ZERO);
		}
		return new IntegerExp(Loc.ZERO, 0, type);
	}
	
	private String getTypeName(Type type) {
		if (type instanceof TypeAArray) {
			TypeAArray ta = (TypeAArray) type;
			return getTypeName(ta.next) + "[" + getTypeName(ta.index) +"]";
		} else if (type instanceof TypeDArray) {
			TypeDArray ta = (TypeDArray) type;
			return getTypeName(ta.next) + "[]";
		}
		if (type instanceof TypeStruct) {
			TypeStruct ts = (TypeStruct) type;
			return ts.sym.ident.toString();
		} else {
			return type.toString();
		}
	}
	
	public String getDetail() {
		StringBuilder sb = new StringBuilder();
		appendDetail(fExpression, sb, 0);
		return sb.toString();
	}
	
	private void appendDetail(Expression expression, StringBuilder sb, int ident) {
		if (expression instanceof VarExp) {
			VarExp varExp = (VarExp) expression;
			if (varExp.type instanceof TypeStruct) {
				TypeStruct struct = (TypeStruct) varExp.type;
				
				appendIdent(sb, 0);
				sb.append("struct ");
				sb.append(struct.sym.ident.toString());
				sb.append(" {");
				sb.append("\n");
				for (int i = 0; i < struct.sym.fields.size(); i++) {
					appendIdent(sb, ident + 1);
					sb.append(struct.sym.fields.get(i).ident.toString());
					sb.append(" = ");
					appendDetail(getDefaultValue(struct.sym.fields.get(0).type), sb, ident + 1);
					if (i != struct.sym.fields.size() - 1) {
						sb.append(",");
					}
					sb.append("\n");
				}
				sb.append("}");
				return;
			}
		} else if (expression instanceof StructLiteralExp) {
			StructLiteralExp exp = (StructLiteralExp) expression;
			appendIdent(sb, 0);
			sb.append("struct ");
			sb.append(exp.sd.ident.toString());
			sb.append(" {");
			sb.append("\n");
			for (int i = 0; i < exp.sd.fields.size(); i++) {
				appendIdent(sb, ident + 1);
				sb.append(exp.sd.fields.get(i).ident.toString());
				sb.append(" = ");
				appendDetail(exp.elements.get(i), sb, ident + 1);
				if (i != exp.sd.fields.size() - 1) {
					sb.append(",");
				}
				sb.append("\n");				
			}
			sb.append("}");
			return;
		} else if (expression instanceof ArrayLiteralExp) {
			ArrayLiteralExp exp = (ArrayLiteralExp) expression;
			sb.append("[");
			for (int i = 0; i < exp.elements.size(); i++) {
				if (i != 0) {
					sb.append(", ");
				}
				appendDetail(exp.elements.get(i), sb, ident);
			}
			sb.append("]");
			return;
		}
		
		sb.append(expression.toString());
	}
	
	private void appendIdent(StringBuilder sb, int ident) {
		for (int i = 0; i < ident; i++) {
			sb.append("  ");
		}
	}

}
