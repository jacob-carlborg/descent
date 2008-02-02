package descent.internal.compiler.lookup;

import java.util.List;

import descent.core.Flags;
import descent.core.IMethod;
import descent.core.ISourceReference;
import descent.core.JavaModelException;
import descent.core.compiler.CharOperation;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.Arguments;
import descent.internal.compiler.parser.ComplexExp;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.Expressions;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.IAggregateDeclaration;
import descent.internal.compiler.parser.IDeclaration;
import descent.internal.compiler.parser.IDsymbol;
import descent.internal.compiler.parser.IFuncDeclaration;
import descent.internal.compiler.parser.IScopeDsymbol;
import descent.internal.compiler.parser.ISignatureConstants;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.InlineScanState;
import descent.internal.compiler.parser.IntegerExp;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.NullExp;
import descent.internal.compiler.parser.RealExp;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.SemanticMixin;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeFunction;
import descent.internal.compiler.parser.VarDeclaration;
import descent.internal.compiler.parser.complex_t;
import descent.internal.core.SourceMethod;
import descent.internal.core.SourceMethodElementInfo;
import descent.internal.core.util.Util;

import static descent.internal.compiler.parser.STC.STCstatic;

public class RFuncDeclaration extends RDeclaration implements IFuncDeclaration {
	
	private Type type;
	private FuncDeclaration func; // The underlying function, only for interpreting

	public RFuncDeclaration(IMethod element, SemanticContext context) {
		super(element, context);
	}

	public boolean canInline(boolean hasthis, boolean hdrscan, SemanticContext context) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean canInline(boolean hasthis, SemanticContext context) {
		// TODO Auto-generated method stub
		return false;
	}

	public Expression doInline(InlineScanState iss, Expression ethis, List arguments, SemanticContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean inferRetType() {
		// TODO Auto-generated method stub
		return false;
	}

	public Expression interpret(InterState istate, Expressions arguments, SemanticContext context) {
		if (func == null) {
			func = (FuncDeclaration) ((RModule) getModule()).materialize((ISourceReference) element);
		}
		return func.interpret(istate, arguments, context);
	}
	
	public boolean isVirtual(SemanticContext context) {
		return SemanticMixin.isVirtual(this, context);
	}

	public IAggregateDeclaration isMember2() {
		IAggregateDeclaration ad;

		ad = null;
		for (IDsymbol s = this; s != null; s = s.parent()) {
			ad = s.isMember();
			if (ad != null) {
				break;
			}
			if (s.parent() == null || s.parent().isTemplateInstance() == null) {
				break;
			}
		}
		return ad;
	}

	public boolean isNested() {
		return ((storage_class() & STCstatic) == 0)
			&& (toParent2().isFuncDeclaration() != null);
	}
	
	@Override
	public IAggregateDeclaration isThis() {
		return SemanticMixin.isThis(this);
	}

	public void nestedFrameRef(boolean nestedFrameRef) {
		// TODO Auto-generated method stub
		
	}

	public IFuncDeclaration overloadExactMatch(Type t, SemanticContext context) {
		return SemanticMixin.overloadExactMatch(this, t, context);
	}

	public IFuncDeclaration overloadResolve(Expressions arguments, SemanticContext context, ASTDmdNode caller) {
		return SemanticMixin.overloadResolve(this, arguments, context, caller);
	}

	public IDeclaration overnext() {
		boolean foundMe = false;
		
		IScopeDsymbol parentS = (IScopeDsymbol) parent;
		for(IDsymbol s : parentS.members()) {
			if (s == this) {
				foundMe = true;
			} else if (foundMe && s.isFuncDeclaration() != null &&
					CharOperation.equals(s.ident().ident, ident().ident)) {
				IDeclaration d = s.isDeclaration();
				if (d != null) {
					return d;
				}
			}
		}
		
		return null;
	}

	public boolean overrides(IFuncDeclaration fd, SemanticContext context) {
		return SemanticMixin.overrides(this, fd, context);
	}

	public Type tintro() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Type getType() {
		// FuncDeclaration returns null
		return null;
	}
	
	@Override
	public Type type() {
		if (type == null) {
			try {
				IMethod method = (IMethod) element;
				SourceMethodElementInfo info = (SourceMethodElementInfo) ((SourceMethod) method).getElementInfo();
				int defaultValuesCount = info.getDefaultValuesCount();

				String retTypeSig = method.getReturnType();
				
				Type retType;
				
				if (isCtorDeclaration() != null) {
					// For a constructor, the return type is the
					// type of its class
					retType = parent().type();
				} else {
					retType = getTypeFromSignature(retTypeSig);
				}
				
				Arguments args = new Arguments();
				
				String[] paramNames = method.getParameterNames();
				String[] paramTypesSig = method.getParameterTypes();
				
				for(int i = 0; i < paramNames.length; i++) {
					Type argType = getTypeFromSignature(paramTypesSig[i]);
					// TODO storage class and default value
					// Observation: the default value doesn't matter. It only
					// matters if it's null or not.
					args.add(new Argument(
							0, // storage class
							argType, // type 
							new IdentifierExp(paramNames[i].toCharArray()), // name 
							paramNames.length - i <= defaultValuesCount ? defaultValue(argType) : null // default value
							));
				}
				
				// TODO link
				type = new TypeFunction(args, retType, (getFlags() & Flags.AccVarargs) == 0 ? 0 : 1, LINK.LINKd);
				((TypeFunction) type).linkageChar = 'F';
				type = type.merge(context);
			} catch (JavaModelException e) {
				Util.log(e);
			}
		}
		return type;
	}

	private Expression defaultValue(Type argType) {
		if (argType == null) {
			return null;	
		}
		
		switch(argType.ty) {
		case Tstruct:
			// TODO Descent what can be the default value of a struct?
			return null;
		case Tbit:
		case Tbool:
		case Tchar:
		case Tdchar:
		case Twchar:
		case Tint8:
		case Tuns8:
		case Tint16:
		case Tuns16:
		case Tint32:
		case Tuns32:
		case Tint64:
		case Tuns64:
		case Tenum:
			return new IntegerExp(0);
			
		case Tfloat32:
		case Tfloat64:
		case Tfloat80:
			return new RealExp(Loc.ZERO, 0, argType);
			
		case Tcomplex32:
		case Tcomplex64:
		case Tcomplex80:
		case Timaginary32:
		case Timaginary64:
		case Timaginary80:
			return new ComplexExp(Loc.ZERO, complex_t.ZERO, argType);
		
		case Taarray:
		case Tarray:
		case Tsarray:
		case Tdelegate:
		case Tclass:
		case Tfunction:
		case Tinstance:
		case Tpointer:
		case Treference:
			return new NullExp(Loc.ZERO);

		default:
			return null;
		}
	}

	public VarDeclaration vthis() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public IFuncDeclaration isFuncDeclaration() {
		return this;
	}
	
	public char getSignaturePrefix() {
		return ISignatureConstants.FUNCTION;
	}
	
	public String getFunctionSignature() {
		StringBuilder sb = new StringBuilder();
		SemanticMixin.appendNameSignature(this, sb);
		return sb.toString();
	}
	
	@Override
	public String kind() {
		return "function";
	}

}
