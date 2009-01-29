package descent.internal.codeassist;

import java.util.Map;

import descent.core.Complex;
import descent.core.IEvaluationResult;
import descent.core.IJavaProject;
import descent.core.JavaModelException;
import descent.core.WorkingCopyOwner;
import descent.core.dom.AST;
import descent.core.dom.ASTConverter;
import descent.core.dom.CompilationUnit;
import descent.core.dom.CompilationUnitResolver;
import descent.core.dom.CompileTimeASTConverter;
import descent.core.dom.CompilationUnitResolver.ParseResult;
import descent.internal.compiler.env.ICompilationUnit;
import descent.internal.compiler.impl.CompilerOptions;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.AliasDeclaration;
import descent.internal.compiler.parser.ArrayInitializer;
import descent.internal.compiler.parser.ArrayLiteralExp;
import descent.internal.compiler.parser.CallExp;
import descent.internal.compiler.parser.CompileDeclaration;
import descent.internal.compiler.parser.ComplexExp;
import descent.internal.compiler.parser.Declaration;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.EnumDeclaration;
import descent.internal.compiler.parser.EnumMember;
import descent.internal.compiler.parser.ExpInitializer;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Initializer;
import descent.internal.compiler.parser.IntegerExp;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.NegExp;
import descent.internal.compiler.parser.RealExp;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.StringExp;
import descent.internal.compiler.parser.StructInitializer;
import descent.internal.compiler.parser.StructLiteralExp;
import descent.internal.compiler.parser.TemplateInstance;
import descent.internal.compiler.parser.TemplateMixin;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeEnum;
import descent.internal.compiler.parser.VarDeclaration;
import descent.internal.compiler.parser.VarExp;
import descent.internal.compiler.parser.integer_t;
import descent.internal.compiler.parser.real_t;
import descent.internal.compiler.parser.ast.AstVisitorAdapter;
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

	public EvaluationEngine(
			Map settings,
			IJavaProject javaProject,
			WorkingCopyOwner owner) {
		this.javaProject = javaProject;
		this.owner = owner;
		this.settings = settings;
		this.compilerOptions = new CompilerOptions(settings);
	}
	
	public IEvaluationResult evaluate(ICompilationUnit sourceUnit, int offset) {
		this.offset = offset;
		
		try {
			ParseResult parseResult = CompilationUnitResolver.resolve(Util.getApiLevel(this.compilerOptions.getMap()), sourceUnit, javaProject, settings, owner, false, true, null);
			
			context = parseResult.context;
			
			Module module = parseResult.module;
			module.accept(this);
			return result;
		} catch (JavaModelException e) {
			Util.log(e);
			return null;
		}
	}
	
	private void evalMembers(Dsymbols symbols) {
		CompileTimeASTConverter converter = new CompileTimeASTConverter(false, null);
		converter.setAST(AST.newAST(Util.getApiLevel(javaProject)));
		converter.init(javaProject, context, null);
		
		Module module = new Module(null, null);
		module.members = new Dsymbols();
		for(Dsymbol sym : symbols) {
			if (sym instanceof TemplateInstance && !(sym instanceof TemplateMixin)) {
				continue;
			}
			module.members.add(sym);
		}
		
		module.sourceMembers = module.members;
		
		CompilationUnit unit = converter.convert(module, null);
		result = new EvaluationResult(unit, IEvaluationResult.COMPILATION_UNIT);
	}
	
	@Override
	public boolean visit(CompileDeclaration node) {
		// If between the mixin keyword
		if (node.start <= offset && offset + length <= node.start + 5) {
			evalMembers(node.decl);
			return false;
		}
		return true;
	}
	
	@Override
	public boolean visit(VarDeclaration node) {
		if (result == null) {
			if (node.isConst() && isInRange(node.ident)) { 
				evalInit(node.init);
			} else if (isInRange(node.sourceInit)) {
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
			} else if (node.templateInstance != null && node.templateInstance.members != null) {
				evalMembers(node.templateInstance.members);
			} else if (node.resolvedSymbol != null && node.resolvedSymbol instanceof AliasDeclaration) {
				evalAlias((AliasDeclaration) node.resolvedSymbol);
			}
		}
		
		return false;
	}
	
	private void evalAlias(AliasDeclaration alias) {
		//System.out.println(alias);
	}

	private void evalInit(Initializer init) {
		if (init.isVoidInitializer() != null) {
			result = new EvaluationResult(null, IEvaluationResult.VOID);
		} else if (init.isExpInitializer() != null) {
			ExpInitializer expInit = (ExpInitializer) init;
			evalExp(expInit.exp);
		} else if (init.isStructInitializer() != null) {
			StructInitializer structInit = (StructInitializer) init;
			
			String name = structInit.ad.ident.toChars();
			String[] names = new String[structInit.ad.fields.size()]; 
			for (int i = 0; i < names.length; i++) {
				names[i] = structInit.ad.fields.get(i).ident.toChars();
			}
			IEvaluationResult[] values = new IEvaluationResult[structInit.value.size()];
			for (int i = 0; i < values.length; i++) {
				evalInit(structInit.value.get(i));
				values[i] = result;
			}
			
			StructLiteral sl = new StructLiteral(name, names, values);
			result = new EvaluationResult(sl, IEvaluationResult.STRUCT_LITERAL);
			
		} else if (init.isArrayInitializer() != null) {
			ArrayInitializer arrayInit = (ArrayInitializer) init;
			
			IEvaluationResult[] er = new IEvaluationResult[arrayInit.value.size()];
			for (int i = 0; i < er.length; i++) {
				Initializer subInit = arrayInit.value.get(i);
				evalInit(subInit);
				er[i] = result;
			}
			
			result = new EvaluationResult(er, IEvaluationResult.ARRAY);
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
		} else if (exp instanceof VarExp) {
			Declaration decl = ((VarExp) exp).var;
			if (decl instanceof VarDeclaration) {
				VarDeclaration var = (VarDeclaration) decl;
				if (var.isConst()) { 
					evalInit(var.init);
				}
			} else if (decl instanceof FuncDeclaration) {
				Dsymbols symbols = new Dsymbols();
				symbols.add(decl);
				evalMembers(symbols);
			}
		} else if (exp instanceof StructLiteralExp) {
			StructLiteralExp sle = (StructLiteralExp) exp;
			String name = sle.sd.ident.toChars();
			String[] names = new String[sle.sd.fields.size()]; 
			for (int i = 0; i < names.length; i++) {
				names[i] = sle.sd.fields.get(i).ident.toChars();
			}
			IEvaluationResult[] values = new IEvaluationResult[sle.elements.size()];
			for (int i = 0; i < values.length; i++) {
				evalExp(sle.elements.get(i));
				values[i] = result;
			}
			
			StructLiteral sl = new StructLiteral(name, names, values);
			result = new EvaluationResult(sl, IEvaluationResult.STRUCT_LITERAL);
		} else if (exp instanceof ArrayLiteralExp) {
			ArrayLiteralExp ale = (ArrayLiteralExp) exp;
			
			IEvaluationResult[] er = new IEvaluationResult[ale.elements.size()];
			for (int i = 0; i < er.length; i++) {
				evalExp(ale.elements.get(i));
				er[i] = result;
			}
			
			result = new EvaluationResult(er, IEvaluationResult.ARRAY);
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
			EnumDeclaration e = te.sym;
			evalInt(value, e.memtype);
			break;
		}
	}
	
	private void evalReal(RealExp exp) {
		real_t value = (exp).value;
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
