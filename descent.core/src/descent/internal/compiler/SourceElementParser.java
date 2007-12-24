/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package descent.internal.compiler;

import java.util.List;
import java.util.Stack;

import descent.core.Flags;
import descent.core.ICompilationUnit;
import descent.core.JavaModelException;
import descent.core.compiler.CharOperation;
import descent.core.dom.AST;
import descent.core.dom.CompilationUnitResolver;
import descent.internal.compiler.ISourceElementRequestor.FieldInfo;
import descent.internal.compiler.ISourceElementRequestor.MethodInfo;
import descent.internal.compiler.ISourceElementRequestor.TypeInfo;
import descent.internal.compiler.ISourceElementRequestor.TypeParameterInfo;
import descent.internal.compiler.impl.CompilerOptions;
import descent.internal.compiler.parser.*;
import descent.internal.compiler.parser.Package;
import descent.internal.compiler.parser.ast.ASTNode;
import descent.internal.compiler.parser.ast.AstVisitorAdapter;
import descent.internal.compiler.parser.ast.NaiveASTFlattener;
import descent.internal.core.util.Util;

/**
 * A source element parser extracts structural and reference information
 * from a piece of source.
 *
 * also see @ISourceElementRequestor
 *
 * The structural investigation includes:
 * - the package statement
 * - import statements
 * - top-level types: package member, member types (member types of member types...)
 * - fields
 * - methods
 *
 * If reference information is requested, then all source constructs are
 * investigated and type, field & method references are provided as well.
 *
 * Any (parsing) problem encountered is also provided.
 */
public class SourceElementParser extends AstVisitorAdapter {
	
	private final static long[] NO_LONG = new long[0];
	private final static char[] DUMMY_TYPE = new char[] { 'i', 'n', 't' };
	
	public ISourceElementRequestor requestor;
	private Module module;
	private boolean foundType = false;
	private CompilerOptions options;
	private NaiveASTFlattener flattener;
	private Stack< Stack<AttribDeclaration> > attribDeclarationStack;

	/**
	 * @param surfaceDeclarations instruct the parser to ignore statements
	 * and expressions, just parse declarations.
	 */
	public SourceElementParser(
			ISourceElementRequestor requestor,
			CompilerOptions options) {
	
		this.requestor = requestor;
		this.options = options;
		this.flattener = new NaiveASTFlattener();
		this.attribDeclarationStack = new Stack< Stack<AttribDeclaration> >();
		this.attribDeclarationStack.push(new Stack<AttribDeclaration>());
	}
	
	private int getASTlevel() {
		String source = (String) options.getMap().get(CompilerOptions.OPTION_Source);
		if (source == null || source.length() == 0) {
			return AST.D2;
		} else if (source.equals(CompilerOptions.VERSION_2_x)) {
			return AST.D2;
		} else if (source.equals(CompilerOptions.VERSION_1_x)) {
			return AST.D1;
		} else if (source.equals(CompilerOptions.VERSION_0_x)) {
			return AST.D0;
		} else {
			return AST.D2;
		}
	}
	
	public Module parseCompilationUnit(descent.internal.compiler.env.ICompilationUnit unit, boolean resolveBindings) {
		module = CompilationUnitResolver.parse(getASTlevel(), (descent.internal.compiler.env.ICompilationUnit) unit, options.getMap(), true).module;
		
		// If the target is an ICompilationUnit, we need to solve all the
		// compile-time stuff to know the *real* structure of the module
		if (unit instanceof ICompilationUnit) {
			ICompilationUnit cunit = (ICompilationUnit) unit;
			try {
				CompilationUnitResolver.resolve(module, cunit.getJavaProject(), cunit.getOwner());
			} catch (JavaModelException e) {
				Util.log(e);
			}
		}
	
		requestor.enterCompilationUnit();
		module.accept(this);
		requestor.exitCompilationUnit(endOf(module));
		
		return module;
	}
	
	private int startOf(ASTDmdNode node) {
		if (node == null) return 0;
		return node.start;
	}
	
	private int endOf(ASTDmdNode node) {
		if (node == null) return 0;
		return startOf(node) + node.length - 1;
	}
	
	private int startOfDeclaration(ASTDmdNode node) {
		if (node == null) return 0;
		if (attribDeclarationStack.isEmpty()) {
			return startOfCommentIfAny(node);
		} else {
			Stack<AttribDeclaration> stack = attribDeclarationStack.peek();
			if (stack.isEmpty()) {
				return startOfCommentIfAny(node);
			} else {
				return startOfCommentIfAny(stack.get(0));
			}
		}
	}	

	private int endOfDeclaration(ASTDmdNode node) {
		if (node == null) return 0;
		if (attribDeclarationStack.isEmpty()) {
			return endOfCommentIfAny(node);
		} else {
			Stack<AttribDeclaration> stack = attribDeclarationStack.peek();
			if (stack.isEmpty()) {
				return endOfCommentIfAny(node);
			} else {
				return endOfCommentIfAny(stack.get(stack.size() - 1));
			}
		}
	}
	
	private int startOfCommentIfAny(ASTDmdNode node) {
		if (node.preComments == null || node.preComments.isEmpty()) {
			return startOf(node);
		} else {
			return startOf(node.preComments.get(0));
		}
	}

	private int endOfCommentIfAny(ASTDmdNode node) {
		if (node.postComment == null) {
			return endOf(node);
		} else {
			return endOf(node.postComment);
		}
	}
	
	private int getFlags(List<Modifier> modifiers) {
		int flags = 0;
		if (modifiers != null) {
			for(Modifier modifier : modifiers) {
				switch(modifier.tok) {
				case TOKprivate: flags |= Flags.AccPrivate; break;
				case TOKpackage: flags |= Flags.AccPackage; break;
				case TOKprotected: flags |= Flags.AccProtected; break;
				case TOKpublic: flags |= Flags.AccPublic; break;
				case TOKexport: flags |= Flags.AccExport; break;
				case TOKstatic: flags |= Flags.AccStatic; break;
				case TOKfinal: flags |= Flags.AccFinal; break;
				case TOKabstract: flags |= Flags.AccAbstract; break;
				case TOKoverride: flags |= Flags.AccOverride; break;
				case TOKauto: flags |= Flags.AccAuto; break;
				case TOKsynchronized: flags |= Flags.AccSynchronized; break;
				case TOKdeprecated: flags |= Flags.AccDeprecated; break;
				case TOKextern: flags |= Flags.AccExtern; break;
				case TOKconst: flags |= Flags.AccConst; break;
				case TOKscope: flags |= Flags.AccScope; break;
				case TOKinvariant: flags |= Flags.AccInvariant; break;
				case TOKin: break;
				case TOKout: break;
				case TOKinout: break;
				case TOKlazy: break;
				case TOKref: break;
				}			
			}
		}
		return flags;
	}
	
	private char[][] getTokens(BaseClasses baseClasses) {
		if (baseClasses.size() == 0) return CharOperation.NO_CHAR_CHAR;
		
		char[][] tokens = new char[baseClasses.size()][];
		for(int i = 0; i < baseClasses.size(); i++) {
			tokens[i] = baseClasses.get(i).sourceType.toCharArray();
		}
		return tokens;
	}	

	private TypeParameterInfo[] getTypeParameters(TemplateParameters parameters) {
		TypeParameterInfo[] infos = new TypeParameterInfo[parameters.size()];
		for(int i = 0; i < parameters.size(); i++) {
			TemplateParameter param = parameters.get(i);
			
			TypeParameterInfo info = new TypeParameterInfo();
			info.annotationPositions = NO_LONG;
			info.bounds = CharOperation.NO_CHAR_CHAR;
			info.declarationStart = startOfDeclaration(param);
			info.declarationEnd = endOf(param);			
			info.name = param.ident.ident;
			info.nameSourceStart = startOf(param.ident);
			info.nameSourceEnd = endOf(param.ident);
			infos[i] = info;
		}
		return infos;
	}
	
	private char[][] getParameterNames(Arguments arguments) {
		if (arguments.size() == 0) return CharOperation.NO_CHAR_CHAR;
		
		char[][] names = new char[arguments.size()][];
		for(int i = 0; i < arguments.size(); i++) {
			IdentifierExp ident = arguments.get(i).ident;
			if (ident == null) {
				names[i] = CharOperation.NO_CHAR;
			} else {
				names[i] = ident.ident;	
			}
		}
		return names;
	}
	
	private char[][] getParameterTypes(Arguments arguments) {
		if (arguments.size() == 0) return CharOperation.NO_CHAR_CHAR;
		
		char[][] types = new char[arguments.size()][];
		for(int i = 0; i < arguments.size(); i++) {
			Argument argument = arguments.get(i);
			
			StringBuilder sb = new StringBuilder();
			for(Modifier modifier : argument.modifiers) {
				sb.append(modifier.toCharArray());
				sb.append(" ");
			}
			
			Type type = argument.type;
			if (type != null) {
				sb.append(type.toString());
			}
			
			types[i] = sb.toString().toCharArray();
		}
		return types;
	}
	
	// ------------------------------------------------------------------------
	
	public void visit(AggregateDeclaration node, TemplateDeclaration templateDeclaration) {
		switch(node.getNodeType()) {
		case ASTDmdNode.CLASS_DECLARATION:
			ClassDeclaration classDecl = (ClassDeclaration) node;
			visit(classDecl, 0, classDecl.sourceBaseclasses, templateDeclaration);
			break;
		case ASTDmdNode.INTERFACE_DECLARATION:
			InterfaceDeclaration intDecl = (InterfaceDeclaration) node;
			visit(intDecl, Flags.AccInterface, intDecl.sourceBaseclasses, templateDeclaration);
			break;
		case ASTDmdNode.STRUCT_DECLARATION:
			StructDeclaration strDecl = (StructDeclaration) node;
			visit(strDecl, Flags.AccStruct, null, templateDeclaration);
			break;
		case ASTDmdNode.UNION_DECLARATION:
			UnionDeclaration unDecl = (UnionDeclaration) node;
			visit(unDecl, Flags.AccUnion, null, templateDeclaration);
			break;
		}
	}
	
	private void visit(AggregateDeclaration node, int flags, BaseClasses baseClasses, TemplateDeclaration templateDeclaration) {
		// TODO Java -> D
		// Also, since the base class notation in D dosen't distinguis between
		// classes and interfaces, let's assume they are all interfaces for the moment
		TypeInfo info = new TypeInfo();
		info.annotationPositions = NO_LONG;
		info.categories = CharOperation.NO_CHAR_CHAR;
		if (templateDeclaration != null) {
			info.declarationStart = startOfDeclaration(templateDeclaration);
		} else {
			info.declarationStart = startOfDeclaration(node);
		}
		info.modifiers = getFlags(node.modifiers);
		info.modifiers |= flags;
		if (node.ident != null) {
			info.name = node.ident.ident;
			info.nameSourceEnd = endOf(node.ident);
			info.nameSourceStart = startOf(node.ident);
		} else {
			info.name = CharOperation.NO_CHAR;
		}
		info.secondary = !foundType;
		
		if (node instanceof IClassDeclaration) {
			IClassDeclaration c = (IClassDeclaration) node;
			IClassDeclaration b = c.baseClass();
			if (b != null) { // May be null if c is actually Object
				Type t = b.type();
				info.superclass = t.getSignature().toCharArray();
			}
		} else {
			info.superclass = CharOperation.NO_CHAR;
		}
		if (baseClasses != null) {
			info.superinterfaces = getTokens(baseClasses);
		}
		if (templateDeclaration != null) {
			info.typeParameters = getTypeParameters(templateDeclaration.parameters);
		}
		
		foundType = true;		
		requestor.enterType(info);
	}
	
	public boolean visit(ClassDeclaration node) {
		visit(node, 0, node.sourceBaseclasses, null);
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	public boolean visit(InterfaceDeclaration node) {
		visit(node, Flags.AccInterface, node.sourceBaseclasses, null);
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	public boolean visit(ModuleDeclaration node) {
		flattener.reset();
		flattener.visitModuleDeclarationName(node);
		
		requestor.acceptPackage(startOfDeclaration(node), endOfDeclaration(node), flattener.getResult().toCharArray());
		pushLevelInAttribDeclarationStack();
		return false;
	}
	
	public boolean visit(StructDeclaration node) {
		visit(node, Flags.AccStruct, null, null);
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	public boolean visit(UnionDeclaration node) {
		visit(node, Flags.AccUnion, null, null);
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	public boolean visit(TemplateDeclaration node) {
		// TODO Java -> D
		if (node.wrapper) {
			Dsymbol wrappedSymbol = (Dsymbol) node.members.get(0); // SEMANTIC
			if (wrappedSymbol.getNodeType() == ASTDmdNode.FUNC_DECLARATION) {
				visit((FuncDeclaration) wrappedSymbol, node);
				return false;
			} else {
				visit((AggregateDeclaration) wrappedSymbol, node);
				return false;
			}
		}
		
		TypeInfo info = new TypeInfo();
		info.annotationPositions = NO_LONG;
		info.categories = CharOperation.NO_CHAR_CHAR;
		info.declarationStart = startOfDeclaration(node);
		info.modifiers = getFlags(node.modifiers);
		info.modifiers |= Flags.AccTemplate;
		if (node.ident != null) {
			info.name = node.ident.ident;
			info.nameSourceStart = startOf(node.ident);
			info.nameSourceEnd = endOf(node.ident);
		} else {
			info.name = CharOperation.NO_CHAR;
		}
		info.secondary = !foundType;
		info.superclass = CharOperation.NO_CHAR;
		info.typeParameters = getTypeParameters(node.parameters);
		
		foundType = true;
		
		requestor.enterType(info);
		
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	private void visit(FuncDeclaration node, TemplateDeclaration templateDeclaration) {
		TypeFunction ty = (TypeFunction) node.sourceType;
		
		MethodInfo info = new MethodInfo();
		info.annotationPositions = NO_LONG;
		info.categories = CharOperation.NO_CHAR_CHAR;
		if (templateDeclaration != null) {
			info.declarationStart = startOfDeclaration(templateDeclaration);
		} else {
			info.declarationStart = startOfDeclaration(node);
		}
		info.exceptionTypes = CharOperation.NO_CHAR_CHAR;
		info.modifiers = getFlags(node.modifiers);
		if (node.ident != null) {
			info.name = node.ident.ident;
			info.nameSourceStart = startOf(node.ident);
			info.nameSourceEnd = endOf(node.ident);
		} else {
			info.name = CharOperation.NO_CHAR;
		}
		info.parameterNames = getParameterNames(ty.parameters);
		info.parameterTypes = getParameterTypes(ty.parameters);
		info.returnType = ty.next.toCharArray();
		if (templateDeclaration != null) {
			info.typeParameters = getTypeParameters(templateDeclaration.parameters);
		}
		
		requestor.enterMethod(info);
	}
	
	public boolean visit(FuncDeclaration node) {
		visit(node, null);
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	private boolean visit(FuncDeclaration node, int flags, Arguments arguments) {
		MethodInfo info = new MethodInfo();
		info.annotationPositions = NO_LONG;
		info.categories = CharOperation.NO_CHAR_CHAR;
		info.declarationStart = startOfDeclaration(node);
		info.exceptionTypes = CharOperation.NO_CHAR_CHAR;
		info.modifiers = getFlags(node.modifiers);
		info.modifiers |= flags;
		info.name = CharOperation.NO_CHAR;
		if (arguments != null) {
			info.parameterNames = getParameterNames(arguments);
			info.parameterTypes = getParameterTypes(arguments);
		}
		info.returnType = "void".toCharArray();
		info.typeParameters = new TypeParameterInfo[0];
		
		requestor.enterConstructor(info);
		return true;
	}
	
	public boolean visit(CtorDeclaration node) {
		visit(node, Flags.AccConstructor, node.arguments);
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	public boolean visit(DtorDeclaration node) {
		visit(node, Flags.AccDestructor, null);
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	public boolean visit(NewDeclaration node) {
		visit(node, Flags.AccNew, node.arguments);
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	public boolean visit(DeleteDeclaration node) {
		visit(node, Flags.AccDelete, node.arguments);
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	public boolean visit(StaticCtorDeclaration node) {
		requestor.enterInitializer(startOfDeclaration(node), getFlags(node.modifiers), CharOperation.NO_CHAR);
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	public boolean visit(StaticDtorDeclaration node) {
		requestor.enterInitializer(startOfDeclaration(node), getFlags(node.modifiers) | Flags.AccStaticDestructor, CharOperation.NO_CHAR);
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	public boolean visit(InvariantDeclaration node) {
		requestor.enterInitializer(startOfDeclaration(node), getFlags(node.modifiers) | Flags.AccInvariant, CharOperation.NO_CHAR);
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	public boolean visit(UnitTestDeclaration node) {
		requestor.enterInitializer(startOfDeclaration(node), getFlags(node.modifiers) | Flags.AccUnitTest, CharOperation.NO_CHAR);
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	public boolean visit(VarDeclaration node) {
		VarDeclaration last = node;
		while(last.next != null) {
			last = last.next;
		}
		
		FieldInfo info = new FieldInfo();
		info.annotationPositions = NO_LONG;
		info.categories = CharOperation.NO_CHAR_CHAR;
		info.declarationStart = startOfDeclaration(last);
		info.modifiers = getFlags(node.modifiers);
		if (node.ident != null) {
			info.name = node.ident.ident;
			info.nameSourceStart = startOf(node.ident);
			info.nameSourceEnd = endOf(node.ident);
		} else {
			info.name = CharOperation.NO_CHAR;
		}
		if (node.sourceType != null) {
			info.type = node.sourceType.toCharArray();
		} else {
			info.type = CharOperation.NO_CHAR;
		}
		
		requestor.enterField(info);
		
		return false;
	}
	
	public boolean visit(AliasDeclaration node) {
		AliasDeclaration last = node;
		while(last.next != null) {
			last = last.next;
		}
		
		FieldInfo info = new FieldInfo();
		info.annotationPositions = NO_LONG;
		info.categories = CharOperation.NO_CHAR_CHAR;
		info.declarationStart = startOfDeclaration(last);
		info.modifiers = getFlags(node.modifiers);
		info.modifiers |= Flags.AccAlias;
		if (node.ident != null) {
			info.name = node.ident.ident;
			info.nameSourceStart = startOf(node.ident);
			info.nameSourceEnd = endOf(node.ident);
		} else {
			info.name = CharOperation.NO_CHAR;
		}
		if (node.sourceType != null) {
			info.type = node.sourceType.toCharArray();
		} else {
			info.type = CharOperation.NO_CHAR;
		}
		
		requestor.enterField(info);
		
		return false;
	}
	
	public boolean visit(TypedefDeclaration node) {
		TypedefDeclaration last = node;
		while(last.next != null) {
			last = last.next;
		}
		
		FieldInfo info = new FieldInfo();
		info.annotationPositions = NO_LONG;
		info.categories = CharOperation.NO_CHAR_CHAR;
		info.declarationStart = startOfDeclaration(last);
		info.modifiers = getFlags(node.modifiers);
		info.modifiers |= Flags.AccTypedef;
		if (node.ident != null) {
			info.name = node.ident.ident;
			info.nameSourceStart = startOf(node.ident);
			info.nameSourceEnd = endOf(node.ident);
		} else {
			info.name = CharOperation.NO_CHAR;
		}
		if (node.sourceType != null) {
			info.type = node.sourceType.toCharArray();
		} else {
			info.type = CharOperation.NO_CHAR;
		}
		
		requestor.enterField(info);
		
		return false;
	}
	
	public boolean visit(StaticAssert node) {
		requestor.enterInitializer(startOfDeclaration(node), getFlags(node.modifiers) | Flags.AccStaticAssert, node.exp.toCharArray());
		return false;
	}
	
	public boolean visit(DebugSymbol node) {
		requestor.enterInitializer(startOfDeclaration(node), getFlags(node.modifiers) | Flags.AccDebugAssignment, node.version.value);
		return false;
	}

	public boolean visit(VersionSymbol node) {
		requestor.enterInitializer(startOfDeclaration(node), getFlags(node.modifiers) | Flags.AccVersionAssignment, node.version.value);
		return false;
	}
	
	public boolean visit(AlignDeclaration node) {
		requestor.enterInitializer(startOfDeclaration(node), getFlags(node.modifiers) | Flags.AccAlign, node.salign == 0 ? CharOperation.NO_CHAR : String.valueOf(node.salign).toCharArray());
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	public boolean visit(LinkDeclaration node) {
		char[] id = null;
		switch(node.linkage) {
		case LINKc: id = Id.C; break;
		case LINKcpp: id = Id.Cpp; break;
		case LINKd: id = Id.D; break;
		case LINKdefault: id = Id.empty;
		case LINKpascal: id = Id.Pascal;
		case LINKsystem: id = Id.System;
		case LINKwindows: id = Id.Windows;
		}
		requestor.enterInitializer(startOfDeclaration(node), getFlags(node.modifiers) | Flags.AccExternDeclaration, id);
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	public boolean visit(PragmaDeclaration node) {
		StringBuilder sb = new StringBuilder();
		sb.append(node.ident);
		if (node.args != null && node.args.size() > 0) {
			sb.append(": ");
			for(int i = 0; i < node.args.size(); i++) {
				if (i != 0)
					sb.append(", ");
				sb.append(node.args.get(i).toString());
			}
		}
		requestor.enterInitializer(startOfDeclaration(node), getFlags(node.modifiers) | Flags.AccPragma, sb.toString().toCharArray());
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	public boolean visit(EnumDeclaration node) {
		// TODO Java -> D
		// Also, since the base class notation in D dosen't distinguis between
		// classes and interfaces, let's assume they are all interfaces for the moment
		TypeInfo info = new TypeInfo();
		info.annotationPositions = NO_LONG;
		info.categories = CharOperation.NO_CHAR_CHAR;
		info.declarationStart = startOfDeclaration(node);
		info.modifiers = getFlags(node.modifiers);
		info.modifiers |= Flags.AccEnum;
		if (node.ident != null) {
			info.name = node.ident.ident;
			info.nameSourceStart = startOf(node.ident);
			info.nameSourceEnd = endOf(node.ident);
		} else {
			info.name = CharOperation.NO_CHAR;
		}
		info.secondary = !foundType;
		info.superclass = node.memtype.getSignature().toCharArray();
		if (node.memtype != null) {
			info.superinterfaces = new char[][] { node.memtype.toCharArray() };
		}
		
		info.enumValues = new integer_t[] { node.defaultval, node.minval, node.maxval };
		
		foundType = true;
		
		requestor.enterType(info);
		
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	public boolean visit(EnumMember node) {
		FieldInfo info = new FieldInfo();
		info.annotationPositions = NO_LONG;
		info.categories = CharOperation.NO_CHAR_CHAR;
		info.declarationStart = startOfDeclaration(node);
		info.modifiers = Flags.AccEnum;
		if (node.ident != null) {
			info.name = node.ident.ident;
			info.nameSourceStart = startOf(node.ident);
			info.nameSourceEnd = endOf(node.ident);
		} else {
			info.name = CharOperation.NO_CHAR;
		}
		
		// TODO info.type = the one of the enum declaration
		info.type = DUMMY_TYPE;
		
		requestor.enterField(info);
		
		return false;
	}
	
	public boolean visit(TemplateMixin node) {
		FieldInfo info = new FieldInfo();
		info.annotationPositions = NO_LONG;
		info.categories = CharOperation.NO_CHAR_CHAR;
		info.declarationStart = startOfDeclaration(node);
		info.modifiers = getFlags(node.modifiers);
		info.modifiers |= Flags.AccTemplateMixin;
		if (node.ident != null) {
			info.name = node.ident.ident;
			info.nameSourceStart = startOf(node.ident);
			info.nameSourceEnd = endOf(node.ident);				
		} else {
			info.name = CharOperation.NO_CHAR;
		}
		
		flattener.reset();
		flattener.visitTemplateMixinType(node);
		
		info.type = flattener.getResult().toCharArray();
		
		requestor.enterField(info);
		return false;
	}
	
	public boolean visit(CompileDeclaration node) {
		requestor.enterInitializer(startOf(node), getFlags(node.modifiers) | Flags.AccMixin, String.valueOf(node.exp.toString()).toCharArray());
		return false;
	}
	
	public boolean visit(ConditionalDeclaration node) {
		char[] displayString = CharOperation.NO_CHAR;;
		int flags = 0;
		switch(node.condition.getConditionType()) {
		case Condition.DEBUG: {
			DebugCondition cond = (DebugCondition) node.condition;
			displayString = cond.toCharArray();
			break;
		}
		case Condition.IFTYPE: {
			flags = Flags.AccIftypeDeclaration;
			break;
		}
		case Condition.STATIC_IF: {
			StaticIfCondition cond = (StaticIfCondition) node.condition;
			if (cond.exp != null) {
				displayString = cond.exp.toCharArray();
			}
			flags = Flags.AccStaticIfDeclaration;
			break;
		}
		case Condition.VERSION: {
			VersionCondition cond = (VersionCondition) node.condition;
			displayString = cond.toCharArray();
			flags = Flags.AccVersionDeclaration;
			break;
		}
		}
		requestor.enterConditional(startOf(node), getFlags(node.modifiers) | flags, displayString);
		
		Dsymbols thenDeclarations = node.decl;
		Dsymbols elseDeclarations = node.elsedecl;
		
		if (thenDeclarations != null && !thenDeclarations.isEmpty()) {
			if (elseDeclarations != null && !elseDeclarations.isEmpty()) {
				requestor.enterConditionalThen(startOf((Dsymbol) thenDeclarations.get(0))); // SEMANTIC
			}
			for(IDsymbol ideclaration : thenDeclarations) {
				Dsymbol declaration = (Dsymbol) ideclaration; // SEMANTIC
				declaration.accept(this);
			}
			if (elseDeclarations != null &&!elseDeclarations.isEmpty()) {
				requestor.exitConditionalThen(endOf((Dsymbol) thenDeclarations.get(thenDeclarations.size() - 1))); // SEMANTIC
			}
		}
		
		
		if (elseDeclarations != null &&!elseDeclarations.isEmpty()) {
			requestor.enterConditionalElse(startOf((Dsymbol) elseDeclarations.get(0))); // SEMANTIC
			for(IDsymbol ideclaration : elseDeclarations) {
				Dsymbol declaration = (Dsymbol) ideclaration; // SEMANTIC
				declaration.accept(this);
			}
			requestor.exitConditionalElse(endOf((Dsymbol) elseDeclarations.get(elseDeclarations.size() - 1))); // SEMANTIC
		}
		
		pushLevelInAttribDeclarationStack();
		return false;
	}
	
	public boolean visit(Import node) {
		if (!node.first) {
			return false;
		}
		
		while(node != null) {
			int start, end;
			if (node.first) {
				start = node.firstStart;
				int otherStart = startOfDeclaration(node);
				if (otherStart < start) {
					start = otherStart;
				}
			} else {
				start = startOfDeclaration(node);
			}
			if (node.next == null) {
				end = node.start + node.lastLength - 1;
			} else {
				end = endOfDeclaration(node);
			}
			
			int flags = node.isstatic ? Flags.AccStatic : 0;
			
			requestor.acceptImport(start, end, importToString(node), false, flags);
			
			node = node.next;
		}
		pushLevelInAttribDeclarationStack();
		return false;
	}
	
	private String importToString(Import node) {
		StringBuilder buffer = new StringBuilder();
		if (node.aliasId != null) {
			buffer.append(node.aliasId);
			buffer.append(" = ");
		}
		if (node.packages != null) {
			for(int i = 0; i < node.packages.size(); i++) {
				if (i > 0) {
					buffer.append('.');
				}
				buffer.append(node.packages.get(i));
			}
			buffer.append('.');
		}
		if (node.id != null) {
			buffer.append(node.id);
		}
		if (node.names != null) {
			buffer.append(" : ");
			for(int i = 0; i < node.names.size(); i++) {
				if (i > 0) {
					buffer.append(", ");
				}
				if (node.aliases.get(i) != null) {
					node.aliases.get(i).accept(this);
					buffer.append(" = ");
				}
				buffer.append(node.names.get(i));
			}
		}
		return buffer.toString();
	}

	public boolean visit(ProtDeclaration node) {
		if (node.single) {
			pushAttribDeclaration(node);
		}
		return true;
	}
	
	public boolean visit(StorageClassDeclaration node) {
		if (node.single) {
			pushAttribDeclaration(node);
		}
		return true;
	}
	
	public boolean visit(DeclarationStatement node) {
		Dsymbol dsymbol = (Dsymbol) ((DeclarationExp) node.sourceExp).declaration; // SEMANTIC
		switch(dsymbol.getNodeType()) {
		case ASTDmdNode.CLASS_DECLARATION:
		case ASTDmdNode.INTERFACE_DECLARATION:
		case ASTDmdNode.STRUCT_DECLARATION:
		case ASTDmdNode.UNION_DECLARATION:
		case ASTDmdNode.FUNC_DECLARATION:
			return true;
		default:
			return false;
		}
	}
	
	private void pushAttribDeclaration(AttribDeclaration node) {
		Stack<AttribDeclaration> stack = attribDeclarationStack.peek();
		stack.push(node);
	}
	
	private void pushLevelInAttribDeclarationStack() {
		attribDeclarationStack.push(new Stack<AttribDeclaration>());
	}
	
	private void popLevelInAttribDeclarationStack() {
		attribDeclarationStack.pop();
	}
	
	// ------------------------------------------------------------------------	
	
	public void endVisit(ModuleDeclaration node) {
		popLevelInAttribDeclarationStack();
	}

	public void endVisit(ClassDeclaration node) {
		requestor.exitType(endOfDeclaration(node));
		popLevelInAttribDeclarationStack();
	}
	
	public void endVisit(InterfaceDeclaration node) {
		requestor.exitType(endOfDeclaration(node));
		popLevelInAttribDeclarationStack();
	}
	
	public void endVisit(StructDeclaration node) {
		requestor.exitType(endOfDeclaration(node));
		popLevelInAttribDeclarationStack();
	}
	
	public void endVisit(UnionDeclaration node) {
		requestor.exitType(endOfDeclaration(node));
		popLevelInAttribDeclarationStack();
	}
	
	public void endVisit(TemplateDeclaration node) {
		if (node.postComment != null) {
			requestor.exitType(endOfDeclaration(node.postComment));
		} else {
			requestor.exitType(endOfDeclaration(node));
		}
		if (!node.wrapper) {
			popLevelInAttribDeclarationStack();
		}
	}
	
	public void endVisit(FuncDeclaration node) {
		requestor.exitMethod(endOfDeclaration(node), -1, -1);
		popLevelInAttribDeclarationStack();
	}
	
	public void endVisit(CtorDeclaration node) {
		requestor.exitConstructor(endOfDeclaration(node));
		popLevelInAttribDeclarationStack();
	}

	public void endVisit(DtorDeclaration node) {
		requestor.exitConstructor(endOfDeclaration(node));
		popLevelInAttribDeclarationStack();
	}

	public void endVisit(NewDeclaration node) {
		requestor.exitConstructor(endOfDeclaration(node));
		popLevelInAttribDeclarationStack();
	}

	public void endVisit(DeleteDeclaration node) {
		requestor.exitConstructor(endOfDeclaration(node));
		popLevelInAttribDeclarationStack();
	}
	
	public void endVisit(StaticCtorDeclaration node) {
		requestor.exitInitializer(endOfDeclaration(node));
		popLevelInAttribDeclarationStack();
	}

	public void endVisit(StaticDtorDeclaration node) {
		requestor.exitInitializer(endOfDeclaration(node));
		popLevelInAttribDeclarationStack();
	}

	public void endVisit(InvariantDeclaration node) {
		requestor.exitInitializer(endOfDeclaration(node));
		popLevelInAttribDeclarationStack();
	}

	public void endVisit(UnitTestDeclaration node) {
		requestor.exitInitializer(endOfDeclaration(node));
		popLevelInAttribDeclarationStack();
	}
	
	public void endVisit(VarDeclaration node) {
		VarDeclaration last = node;
		while(last.next != null) {
			last = last.next;
		}
		
		int initializerStart = node.sourceInit == null ? - 1 : startOf((ASTDmdNode) node.sourceInit); // SEMANTIC
		int declarationSourceEnd = endOf(last);
		int declarationEnd = endOfDeclaration(node.ident);
		
		requestor.exitField(initializerStart, declarationEnd, declarationSourceEnd);
	}
	
	public void endVisit(AliasDeclaration node) {
		AliasDeclaration last = node;
		while(last.next != null) {
			last = last.next;
		}
		
		int initializerStart = endOf(node.ident);
		int declarationSourceEnd = endOf(last);
		int declarationEnd = endOfDeclaration(node.ident);
		
		requestor.exitField(initializerStart, declarationEnd, declarationSourceEnd);
	}

	public void endVisit(TypedefDeclaration node) {
		TypedefDeclaration last = node;
		while(last.next != null) {
			last = last.next;
		}
		
		int initializerStart = endOf(node.ident);
		int declarationSourceEnd = endOf(last);
		int declarationEnd = endOfDeclaration(node.ident);
		
		requestor.exitField(initializerStart, declarationEnd, declarationSourceEnd);
	}
	
	public void endVisit(StaticAssert node) {
		requestor.exitInitializer(endOfDeclaration(node));
	}

	public void endVisit(DebugSymbol node) {
		requestor.exitInitializer(endOfDeclaration(node));
	}

	public void endVisit(VersionSymbol node) {
		requestor.exitInitializer(endOfDeclaration(node));
	}

	public void endVisit(AlignDeclaration node) {
		requestor.exitInitializer(endOfDeclaration(node));
		popLevelInAttribDeclarationStack();
	}

	public void endVisit(LinkDeclaration node) {
		requestor.exitInitializer(endOfDeclaration(node));
		popLevelInAttribDeclarationStack();
	}

	public void endVisit(PragmaDeclaration node) {
		requestor.exitInitializer(endOfDeclaration(node));
		popLevelInAttribDeclarationStack();
	}

	public void endVisit(EnumDeclaration node) {
		requestor.exitType(endOfDeclaration(node));
		popLevelInAttribDeclarationStack();
	}

	public void endVisit(EnumMember node) {
		int initializerStart = node.value == null ? - 1 : startOf(node.value);
		int declarationEnd = endOf(node.ident);
		int declarationSourceEnd = endOfDeclaration(node);
		
		requestor.exitField(initializerStart, declarationEnd, declarationSourceEnd);
	}

	public void endVisit(TemplateMixin node) {
		int declarationSourceEnd = endOfDeclaration(node);
		int initializerStart = node.ident == null ? declarationSourceEnd - 1 : endOf(node.ident);			
		
		requestor.exitField(initializerStart, declarationSourceEnd, declarationSourceEnd);
	}

	public void endVisit(CompileDeclaration node) {
		requestor.exitInitializer(endOfDeclaration(node));
	}
	
	public void endVisit(ConditionalDeclaration node) {
		requestor.exitConditional(endOfDeclaration(node));
		popLevelInAttribDeclarationStack();
	}
	
	public void endVisit(Import node) {
		if (!node.first) {
			return;
		}
		
		popLevelInAttribDeclarationStack();
	}
	
	public void endVisit(ProtDeclaration node) {
		if (node.single) {
			popAttribDeclaration();
		}
	}

	public void endVisit(StorageClassDeclaration node) {
		if (node.single) {
			popAttribDeclaration();
		}
	}
	
	private void popAttribDeclaration() {
		if (!attribDeclarationStack.isEmpty()) {
			Stack<AttribDeclaration> stack = attribDeclarationStack.peek();
			if (!stack.isEmpty()) {
				stack.pop();
			}
		}
	}
	
	// ------------------------------------------------------------------------

	public boolean visit(ASTNode node) {
		return false;
	}

	public boolean visit(ASTDmdNode node) {
		return false;
	}

	public boolean visit(AddAssignExp node) {
		return false;
	}

	public boolean visit(AddExp node) {
		return false;
	}

	public boolean visit(AddrExp node) {
		return false;
	}

	public boolean visit(AggregateDeclaration node) {
		return false;
	}

	public boolean visit(AndAndExp node) {
		return false;
	}

	public boolean visit(AndAssignExp node) {
		return false;
	}

	public boolean visit(AndExp node) {
		return false;
	}

	public boolean visit(AnonDeclaration node) {
		return false;
	}

	public boolean visit(AnonymousAggregateDeclaration node) {
		return false;
	}

	public boolean visit(Argument node) {
		return false;
	}

	public boolean visit(ArrayExp node) {
		return false;
	}

	public boolean visit(ArrayInitializer node) {
		return false;
	}
	
	public boolean visit(ArrayLengthExp node) {
		return false;
	}

	public boolean visit(ArrayLiteralExp node) {
		return false;
	}

	public boolean visit(ArrayScopeSymbol node) {
		return false;
	}

	public boolean visit(AsmBlock node) {
		return false;
	}

	public boolean visit(AsmStatement node) {
		return false;
	}

	public boolean visit(AssertExp node) {
		return false;
	}

	public boolean visit(AssignExp node) {
		return false;
	}

	public boolean visit(AssocArrayLiteralExp node) {
		return false;
	}

	public boolean visit(AttribDeclaration node) {
		return false;
	}

	public boolean visit(BaseClass node) {
		return false;
	}

	public boolean visit(BinExp node) {
		return false;
	}

	public boolean visit(BoolExp node) {
		return false;
	}

	public boolean visit(BreakStatement node) {
		return false;
	}

	public boolean visit(CallExp node) {
		return false;
	}

	public boolean visit(CaseStatement node) {
		return true;
	}

	public boolean visit(CastExp node) {
		return false;
	}

	public boolean visit(CatAssignExp node) {
		return false;
	}

	public boolean visit(Catch node) {
		return true;
	}

	public boolean visit(CatExp node) {
		return false;
	}

	public boolean visit(ClassInfoDeclaration node) {
		return false;
	}

	public boolean visit(CmpExp node) {
		return false;
	}

	public boolean visit(ComExp node) {
		return false;
	}

	public boolean visit(CommaExp node) {
		return false;
	}

	public boolean visit(CompileExp node) {
		return false;
	}

	public boolean visit(CompileStatement node) {
		return false;
	}

	public boolean visit(ComplexExp node) {
		return false;
	}

	public boolean visit(CompoundStatement node) {
		return true;
	}

	public boolean visit(CondExp node) {
		return false;
	}

	public boolean visit(Condition node) {
		return false;
	}

	public boolean visit(ConditionalStatement node) {
		return true;
	}

	public boolean visit(ContinueStatement node) {
		return false;
	}

	public boolean visit(DebugCondition node) {
		return false;
	}

	public boolean visit(Declaration node) {
		return false;
	}

	public boolean visit(DeclarationExp node) {
		return false;
	}

	public boolean visit(DefaultStatement node) {
		return true;
	}

	public boolean visit(DelegateExp node) {
		return false;
	}

	public boolean visit(DeleteExp node) {
		return false;
	}

	public boolean visit(DivAssignExp node) {
		return false;
	}

	public boolean visit(DivExp node) {
		return false;
	}

	public boolean visit(DollarExp node) {
		return false;
	}

	public boolean visit(DoStatement node) {
		return true;
	}

	public boolean visit(DotExp node) {
		return false;
	}

	public boolean visit(DotIdExp node) {
		return false;
	}

	public boolean visit(DotTemplateExp node) {
		return false;
	}

	public boolean visit(DotTemplateInstanceExp node) {
		return false;
	}

	public boolean visit(DotTypeExp node) {
		return false;
	}

	public boolean visit(DotVarExp node) {
		return false;
	}

	public boolean visit(Dsymbol node) {
		return false;
	}

	public boolean visit(DsymbolExp node) {
		return false;
	}

	public boolean visit(EqualExp node) {
		return false;
	}

	public boolean visit(ExpInitializer node) {
		return false;
	}

	public boolean visit(Expression node) {
		return false;
	}

	public boolean visit(ExpStatement node) {
		return false;
	}

	public boolean visit(FileExp node) {
		return false;
	}

	public boolean visit(ForeachRangeStatement node) {
		return true;
	}

	public boolean visit(ForeachStatement node) {
		return true;
	}

	public boolean visit(ForStatement node) {
		return true;
	}

	public boolean visit(FuncAliasDeclaration node) {
		return false;
	}

	public boolean visit(FuncExp node) {
		return false;
	}

	public boolean visit(FuncLiteralDeclaration node) {
		return false;
	}

	public boolean visit(GotoCaseStatement node) {
		return false;
	}

	public boolean visit(GotoDefaultStatement node) {
		return false;
	}

	public boolean visit(GotoStatement node) {
		return false;
	}

	public boolean visit(HaltExp node) {
		return false;
	}

	public boolean visit(IdentifierExp node) {
		return false;
	}

	public boolean visit(IdentityExp node) {
		return false;
	}

	public boolean visit(IfStatement node) {
		return true;
	}

	public boolean visit(IftypeCondition node) {
		return false;
	}

	public boolean visit(IftypeExp node) {
		return false;
	}

	public boolean visit(IndexExp node) {
		return false;
	}

	public boolean visit(InExp node) {
		return false;
	}

	public boolean visit(Initializer node) {
		return false;
	}

	public boolean visit(IntegerExp node) {
		return false;
	}	

	public boolean visit(LabelDsymbol node) {
		return false;
	}

	public boolean visit(LabelStatement node) {
		return true;
	}

	public boolean visit(MinAssignExp node) {
		return false;
	}

	public boolean visit(MinExp node) {
		return false;
	}

	public boolean visit(ModAssignExp node) {
		return false;
	}

	public boolean visit(ModExp node) {
		return false;
	}

	public boolean visit(Modifier node) {
		return false;
	}

	public boolean visit(Module node) {
		return true;
	}

	public boolean visit(ModuleInfoDeclaration node) {
		return false;
	}

	public boolean visit(MulAssignExp node) {
		return false;
	}

	public boolean visit(MulExp node) {
		return false;
	}

	public boolean visit(NegExp node) {
		return false;
	}

	public boolean visit(NewAnonClassExp node) {
		return false;
	}	

	public boolean visit(NewExp node) {
		return false;
	}

	public boolean visit(NotExp node) {
		return false;
	}

	public boolean visit(NullExp node) {
		return false;
	}

	public boolean visit(OnScopeStatement node) {
		return true;
	}

	public boolean visit(OrAssignExp node) {
		return false;
	}

	public boolean visit(OrExp node) {
		return false;
	}

	public boolean visit(OrOrExp node) {
		return false;
	}

	public boolean visit(Package node) {
		return false;
	}

	public boolean visit(PostExp node) {
		return false;
	}	

	public boolean visit(PragmaStatement node) {
		return true;
	}

	public boolean visit(PtrExp node) {
		return false;
	}

	public boolean visit(RealExp node) {
		return false;
	}

	public boolean visit(RemoveExp node) {
		return false;
	}

	public boolean visit(ReturnStatement node) {
		return false;
	}

	public boolean visit(ScopeDsymbol node) {
		return false;
	}

	public boolean visit(ScopeExp node) {
		return false;
	}

	public boolean visit(ScopeStatement node) {
		return false;
	}

	public boolean visit(ShlAssignExp node) {
		return false;
	}

	public boolean visit(ShlExp node) {
		return false;
	}

	public boolean visit(ShrAssignExp node) {
		return false;
	}

	public boolean visit(ShrExp node) {
		return false;
	}

	public boolean visit(SliceExp node) {
		return false;
	}

	public boolean visit(Statement node) {
		return false;
	}

	public boolean visit(StaticAssertStatement node) {
		return false;
	}

	public boolean visit(StaticIfCondition node) {
		return false;
	}

	public boolean visit(StaticIfDeclaration node) {
		return false;
	}

	public boolean visit(StringExp node) {
		return false;
	}

	public boolean visit(StructInitializer node) {
		return false;
	}

	public boolean visit(SuperExp node) {
		return false;
	}

	public boolean visit(SwitchStatement node) {
		return true;
	}

	public boolean visit(SymOffExp node) {
		return false;
	}

	public boolean visit(SynchronizedStatement node) {
		return true;
	}

	public boolean visit(TemplateAliasParameter node) {
		return false;
	}

	public boolean visit(TemplateExp node) {
		return false;
	}

	public boolean visit(TemplateInstance node) {
		return false;
	}

	public boolean visit(TemplateInstanceWrapper node) {
		return false;
	}

	public boolean visit(TemplateParameter node) {
		return false;
	}

	public boolean visit(TemplateTupleParameter node) {
		return false;
	}

	public boolean visit(TemplateTypeParameter node) {
		return false;
	}

	public boolean visit(TemplateValueParameter node) {
		return false;
	}

	public boolean visit(ThisDeclaration node) {
		return false;
	}

	public boolean visit(ThisExp node) {
		return false;
	}

	public boolean visit(ThrowStatement node) {
		return false;
	}

	public boolean visit(TraitsExp node) {
		return false;
	}

	public boolean visit(TryCatchStatement node) {
		return true;
	}

	public boolean visit(TryFinallyStatement node) {
		return true;
	}

	public boolean visit(Tuple node) {
		return false;
	}

	public boolean visit(TupleDeclaration node) {
		return false;
	}

	public boolean visit(TupleExp node) {
		return false;
	}

	public boolean visit(Type node) {
		return false;
	}

	public boolean visit(TypeAArray node) {
		return false;
	}

	public boolean visit(TypeBasic node) {
		return false;
	}

	public boolean visit(TypeClass node) {
		return false;
	}

	public boolean visit(TypeDArray node) {
		return false;
	}	

	public boolean visit(TypeDelegate node) {
		return false;
	}

	public boolean visit(TypeDotIdExp node) {
		return false;
	}

	public boolean visit(TypeEnum node) {
		return false;
	}

	public boolean visit(TypeExp node) {
		return false;
	}

	public boolean visit(TypeFunction node) {
		return false;
	}

	public boolean visit(TypeIdentifier node) {
		return false;
	}

	public boolean visit(TypeidExp node) {
		return false;
	}

	public boolean visit(TypeInfoArrayDeclaration node) {
		return false;
	}

	public boolean visit(TypeInfoAssociativeArrayDeclaration node) {
		return false;
	}

	public boolean visit(TypeInfoClassDeclaration node) {
		return false;
	}

	public boolean visit(TypeInfoDeclaration node) {
		return false;
	}

	public boolean visit(TypeInfoDelegateDeclaration node) {
		return false;
	}

	public boolean visit(TypeInfoEnumDeclaration node) {
		return false;
	}

	public boolean visit(TypeInfoFunctionDeclaration node) {
		return false;
	}

	public boolean visit(TypeInfoInterfaceDeclaration node) {
		return false;
	}

	public boolean visit(TypeInfoPointerDeclaration node) {
		return false;
	}

	public boolean visit(TypeInfoStaticArrayDeclaration node) {
		return false;
	}

	public boolean visit(TypeInfoStructDeclaration node) {
		return false;
	}

	public boolean visit(TypeInfoTypedefDeclaration node) {
		return false;
	}

	public boolean visit(TypeInstance node) {
		return false;
	}

	public boolean visit(TypePointer node) {
		return false;
	}

	public boolean visit(TypeQualified node) {
		return false;
	}

	public boolean visit(TypeSArray node) {
		return false;
	}

	public boolean visit(TypeSlice node) {
		return false;
	}

	public boolean visit(TypeStruct node) {
		return false;
	}

	public boolean visit(TypeTuple node) {
		return false;
	}

	public boolean visit(TypeTypedef node) {
		return false;
	}

	public boolean visit(TypeTypeof node) {
		return false;
	}

	public boolean visit(UAddExp node) {
		return false;
	}

	public boolean visit(UnaExp node) {
		return false;
	}

	public boolean visit(UnrolledLoopStatement node) {
		return false;
	}

	public boolean visit(UshrAssignExp node) {
		return false;
	}

	public boolean visit(UshrExp node) {
		return false;
	}

	public boolean visit(VarExp node) {
		return false;
	}

	public boolean visit(Version node) {
		return false;
	}

	public boolean visit(VersionCondition node) {
		return false;
	}

	public boolean visit(VoidInitializer node) {
		return false;
	}

	public boolean visit(VolatileStatement node) {
		return true;
	}

	public boolean visit(WhileStatement node) {
		return true;
	}

	public boolean visit(WithScopeSymbol node) {
		return false;
	}

	public boolean visit(WithStatement node) {
		return false;
	}

	public boolean visit(XorAssignExp node) {
		return false;
	}

	public boolean visit(XorExp node) {
		return false;
	}
	
}
