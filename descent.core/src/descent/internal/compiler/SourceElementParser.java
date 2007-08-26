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
import descent.internal.compiler.parser.ast.IASTVisitor;
import descent.internal.compiler.parser.ast.NaiveASTFlattener;

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
public class SourceElementParser implements IASTVisitor {
	
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
			return node.start;
		} else {
			Stack<AttribDeclaration> stack = attribDeclarationStack.peek();
			if (stack.isEmpty()) {
				return node.start;
			} else {
				return startOf(stack.get(0));
			}
		}
	}
	
	private int endOfDeclaration(ASTDmdNode node) {
		if (node == null) return 0;
		if (attribDeclarationStack.isEmpty()) {
			return startOf(node) + node.length - 1;
		} else {
			Stack<AttribDeclaration> stack = attribDeclarationStack.peek();
			if (stack.isEmpty()) {
				return startOf(node) + node.length - 1;
			} else {
				return endOf(stack.get(stack.size() - 1));
			}
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
	
	private char[][] getTokens(List<BaseClass> baseClasses) {
		if (baseClasses.size() == 0) return CharOperation.NO_CHAR_CHAR;
		
		char[][] tokens = new char[baseClasses.size()][];
		for(int i = 0; i < baseClasses.size(); i++) {
			tokens[i] = baseClasses.get(i).sourceType.toCharArray();
		}
		return tokens;
	}	

	private TypeParameterInfo[] getTypeParameters(List<TemplateParameter> parameters) {
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
	
	private char[][] getParameterNames(List<Argument> arguments) {
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
	
	private char[][] getParameterTypes(List<Argument> arguments) {
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
	
	public void visit(AggregateDeclaration node, List<TemplateParameter> parameters, List<Comment> preDdocs) {
		switch(node.getNodeType()) {
		case ASTDmdNode.CLASS_DECLARATION:
			ClassDeclaration classDecl = (ClassDeclaration) node;
			visit(classDecl, 0, parameters, classDecl.baseclasses, preDdocs);
			break;
		case ASTDmdNode.INTERFACE_DECLARATION:
			InterfaceDeclaration intDecl = (InterfaceDeclaration) node;
			visit(intDecl, Flags.AccInterface, parameters, intDecl.baseclasses, preDdocs);
			break;
		case ASTDmdNode.STRUCT_DECLARATION:
			StructDeclaration strDecl = (StructDeclaration) node;
			visit(strDecl, Flags.AccStruct, parameters, null, preDdocs);
			break;
		case ASTDmdNode.UNION_DECLARATION:
			UnionDeclaration unDecl = (UnionDeclaration) node;
			visit(unDecl, Flags.AccUnion, parameters, null, preDdocs);
			break;
		}
	}
	
	private void visit(AggregateDeclaration node, int flags, List<TemplateParameter> templateParameters, List<BaseClass> baseClasses, List<Comment> preDdocs) {
		// TODO Java -> D
		// Also, since the base class notation in D dosen't distinguis between
		// classes and interfaces, let's assume they are all interfaces for the moment
		TypeInfo info = new TypeInfo();
		info.annotationPositions = NO_LONG;
		info.categories = CharOperation.NO_CHAR_CHAR;
		if (preDdocs != null && preDdocs.size() > 0) {
			info.declarationStart = startOfDeclaration(preDdocs.get(0));
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
		info.superclass = CharOperation.NO_CHAR;
		if (baseClasses != null) {
			info.superinterfaces = getTokens(baseClasses);
		}
		if (templateParameters != null) {
			info.typeParameters = getTypeParameters(templateParameters);
		}
		
		foundType = true;		
		requestor.enterType(info);
	}
	
	public boolean visit(ClassDeclaration node) {
		visit(node, 0, null, node.baseclasses, null);
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	public boolean visit(InterfaceDeclaration node) {
		visit(node, Flags.AccInterface, null, node.baseclasses, null);
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	public boolean visit(ModuleDeclaration node) {
		flattener.reset();
		flattener.visitModuleDeclarationName(node);
		
		requestor.acceptPackage(startOf(node), endOf(node), flattener.getResult().toCharArray());
		pushLevelInAttribDeclarationStack();
		return false;
	}
	
	public boolean visit(MultiImport parent) {
		if (parent.imports != null && parent.imports.size() > 0) {
			int length = parent.imports.size();
			for(int index = 0; index < length; index++) {
				Import node = parent.imports.get(index);
				
				int start, end;
				if (index == 0) {
					start = startOf(parent);
				} else {
					start = startOf(node);
				}
				if (index == length - 1) {
					end = endOf(parent);
				} else {
					end = endOf(node);
				}
				
				int flags = parent.isstatic ? Flags.AccStatic : 0;
				
				flattener.reset();
				node.accept(flattener);			
				requestor.acceptImport(start, end, flattener.getResult(), false, flags);
			}
		}
		pushLevelInAttribDeclarationStack();
		return false;
	}
	
	public boolean visit(StructDeclaration node) {
		visit(node, Flags.AccStruct, null, null, null);
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	public boolean visit(UnionDeclaration node) {
		visit(node, Flags.AccUnion, null, null, null);
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	public boolean visit(TemplateDeclaration node) {
		// TODO Java -> D
		if (node.wrapper) {
			Dsymbol wrappedSymbol = node.members.get(0);
			if (wrappedSymbol.getNodeType() == ASTDmdNode.FUNC_DECLARATION) {
				visit((FuncDeclaration) wrappedSymbol, node.parameters, node.preDdocs);
				return false;
			} else {
				visit((AggregateDeclaration) wrappedSymbol, node.parameters, node.preDdocs);
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
	
	private void visit(FuncDeclaration node, List<TemplateParameter> templateParameters, List<Comment> preDdocs) {
		TypeFunction ty = (TypeFunction) node.type;
		
		MethodInfo info = new MethodInfo();
		info.annotationPositions = NO_LONG;
		info.categories = CharOperation.NO_CHAR_CHAR;
		if (preDdocs != null && preDdocs.size() > 0) {
			info.declarationStart = startOfDeclaration(preDdocs.get(0));
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
		if (templateParameters != null) {
			info.typeParameters = getTypeParameters(templateParameters);
		}
		
		requestor.enterMethod(info);
	}
	
	public boolean visit(FuncDeclaration node) {
		visit(node, null, null);
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	private boolean visit(FuncDeclaration node, int flags, List<Argument> arguments) {
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
		requestor.enterInitializer(startOf(node), getFlags(node.modifiers), CharOperation.NO_CHAR);
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	public boolean visit(StaticDtorDeclaration node) {
		requestor.enterInitializer(startOf(node), getFlags(node.modifiers) | Flags.AccStaticDestructor, CharOperation.NO_CHAR);
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	public boolean visit(InvariantDeclaration node) {
		requestor.enterInitializer(startOf(node), getFlags(node.modifiers) | Flags.AccInvariant, CharOperation.NO_CHAR);
		pushLevelInAttribDeclarationStack();
		return true;
	}
	
	public boolean visit(UnitTestDeclaration node) {
		requestor.enterInitializer(startOf(node), getFlags(node.modifiers) | Flags.AccUnitTest, CharOperation.NO_CHAR);
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
		if (node.type != null) {
			info.type = node.type.toCharArray();
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
		if (node.type != null) {
			info.type = node.type.toCharArray();
		} else {
			info.type = CharOperation.NO_CHAR;
		}
		
		requestor.enterField(info);
		
		return false;
	}
	
	public boolean visit(StaticAssert node) {
		requestor.enterInitializer(startOf(node), getFlags(node.modifiers) | Flags.AccStaticAssert, node.exp.toCharArray());
		return false;
	}
	
	public boolean visit(DebugSymbol node) {
		requestor.enterInitializer(startOf(node), getFlags(node.modifiers) | Flags.AccDebugAssignment, node.version.value);
		return false;
	}

	public boolean visit(VersionSymbol node) {
		requestor.enterInitializer(startOf(node), getFlags(node.modifiers) | Flags.AccVersionAssignment, node.version.value);
		return false;
	}
	
	public boolean visit(AlignDeclaration node) {
		requestor.enterInitializer(startOf(node), getFlags(node.modifiers) | Flags.AccAlign, node.salign == 0 ? CharOperation.NO_CHAR : String.valueOf(node.salign).toCharArray());
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
		requestor.enterInitializer(startOf(node), getFlags(node.modifiers) | Flags.AccExternDeclaration, id);
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
		requestor.enterInitializer(startOf(node), getFlags(node.modifiers) | Flags.AccPragma, sb.toString().toCharArray());
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
		info.superclass = CharOperation.NO_CHAR;
		if (node.memtype != null) {
			info.superinterfaces = new char[][] { node.memtype.toCharArray() };
		}
		
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
		
		List<Dsymbol> thenDeclarations = node.decl;
		List<Dsymbol> elseDeclarations = node.elsedecl;
		
		if (thenDeclarations != null && !thenDeclarations.isEmpty()) {
			if (elseDeclarations != null && !elseDeclarations.isEmpty()) {
				requestor.enterConditionalThen(startOf(thenDeclarations.get(0)));
			}
			for(Dsymbol declaration : thenDeclarations) {
				declaration.accept(this);
			}
			if (elseDeclarations != null &&!elseDeclarations.isEmpty()) {
				requestor.exitConditionalThen(endOf(thenDeclarations.get(thenDeclarations.size() - 1)));
			}
		}
		
		
		if (elseDeclarations != null &&!elseDeclarations.isEmpty()) {
			requestor.enterConditionalElse(startOf(elseDeclarations.get(0)));
			for(Dsymbol declaration : elseDeclarations) {
				declaration.accept(this);
			}
			requestor.exitConditionalElse(endOf(elseDeclarations.get(elseDeclarations.size() - 1)));
		}
		
		pushLevelInAttribDeclarationStack();
		return false;
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

	public void endVisit(MultiImport node) {
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
		if (node.postDdoc != null) {
			requestor.exitType(endOfDeclaration(node.postDdoc));
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
		
		int initializerStart = node.sourceInit == null ? - 1 : startOf(node.sourceInit);
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

	public boolean visit(DeclarationStatement node) {
		Dsymbol dsymbol = ((DeclarationExp) node.exp).declaration;
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

	public boolean visit(DecrementExp node) {
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

	public boolean visit(Import node) {
		return false;
	}

	public boolean visit(IncrementExp node) {
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

	public boolean visit(MultiStringExp node) {
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

	public boolean visit(ParenExp node) {
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

	public boolean visit(TypeInfoStructureDeclaration node) {
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

	public boolean visit(TypeTupleDelegateDeclaration node) {
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
	
	public void endVisit(ASTNode node) {
	}

	public void endVisit(AddAssignExp node) {
	}

	public void endVisit(AddExp node) {
	}

	public void endVisit(AddrExp node) {
	}

	public void endVisit(AggregateDeclaration node) {
	}

	public void endVisit(AndAndExp node) {
	}

	public void endVisit(AndAssignExp node) {
	}

	public void endVisit(AndExp node) {
	}

	public void endVisit(AnonDeclaration node) {
	}

	public void endVisit(AnonymousAggregateDeclaration node) {
	}

	public void endVisit(Argument node) {
	}

	public void endVisit(ArrayExp node) {
	}

	public void endVisit(ArrayInitializer node) {
	}

	public void endVisit(ArrayLiteralExp node) {
	}

	public void endVisit(ArrayScopeSymbol node) {
	}

	public void endVisit(AsmBlock node) {
	}

	public void endVisit(AsmStatement node) {
	}

	public void endVisit(AssertExp node) {
	}

	public void endVisit(AssignExp node) {
	}

	public void endVisit(AssocArrayLiteralExp node) {
	}

	public void endVisit(AttribDeclaration node) {
	}

	public void endVisit(BaseClass node) {
	}

	public void endVisit(BinExp node) {
	}

	public void endVisit(BoolExp node) {
	}

	public void endVisit(BreakStatement node) {
	}

	public void endVisit(CallExp node) {
	}

	public void endVisit(CaseStatement node) {
	}

	public void endVisit(CastExp node) {
	}

	public void endVisit(CatAssignExp node) {
	}

	public void endVisit(Catch node) {
	}

	public void endVisit(CatExp node) {
	}

	public void endVisit(ClassInfoDeclaration node) {
	}

	public void endVisit(CmpExp node) {
	}

	public void endVisit(ComExp node) {
	}

	public void endVisit(CommaExp node) {
	}

	public void endVisit(CompileExp node) {
	}

	public void endVisit(CompileStatement node) {
	}

	public void endVisit(ComplexExp node) {
	}

	public void endVisit(CompoundStatement node) {
	}

	public void endVisit(CondExp node) {
	}

	public void endVisit(Condition node) {
	}	

	public void endVisit(ConditionalStatement node) {
	}

	public void endVisit(ContinueStatement node) {
	}

	public void endVisit(DebugCondition node) {
	}

	public void endVisit(Declaration node) {
	}

	public void endVisit(DeclarationExp node) {
	}

	public void endVisit(DeclarationStatement node) {
	}

	public void endVisit(DecrementExp node) {
	}

	public void endVisit(DefaultStatement node) {
	}

	public void endVisit(DelegateExp node) {
	}	

	public void endVisit(DeleteExp node) {
	}

	public void endVisit(DivAssignExp node) {
	}

	public void endVisit(DivExp node) {
	}

	public void endVisit(DollarExp node) {
	}

	public void endVisit(DoStatement node) {
	}

	public void endVisit(DotExp node) {
	}

	public void endVisit(DotIdExp node) {
	}

	public void endVisit(DotTemplateExp node) {
	}

	public void endVisit(DotTemplateInstanceExp node) {
	}

	public void endVisit(DotTypeExp node) {
	}

	public void endVisit(DotVarExp node) {
	}

	public void endVisit(Dsymbol node) {
	}

	public void endVisit(DsymbolExp node) {
	}

	public void endVisit(EqualExp node) {
	}

	public void endVisit(ExpInitializer node) {
	}

	public void endVisit(Expression node) {
	}

	public void endVisit(ExpStatement node) {
	}

	public void endVisit(FileExp node) {
	}

	public void endVisit(ForeachRangeStatement node) {
	}

	public void endVisit(ForeachStatement node) {
	}

	public void endVisit(ForStatement node) {
	}

	public void endVisit(FuncAliasDeclaration node) {
	}

	public void endVisit(FuncExp node) {
	}

	public void endVisit(FuncLiteralDeclaration node) {
	}

	public void endVisit(GotoCaseStatement node) {
	}

	public void endVisit(GotoDefaultStatement node) {
	}

	public void endVisit(GotoStatement node) {
	}

	public void endVisit(HaltExp node) {
	}

	public void endVisit(IdentifierExp node) {
	}

	public void endVisit(IdentityExp node) {
	}

	public void endVisit(IfStatement node) {
	}

	public void endVisit(IftypeCondition node) {
	}

	public void endVisit(IftypeExp node) {
	}

	public void endVisit(Import node) {
	}

	public void endVisit(IncrementExp node) {
	}

	public void endVisit(IndexExp node) {
	}

	public void endVisit(InExp node) {
	}

	public void endVisit(Initializer node) {
	}

	public void endVisit(IntegerExp node) {
	}

	public void endVisit(LabelDsymbol node) {
	}

	public void endVisit(LabelStatement node) {
	}

	public void endVisit(MinAssignExp node) {
	}

	public void endVisit(MinExp node) {
	}

	public void endVisit(ModAssignExp node) {
	}

	public void endVisit(ModExp node) {
	}

	public void endVisit(Modifier node) {
	}

	public void endVisit(Module node) {
	}

	public void endVisit(ModuleInfoDeclaration node) {
	}

	public void endVisit(MulAssignExp node) {
	}

	public void endVisit(MulExp node) {
	}

	public void endVisit(MultiStringExp node) {
	}

	public void endVisit(NegExp node) {
	}

	public void endVisit(NewAnonClassExp node) {
	}	

	public void endVisit(NewExp node) {
	}

	public void endVisit(NotExp node) {
	}

	public void endVisit(NullExp node) {
	}

	public void endVisit(OnScopeStatement node) {
	}

	public void endVisit(OrAssignExp node) {
	}

	public void endVisit(OrExp node) {
	}

	public void endVisit(OrOrExp node) {
	}

	public void endVisit(Package node) {
	}

	public void endVisit(ParenExp node) {
	}

	public void endVisit(PostExp node) {
	}

	public void endVisit(PragmaStatement node) {
	}

	public void endVisit(PtrExp node) {
	}

	public void endVisit(RealExp node) {
	}

	public void endVisit(RemoveExp node) {
	}

	public void endVisit(ReturnStatement node) {
	}

	public void endVisit(ScopeDsymbol node) {
	}

	public void endVisit(ScopeExp node) {
	}

	public void endVisit(ScopeStatement node) {
	}

	public void endVisit(ShlAssignExp node) {
	}

	public void endVisit(ShlExp node) {
	}

	public void endVisit(ShrAssignExp node) {
	}

	public void endVisit(ShrExp node) {
	}

	public void endVisit(SliceExp node) {
	}

	public void endVisit(Statement node) {
	}

	public void endVisit(StaticAssertStatement node) {
	}

	public void endVisit(StaticIfCondition node) {
	}

	public void endVisit(StaticIfDeclaration node) {
	}
	
	public void endVisit(StringExp node) {
	}

	public void endVisit(StructInitializer node) {
	}

	public void endVisit(SuperExp node) {
	}

	public void endVisit(SwitchStatement node) {
	}

	public void endVisit(SymOffExp node) {
	}

	public void endVisit(SynchronizedStatement node) {
	}

	public void endVisit(TemplateAliasParameter node) {
	}	

	public void endVisit(TemplateExp node) {
	}

	public void endVisit(TemplateInstance node) {
	}

	public void endVisit(TemplateInstanceWrapper node) {
	}

	public void endVisit(TemplateParameter node) {
	}

	public void endVisit(TemplateTupleParameter node) {
	}

	public void endVisit(TemplateTypeParameter node) {
	}

	public void endVisit(TemplateValueParameter node) {
	}

	public void endVisit(ThisDeclaration node) {
	}

	public void endVisit(ThisExp node) {
	}

	public void endVisit(ThrowStatement node) {
	}

	public void endVisit(TraitsExp node) {
	}

	public void endVisit(TryCatchStatement node) {
	}

	public void endVisit(TryFinallyStatement node) {
	}

	public void endVisit(Tuple node) {
	}

	public void endVisit(TupleDeclaration node) {
	}

	public void endVisit(TupleExp node) {
	}

	public void endVisit(Type node) {
	}

	public void endVisit(TypeAArray node) {
	}

	public void endVisit(TypeBasic node) {
	}

	public void endVisit(TypeClass node) {
	}

	public void endVisit(TypeDArray node) {
	}

	public void endVisit(TypeDelegate node) {
	}

	public void endVisit(TypeDotIdExp node) {
	}

	public void endVisit(TypeEnum node) {
	}

	public void endVisit(TypeExp node) {
	}

	public void endVisit(TypeFunction node) {
	}

	public void endVisit(TypeIdentifier node) {
	}

	public void endVisit(TypeidExp node) {
	}

	public void endVisit(TypeInfoArrayDeclaration node) {
	}

	public void endVisit(TypeInfoAssociativeArrayDeclaration node) {
	}

	public void endVisit(TypeInfoClassDeclaration node) {
	}

	public void endVisit(TypeInfoDeclaration node) {
	}

	public void endVisit(TypeInfoDelegateDeclaration node) {
	}

	public void endVisit(TypeInfoEnumDeclaration node) {
	}

	public void endVisit(TypeInfoFunctionDeclaration node) {
	}

	public void endVisit(TypeInfoInterfaceDeclaration node) {
	}

	public void endVisit(TypeInfoPointerDeclaration node) {
	}

	public void endVisit(TypeInfoStaticArrayDeclaration node) {
	}

	public void endVisit(TypeInfoStructureDeclaration node) {
	}

	public void endVisit(TypeInfoTypedefDeclaration node) {
	}

	public void endVisit(TypeInstance node) {
	}

	public void endVisit(TypePointer node) {
	}

	public void endVisit(TypeQualified node) {
	}

	public void endVisit(TypeSArray node) {
	}

	public void endVisit(TypeSlice node) {
	}

	public void endVisit(TypeStruct node) {
	}

	public void endVisit(TypeTuple node) {
	}

	public void endVisit(TypeTupleDelegateDeclaration node) {
	}

	public void endVisit(TypeTypedef node) {
	}

	public void endVisit(TypeTypeof node) {
	}

	public void endVisit(UAddExp node) {
	}

	public void endVisit(UnaExp node) {
	}

	public void endVisit(UnrolledLoopStatement node) {
	}

	public void endVisit(UshrAssignExp node) {
	}

	public void endVisit(UshrExp node) {
	}	

	public void endVisit(VarExp node) {
	}

	public void endVisit(Version node) {
	}

	public void endVisit(VersionCondition node) {
	}

	public void endVisit(VoidInitializer node) {
	}

	public void endVisit(VolatileStatement node) {
	}

	public void endVisit(WhileStatement node) {
	}

	public void endVisit(WithScopeSymbol node) {
	}

	public void endVisit(WithStatement node) {
	}

	public void endVisit(XorAssignExp node) {
	}

	public void endVisit(XorExp node) {
	}

	public void postVisit(ASTNode node) {
	}

	public void preVisit(ASTNode node) {
	}
	
}
