package descent.internal.core;

import descent.core.Complex;
import descent.core.IEvaluationResult;
import descent.core.IJavaProject;
import descent.core.dom.AST;
import descent.core.dom.CompilationUnit;
import descent.core.dom.CompileTimeASTConverter;
import descent.internal.codeassist.EvaluationResult;
import descent.internal.codeassist.StructLiteral;
import descent.internal.compiler.parser.AliasDeclaration;
import descent.internal.compiler.parser.ArrayInitializer;
import descent.internal.compiler.parser.ArrayLiteralExp;
import descent.internal.compiler.parser.ComplexExp;
import descent.internal.compiler.parser.Declaration;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.EnumDeclaration;
import descent.internal.compiler.parser.ExpInitializer;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.FuncDeclaration;
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
import descent.internal.core.util.Util;

public class Evaluator {
	
	private final IJavaProject javaProject;
	private final SemanticContext context;

	public Evaluator(IJavaProject javaProject, SemanticContext context) {
		this.javaProject = javaProject;
		this.context = context;
	}
	
	public IEvaluationResult evalExp(Expression exp) {
		if (exp instanceof IntegerExp) {
			return evalInt((IntegerExp) exp);
		} else if (exp instanceof RealExp) {
			return evalReal((RealExp) exp);
		} else if (exp instanceof ComplexExp) {
			return evalComplex((ComplexExp) exp);
		} else if (exp instanceof StringExp) {
			return evalString((StringExp) exp);
		} else if (exp instanceof NegExp) {
			// evalExp(((NegExp) exp).e1);
		} else if (exp instanceof VarExp) {
			Declaration decl = ((VarExp) exp).var;
			if (decl instanceof VarDeclaration) {
				VarDeclaration var = (VarDeclaration) decl;
				if (var.isConst()) { 
					return evalInit(var.init);
				}
			} else if (decl instanceof FuncDeclaration) {
				Dsymbols symbols = new Dsymbols();
				symbols.add(decl);
				return evalMembers(symbols);
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
				values[i] = evalExp(sle.elements.get(i));
			}
			
			StructLiteral sl = new StructLiteral(name, names, values);
			return new EvaluationResult(sl, IEvaluationResult.STRUCT_LITERAL);
		} else if (exp instanceof ArrayLiteralExp) {
			ArrayLiteralExp ale = (ArrayLiteralExp) exp;
			
			IEvaluationResult[] er = new IEvaluationResult[ale.elements.size()];
			for (int i = 0; i < er.length; i++) {
				er[i] = evalExp(ale.elements.get(i));
			}
			
			return new EvaluationResult(er, IEvaluationResult.ARRAY);
		} 
		// TODO see how to evaluate tuples
//		else if (exp instanceof TupleExp) {
//			TupleExp tuple = (TupleExp) exp;
//			
//			IEvaluationResult[] er = new IEvaluationResult[tuple.exps.size()];
//			for (int i = 0; i < tuple.exps.size(); i++) {
//				evalExp(tuple.exps.get(i));
//				er[i] = result;
//			}
//			
//			result = new EvaluationResult(er, IEvaluationResult.TUPLE);
//		}
		
		return null;
	}
	
	public IEvaluationResult evalAlias(AliasDeclaration alias) {
		Dsymbols symbols = new Dsymbols();
		symbols.add(alias);
		return evalMembers(symbols);
	}

	public IEvaluationResult evalInit(Initializer init) {
		if (init.isVoidInitializer() != null) {
			return new EvaluationResult(null, IEvaluationResult.VOID);
		} else if (init.isExpInitializer() != null) {
			ExpInitializer expInit = (ExpInitializer) init;
			return evalExp(expInit.exp);
		} else if (init.isStructInitializer() != null) {
			StructInitializer structInit = (StructInitializer) init;
			
			String name = structInit.ad.ident.toChars();
			String[] names = new String[structInit.ad.fields.size()]; 
			for (int i = 0; i < names.length; i++) {
				names[i] = structInit.ad.fields.get(i).ident.toChars();
			}
			IEvaluationResult[] values = new IEvaluationResult[structInit.value.size()];
			for (int i = 0; i < values.length; i++) {
				values[i] = evalInit(structInit.value.get(i));
			}
			
			StructLiteral sl = new StructLiteral(name, names, values);
			return new EvaluationResult(sl, IEvaluationResult.STRUCT_LITERAL);
		} else if (init.isArrayInitializer() != null) {
			ArrayInitializer arrayInit = (ArrayInitializer) init;
			
			IEvaluationResult[] er = new IEvaluationResult[arrayInit.value.size()];
			for (int i = 0; i < er.length; i++) {
				Initializer subInit = arrayInit.value.get(i);
				er[i] = evalInit(subInit);
			}
			
			return new EvaluationResult(er, IEvaluationResult.ARRAY);
		}
		
		return null;
	}
	
	public IEvaluationResult evalInt(integer_t value, Type type) {
		switch(type.ty) {
		case Tbool:
			return new EvaluationResult(value.isTrue(), IEvaluationResult.BOOL);
		case Tchar:
			return new EvaluationResult(new Character((char) value.intValue()), IEvaluationResult.CHAR);
		case Tdchar:
			return new EvaluationResult(new Character((char) value.intValue()), IEvaluationResult.DCHAR);
		case Twchar:
			return new EvaluationResult(new Character((char) value.intValue()), IEvaluationResult.WCHAR);
		case Tint8:
			return new EvaluationResult(value.byteValue(), IEvaluationResult.BYTE);
		case Tuns8:
			return new EvaluationResult(value.shortValue(), IEvaluationResult.UBYTE);
		case Tint16:
			return new EvaluationResult(value.shortValue(), IEvaluationResult.SHORT);
		case Tuns16:
			return new EvaluationResult(value.intValue(), IEvaluationResult.USHORT);
		case Tint32:
			return new EvaluationResult(value.intValue(), IEvaluationResult.INT);
		case Tuns32:
			return new EvaluationResult(value.longValue(), IEvaluationResult.UINT);
		case Tint64:
			return new EvaluationResult(value.longValue(), IEvaluationResult.LONG);
		case Tuns64:
			return new EvaluationResult(value.bigIntegerValue(), IEvaluationResult.ULONG);
		case Tenum:
			TypeEnum te = (TypeEnum) type;
			EnumDeclaration e = te.sym;
			return evalInt(value, e.memtype);
		default:
			return null;
		}
	}
	
	public IEvaluationResult evalInt(IntegerExp exp) {
		return evalInt(exp.value, exp.type);
	}
	
	public IEvaluationResult evalReal(RealExp exp) {
		real_t value = (exp).value;
		switch(exp.type.ty) {
		case Tfloat32:
			if (value.isNaN()) {
				return new EvaluationResult(Float.NaN, IEvaluationResult.FLOAT);
			} else if (value.isPositiveInfinity()) {
				return new EvaluationResult(Float.POSITIVE_INFINITY, IEvaluationResult.FLOAT);
			} else if (value.isNegativeInfinity()) {
				return new EvaluationResult(Float.NEGATIVE_INFINITY, IEvaluationResult.FLOAT);
			} else {
				return new EvaluationResult(value.floatValue(), IEvaluationResult.FLOAT);
			}
		case Tfloat64:
			if (value.isNaN()) {
				return new EvaluationResult(Double.NaN, IEvaluationResult.DOUBLE);
			} else if (value.isPositiveInfinity()) {
				return new EvaluationResult(Double.POSITIVE_INFINITY, IEvaluationResult.DOUBLE);
			} else if (value.isNegativeInfinity()) {
				return new EvaluationResult(Double.NEGATIVE_INFINITY, IEvaluationResult.DOUBLE);
			} else {
				return new EvaluationResult(value.doubleValue(), IEvaluationResult.DOUBLE);
			}
		case Tfloat80:
			if (value.isNaN()) {
				return new EvaluationResult(Double.NaN, IEvaluationResult.REAL);
			} else if (value.isPositiveInfinity()) {
				return new EvaluationResult(Double.POSITIVE_INFINITY, IEvaluationResult.REAL);
			} else if (value.isNegativeInfinity()) {
				return new EvaluationResult(Double.NEGATIVE_INFINITY, IEvaluationResult.REAL);
			} else {
				return new EvaluationResult(value.bigDecimalValue(), IEvaluationResult.REAL);
			}
		default:
			return null;
		}
	}
	
	public IEvaluationResult evalComplex(ComplexExp exp) {
		Complex c = new Complex(exp.value.re.bigDecimalValue(), exp.value.im.bigDecimalValue());
		switch(exp.type.ty) {
		case Timaginary32:
			return new EvaluationResult(c, IEvaluationResult.IFLOAT);
		case Timaginary64:
			return new EvaluationResult(c, IEvaluationResult.IDOUBLE);
		case Timaginary80:
			return new EvaluationResult(c, IEvaluationResult.IREAL);
		case Tcomplex32:
			return new EvaluationResult(c, IEvaluationResult.CFLOAT);
		case Tcomplex64:
			return new EvaluationResult(c, IEvaluationResult.CDOUBLE);
		case Tcomplex80:
			return new EvaluationResult(c, IEvaluationResult.CREAL);
		default:
			return null;
		}
	}
	
	public IEvaluationResult evalString(StringExp exp) {
		String s = new String(exp.string); 
		Type next = exp.type.nextOf();
		if (next == null) {
			return null;
		}
		
		switch(next.ty) {
		case Tchar:
			return new EvaluationResult(s, IEvaluationResult.CHAR_ARRAY);
		case Tdchar:
			return new EvaluationResult(s, IEvaluationResult.DCHAR_ARRAY);
		case Twchar:
			return new EvaluationResult(s, IEvaluationResult.WCHAR_ARRAY);
		default:
			return null;
		}
	}
	
	public IEvaluationResult evalMembers(Dsymbols symbols) {
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
		return new EvaluationResult(unit, IEvaluationResult.COMPILATION_UNIT);
	}

}
