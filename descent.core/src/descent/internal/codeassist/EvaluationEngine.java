package descent.internal.codeassist;

import java.util.Map;

import descent.core.Complex;
import descent.core.IEvaluationResult;
import descent.core.IJavaProject;
import descent.core.JavaModelException;
import descent.core.WorkingCopyOwner;
import descent.core.dom.CompilationUnitResolver;
import descent.core.dom.CompilationUnitResolver.ParseResult;
import descent.internal.compiler.env.ICompilationUnit;
import descent.internal.compiler.impl.CompilerOptions;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.CallExp;
import descent.internal.compiler.parser.ComplexExp;
import descent.internal.compiler.parser.EnumMember;
import descent.internal.compiler.parser.ExpInitializer;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.IEnumDeclaration;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Initializer;
import descent.internal.compiler.parser.IntegerExp;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.NegExp;
import descent.internal.compiler.parser.RealExp;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.StringExp;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeEnum;
import descent.internal.compiler.parser.VarDeclaration;
import descent.internal.compiler.parser.integer_t;
import descent.internal.compiler.parser.real_t;
import descent.internal.compiler.parser.ast.AstVisitorAdapter;
import descent.internal.core.JavaElementFinder;
import descent.internal.core.util.Util;

/*
 * For now, don't take the JDT approach: let's parse, visit and see where
 * if the node falls between the given ranges.
 */
public class EvaluationEngine extends AstVisitorAdapter {
	
	IJavaProject javaProject;
	WorkingCopyOwner owner;
	Map settings;
	CompilerOptions compilerOptions;
	SemanticContext context;
	
	int offset;
	int length;
	IEvaluationResult result;
	
	JavaElementFinder finder;

	public EvaluationEngine(
			Map settings,
			IJavaProject javaProject,
			WorkingCopyOwner owner) {
		this.javaProject = javaProject;
		this.owner = owner;
		this.settings = settings;
		this.compilerOptions = new CompilerOptions(settings);
		this.finder = new JavaElementFinder(javaProject, owner);
	}
	
	public IEvaluationResult evaluate(ICompilationUnit sourceUnit, int offset) {
		this.offset = offset;
		
		try {
			ParseResult parseResult = CompilationUnitResolver.resolve(Util.getApiLevel(this.compilerOptions.getMap()), sourceUnit, javaProject, settings, owner, true, null);
			
			context = parseResult.context;
			
			Module module = parseResult.module;
			module.accept(this);
			return result;
		} catch (JavaModelException e) {
			Util.log(e);
			return null;
		}
	}
	
	@Override
	public boolean visit(VarDeclaration node) {
		if (result == null) {
			if (node.isConst() && isInRange(node.ident)) { 
				evalInit((Initializer) node.init);
			} else if (isInRange((Initializer) node.sourceInit)) {
				node.sourceInit.accept(this);
			}
			return false;
		}
		return true;
	}
	
	@Override
	public boolean visit(EnumMember node) {
		if (result == null && isInRange(node.ident)) {
			evalExp(node.value);
		}
		return false;
	}
	
	@Override
	public boolean visit(CallExp node) {
		if (result == null && isInRange(node.sourceE1)) {
			Expression exp = node.optimize(ASTDmdNode.WANTvalue | ASTDmdNode.WANTinterpret, context);
			evalExp(exp);
		}
		return true;
	}
	
	@Override
	public boolean visit(IdentifierExp node) {
		if (result == null && isInRange(node)) {
			if (node.evaluatedExpression != null) {
				evalExp(node.evaluatedExpression);
			} else if (node.resolvedExpression != null) {
				evalExp(node.resolvedExpression);
			}
		}
		
		return false;
	}
	
	private void evalInit(Initializer init) {
		if (init.isVoidInitializer() != null) {
			result = new EvaluationResult(null, IEvaluationResult.VOID);
		} else if (init.isExpInitializer() != null) {
			ExpInitializer expInit = (ExpInitializer) init;
			evalExp((Expression) expInit.exp);
		}
	}
	
	private void evalExp(Expression exp) {
		if (exp instanceof IntegerExp) {
			evalInt((IntegerExp) exp);
		} else if (exp instanceof RealExp) {
			evalReal((RealExp) exp);
		} else if (exp instanceof ComplexExp) {
			evalComplex((ComplexExp) exp);
		} else if (exp instanceof StringExp) {
			evalString((StringExp) exp);
		} else if (exp instanceof NegExp) {
			// evalExp(((NegExp) exp).e1);
		}
	}
	
	private void evalInt(IntegerExp exp) {
		evalInt(exp.value, exp.type);
	}

	private void evalInt(integer_t value, Type type) {
		switch(type.ty) {
		case Tbool:
			result = new EvaluationResult(value.isTrue(), IEvaluationResult.BOOL);
			break;
		case Tchar:
			result = new EvaluationResult(new Character((char) value.intValue()), IEvaluationResult.CHAR);
			break;
		case Tdchar:
			result = new EvaluationResult(new Character((char) value.intValue()), IEvaluationResult.DCHAR);
			break;
		case Twchar:
			result = new EvaluationResult(new Character((char) value.intValue()), IEvaluationResult.WCHAR);
			break;
		case Tint8:
			result = new EvaluationResult(value.byteValue(), IEvaluationResult.BYTE);
			break;
		case Tuns8:
			result = new EvaluationResult(value.shortValue(), IEvaluationResult.UBYTE);
			break;
		case Tint16:
			result = new EvaluationResult(value.shortValue(), IEvaluationResult.SHORT);
			break;
		case Tuns16:
			result = new EvaluationResult(value.intValue(), IEvaluationResult.USHORT);
			break;
		case Tint32:
			result = new EvaluationResult(value.intValue(), IEvaluationResult.INT);
			break;
		case Tuns32:
			result = new EvaluationResult(value.longValue(), IEvaluationResult.UINT);
			break;
		case Tint64:
			result = new EvaluationResult(value.longValue(), IEvaluationResult.LONG);
			break;
		case Tuns64:
			result = new EvaluationResult(value.bigIntegerValue(), IEvaluationResult.ULONG);
			break;
		case Tenum:
			TypeEnum te = (TypeEnum) type;
			IEnumDeclaration e = te.sym;
			evalInt(value, e.memtype());
			break;
		}
	}
	
	private void evalReal(RealExp exp) {
		real_t value = ((RealExp) exp).value;
		switch(exp.type.ty) {
		case Tfloat32:
			if (value.isNaN()) {
				result = new EvaluationResult(Float.NaN, IEvaluationResult.FLOAT);
			} else if (value.isPositiveInfinity()) {
				result = new EvaluationResult(Float.POSITIVE_INFINITY, IEvaluationResult.FLOAT);
			} else if (value.isNegativeInfinity()) {
				result = new EvaluationResult(Float.NEGATIVE_INFINITY, IEvaluationResult.FLOAT);
			} else {
				result = new EvaluationResult(value.floatValue(), IEvaluationResult.FLOAT);
			}
			break;
		case Tfloat64:
			if (value.isNaN()) {
				result = new EvaluationResult(Double.NaN, IEvaluationResult.DOUBLE);
			} else if (value.isPositiveInfinity()) {
				result = new EvaluationResult(Double.POSITIVE_INFINITY, IEvaluationResult.DOUBLE);
			} else if (value.isNegativeInfinity()) {
				result = new EvaluationResult(Double.NEGATIVE_INFINITY, IEvaluationResult.DOUBLE);
			} else {
				result = new EvaluationResult(value.doubleValue(), IEvaluationResult.DOUBLE);
			}
			break;
		case Tfloat80:
			if (value.isNaN()) {
				result = new EvaluationResult(Double.NaN, IEvaluationResult.REAL);
			} else if (value.isPositiveInfinity()) {
				result = new EvaluationResult(Double.POSITIVE_INFINITY, IEvaluationResult.REAL);
			} else if (value.isNegativeInfinity()) {
				result = new EvaluationResult(Double.NEGATIVE_INFINITY, IEvaluationResult.REAL);
			} else {
				result = new EvaluationResult(value.bigDecimalValue(), IEvaluationResult.REAL);
			}
			break;
		}
	}
	
	private void evalComplex(ComplexExp exp) {
		Complex c = new Complex(exp.value.re.bigDecimalValue(), exp.value.im.bigDecimalValue());
		switch(exp.type.ty) {
		case Timaginary32:
			result = new EvaluationResult(c, IEvaluationResult.IFLOAT);
			break;
		case Timaginary64:
			result = new EvaluationResult(c, IEvaluationResult.IDOUBLE);
			break;
		case Timaginary80:
			result = new EvaluationResult(c, IEvaluationResult.IREAL);
			break;
		case Tcomplex32:
			result = new EvaluationResult(c, IEvaluationResult.CFLOAT);
			break;
		case Tcomplex64:
			result = new EvaluationResult(c, IEvaluationResult.CDOUBLE);
			break;
		case Tcomplex80:
			result = new EvaluationResult(c, IEvaluationResult.CREAL);
			break;
		}
	}
	
	private void evalString(StringExp exp) {
		String s = new String(exp.string); 
		Type next = exp.type.next;
		if (next == null) {
			return;
		}
		
		switch(next.ty) {
		case Tchar:
			result = new EvaluationResult(s, IEvaluationResult.CHAR_ARRAY);
			break;
		case Tdchar:
			result =  new EvaluationResult(s, IEvaluationResult.DCHAR_ARRAY);
			break;
		case Twchar:
			result = new EvaluationResult(s, IEvaluationResult.WCHAR_ARRAY);
			break;
		}
	}
	
	private boolean isInRange(IdentifierExp ident) {
		if (ident == null) {
			return false;
		}
		if (ident.ident.length == 0) {
			return false;
		}
		
		return ident.start <= offset && offset + length <= ident.start + ident.length;
	}
	
	private boolean isInRange(ASTDmdNode node) {
		if (node == null) {
			return false;
		}
		return node.start <= offset && offset + length <= node.start + node.length;
	}

}
