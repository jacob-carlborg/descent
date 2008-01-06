package descent.internal.codeassist;

import java.util.Map;

import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.JavaModelException;
import descent.core.WorkingCopyOwner;
import descent.core.dom.CompilationUnitResolver;
import descent.core.dom.Complex;
import descent.core.dom.Void;
import descent.core.dom.CompilationUnitResolver.ParseResult;
import descent.internal.compiler.env.ICompilationUnit;
import descent.internal.compiler.impl.CompilerOptions;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.CallExp;
import descent.internal.compiler.parser.ComplexExp;
import descent.internal.compiler.parser.DotIdExp;
import descent.internal.compiler.parser.ExpInitializer;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Initializer;
import descent.internal.compiler.parser.IntegerExp;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.RealExp;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.StringExp;
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
	
	private final static IJavaElement[] NO_ELEMENTS = new IJavaElement[0];
	
	IJavaProject javaProject;
	WorkingCopyOwner owner;
	Map settings;
	CompilerOptions compilerOptions;
	SemanticContext context;
	
	int offset;
	int length;
	Object result;
	
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
	
	public Object evaluate(ICompilationUnit sourceUnit, int offset) {
		this.offset = offset;
		
		try {
			ParseResult parseResult = CompilationUnitResolver.resolve(Util.getApiLevel(this.compilerOptions.getMap()), sourceUnit, javaProject, settings, owner, true, null);
			
			context = parseResult.context;
			
			Module module = parseResult.module;
			module.accept(this);
			return result;
		} catch (JavaModelException e) {
			Util.log(e);
			return NO_ELEMENTS;
		}
	}
	
	@Override
	public boolean visit(VarDeclaration node) {
		if (result == null && node.isConst() && isInRange(node.ident)) {
			evalInit((Initializer) node.init);
			return false;
		}
		return true;
	}
	
	@Override
	public boolean visit(CallExp node) {
		if (isInRange(node.sourceE1)) {
			Expression exp = node.optimize(ASTDmdNode.WANTvalue | ASTDmdNode.WANTinterpret, context);
			evalExp(exp);
			return false;
		}
		return true;
	}
	
	@Override
	public boolean visit(IdentifierExp node) {
		if (isInRange(node)) {
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
			result = Void.getInstance();
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
		}
	}
	
	private void evalInt(IntegerExp exp) {
		integer_t value = ((IntegerExp) exp).value;
		switch(exp.type.ty) {
		case Tbool:
			result = value.isTrue();
			break;
		case Tchar:
		case Tdchar:
		case Twchar:
			result = new Character((char) value.intValue());
			break;
		case Tint8:
			result = value.byteValue();
			break;
		case Tuns8:
		case Tint16:
			result = value.shortValue();
			break;
		case Tuns16:
		case Tint32:
			result = value.intValue();
			break;
		case Tuns32:
		case Tint64:
		case Tuns64:
			result = value.bigIntegerValue();
			break;
		}
	}
	
	private void evalReal(RealExp exp) {
		real_t value = ((RealExp) exp).value;
		switch(exp.type.ty) {
		case Tfloat32:
			if (value.isNaN()) {
				result = Float.NaN;
			} else if (value.isPositiveInfinity()) {
				result = Float.POSITIVE_INFINITY;
			} else if (value.isNegativeInfinity()) {
				result = Float.NEGATIVE_INFINITY;
			} else {
				result = value.floatValue();
			}
			break;
		case Tfloat64:
			if (value.isNaN()) {
				result = Double.NaN;
			} else if (value.isPositiveInfinity()) {
				result = Double.POSITIVE_INFINITY;
			} else if (value.isNegativeInfinity()) {
				result = Double.NEGATIVE_INFINITY;
			} else {
				result = value.doubleValue();
			}
			break;
		case Tfloat80:
			if (value.isNaN()) {
				result = Double.NaN;
			} else if (value.isPositiveInfinity()) {
				result = Double.POSITIVE_INFINITY;
			} else if (value.isNegativeInfinity()) {
				result = Double.NEGATIVE_INFINITY;
			} else {
				result = value.bigDecimalValue();
			}
			break;
		}
	}
	
	private void evalComplex(ComplexExp exp) {
		result = new Complex(exp.value.re.bigDecimalValue(), exp.value.im.bigDecimalValue());
	}
	
	private void evalString(StringExp exp) {
		result = new String(exp.string);
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
		return node.start <= offset && offset + length <= node.start + node.length;
	}

}
