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

import descent.core.Flags;
import descent.core.ICompilationUnit;
import descent.core.compiler.CharOperation;
import descent.core.dom.AST;
import descent.core.dom.ASTNode;
import descent.core.dom.ASTParser;
import descent.core.dom.ASTVisitor;
import descent.core.dom.AggregateDeclaration;
import descent.core.dom.AliasDeclaration;
import descent.core.dom.AliasDeclarationFragment;
import descent.core.dom.AlignDeclaration;
import descent.core.dom.Argument;
import descent.core.dom.BaseClass;
import descent.core.dom.CompilationUnit;
import descent.core.dom.ConditionalDeclaration;
import descent.core.dom.ConstructorDeclaration;
import descent.core.dom.DebugAssignment;
import descent.core.dom.DebugDeclaration;
import descent.core.dom.Declaration;
import descent.core.dom.EnumDeclaration;
import descent.core.dom.EnumMember;
import descent.core.dom.ExternDeclaration;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.IftypeDeclaration;
import descent.core.dom.Import;
import descent.core.dom.ImportDeclaration;
import descent.core.dom.InvariantDeclaration;
import descent.core.dom.MixinDeclaration;
import descent.core.dom.Modifier;
import descent.core.dom.ModuleDeclaration;
import descent.core.dom.PragmaDeclaration;
import descent.core.dom.SimpleName;
import descent.core.dom.StaticAssert;
import descent.core.dom.StaticIfDeclaration;
import descent.core.dom.TemplateDeclaration;
import descent.core.dom.TemplateMixinDeclaration;
import descent.core.dom.TemplateParameter;
import descent.core.dom.Type;
import descent.core.dom.TypedefDeclaration;
import descent.core.dom.TypedefDeclarationFragment;
import descent.core.dom.UnitTestDeclaration;
import descent.core.dom.VariableDeclaration;
import descent.core.dom.VariableDeclarationFragment;
import descent.core.dom.VersionAssignment;
import descent.core.dom.VersionDeclaration;
import descent.internal.compiler.ISourceElementRequestor.FieldInfo;
import descent.internal.compiler.ISourceElementRequestor.MethodInfo;
import descent.internal.compiler.ISourceElementRequestor.TypeInfo;
import descent.internal.compiler.ISourceElementRequestor.TypeParameterInfo;
import descent.internal.compiler.impl.CompilerOptions;

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
public class SourceElementParserBackup extends ASTVisitor {
	
	private final static long[] NO_LONG = new long[0];
	
	public ISourceElementRequestor requestor;
	private CompilationUnit compilationUnit;
	private boolean foundType = false;
	CompilerOptions options;

	/**
	 * @param surfaceDeclarations instruct the parser to ignore statements
	 * and expressions, just parse declarations.
	 */
	public SourceElementParserBackup(
			ISourceElementRequestor requestor,
			CompilerOptions options) {
	
		this.requestor = requestor;
		this.options = options;
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
	
	public CompilationUnit parseCompilationUnit(ICompilationUnit unit, boolean resolveBindings) {
		ASTParser parser = ASTParser.newParser(getASTlevel());
		parser.setSource(unit);
		parser.setCompilerOptions(options.getMap());
		parser.setResolveBindings(resolveBindings);
		compilationUnit = (CompilationUnit) parser.createAST(null);
		
		requestor.enterCompilationUnit();
		compilationUnit.accept(this);
		requestor.exitCompilationUnit(endOf(compilationUnit));
		
		return compilationUnit;
	}
	
	public CompilationUnit parseCompilationUnit(descent.internal.compiler.env.ICompilationUnit unit, boolean resolveBindings) {
		ASTParser parser = ASTParser.newParser(getASTlevel());
		parser.setSource(unit.getContents());
		parser.setCompilerOptions(options.getMap());
		parser.setResolveBindings(resolveBindings);
		compilationUnit = (CompilationUnit) parser.createAST(null);
		
		requestor.enterCompilationUnit();
		compilationUnit.accept(this);
		requestor.exitCompilationUnit(endOf(compilationUnit));
		
		return compilationUnit;
	}
	
	private int startOf(ASTNode node) {
		return compilationUnit.getExtendedStartPosition(node);
	}
	
	private int endOf(ASTNode node) {
		return startOf(node) + compilationUnit.getExtendedLength(node) - 1;
	}
	
	private int getFlags(List<Modifier> modifiers) {
		int flags = 0;
		for(Modifier modifier : modifiers) {
			flags |= modifier.getModifierKeyword().toFlagValue();
		}
		return flags;
	}
	
	private char[][] getTokens(List<BaseClass> baseClasses) {
		if (baseClasses.size() == 0) return CharOperation.NO_CHAR_CHAR;
		
		char[][] tokens = new char[baseClasses.size()][];
		for(int i = 0; i < baseClasses.size(); i++) {
			tokens[i] = baseClasses.get(i).getType().toString().toCharArray();
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
			info.declarationStart = startOf(param);
			info.declarationEnd = endOf(param);			
			info.name = param.getName().getIdentifier().toCharArray();
			info.nameSourceStart = startOf(param.getName());
			info.nameSourceEnd = endOf(param.getName());
			infos[i] = info;
		}
		return infos;
	}
	
	private char[][] getParameterNames(List<Argument> arguments) {
		if (arguments.size() == 0) return CharOperation.NO_CHAR_CHAR;
		
		char[][] names = new char[arguments.size()][];
		for(int i = 0; i < arguments.size(); i++) {
			SimpleName name = arguments.get(i).getName();
			if (name == null) {
				names[i] = CharOperation.NO_CHAR;
			} else {
				names[i] = name.getIdentifier().toCharArray();
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
			for(Modifier modifier : argument.modifiers()) {
				sb.append(modifier.getModifierKeyword().toString());
				sb.append(" ");
			}
			
			Type type = argument.getType();
			if (type != null) {
				sb.append(type.toString());
			}
			
			types[i] = sb.toString().toCharArray();
		}
		return types;
	}
	
	private boolean isFieldContainer(ASTNode parent) {
		return !((parent != null 
				&& parent.getNodeType() != ASTNode.COMPILATION_UNIT
				&& parent.getNodeType() != ASTNode.AGGREGATE_DECLARATION
				&& parent.getNodeType() != ASTNode.TEMPLATE_DECLARATION));
	}
	
	@Override
	public boolean visit(ModuleDeclaration node) {
		requestor.acceptPackage(startOf(node), endOf(node), node.getName().getFullyQualifiedName().toCharArray());
		return false;
	}
	
	@Override
	public boolean visit(Import node) {
		ImportDeclaration parent = (ImportDeclaration) node.getParent();
		int index = parent.imports().indexOf(node);
		int start, end;
		if (index == 0) {
			start = startOf(parent);
		} else {
			start = startOf(node);
		}
		if (index == parent.imports().size() - 1) {
			end = endOf(parent);
		} else {
			end = endOf(node);
		}
		
		
		int flags = parent.isStatic() ? Flags.AccStatic : 0;
		requestor.acceptImport(start, end, node.toString(), false, flags);
		return false;
	}
	
	@Override
	public boolean visit(AggregateDeclaration node) {
		// TODO Java -> D
		// Also, since the base class notation in D dosen't distinguis between
		// classes and interfaces, let's assume they are all interfaces for the moment
		TypeInfo info = new TypeInfo();
		info.annotationPositions = NO_LONG;
		info.categories = CharOperation.NO_CHAR_CHAR;
		info.declarationStart = startOf(node);
		info.modifiers = getFlags(node.modifiers());
		switch(node.getKind()) {
		case INTERFACE:
			info.modifiers |= Flags.AccInterface;
			break;
		case STRUCT:
			info.modifiers |= Flags.AccStruct;
			break;
		case UNION:
			info.modifiers |= Flags.AccUnion;
			break;
		}
		if (node.getName() != null) {
			info.name = node.getName().getFullyQualifiedName().toCharArray();
			info.nameSourceEnd = endOf(node.getName());
			info.nameSourceStart = startOf(node.getName());
		}
		info.secondary = !foundType;
		info.superclass = CharOperation.NO_CHAR;
		info.superinterfaces = getTokens(node.baseClasses());		
		info.typeParameters = getTypeParameters(node.templateParameters());
		
		foundType = true;
		
		requestor.enterType(info);
		
		return true;
	}
	
	@Override
	public void endVisit(AggregateDeclaration node) {
		requestor.exitType(endOf(node));
	}
	
	@Override
	public boolean visit(EnumDeclaration node) {
		// TODO Java -> D
		// Also, since the base class notation in D dosen't distinguis between
		// classes and interfaces, let's assume they are all interfaces for the moment
		TypeInfo info = new TypeInfo();
		info.annotationPositions = NO_LONG;
		info.categories = CharOperation.NO_CHAR_CHAR;
		info.declarationStart = startOf(node);
		info.modifiers = getFlags(node.modifiers());
		info.modifiers |= Flags.AccEnum;
		if (node.getName() != null) {
			info.name = node.getName().getFullyQualifiedName().toCharArray();
			info.nameSourceStart = startOf(node.getName());
			info.nameSourceEnd = endOf(node.getName());
		}
		info.secondary = !foundType;
		info.superclass = CharOperation.NO_CHAR;
		if (node.getBaseType() != null) {
			info.superinterfaces = new char[][] { node.getBaseType().toString().toCharArray() };
		}
		
		foundType = true;
		
		requestor.enterType(info);
		
		return true;
	}
	
	@Override
	public void endVisit(EnumDeclaration node) {
		requestor.exitType(endOf(node));
	}
	
	@Override
	public boolean visit(TemplateDeclaration node) {
		// TODO Java -> D
		TypeInfo info = new TypeInfo();
		info.annotationPositions = NO_LONG;
		info.categories = CharOperation.NO_CHAR_CHAR;
		info.declarationStart = startOf(node);
		info.modifiers = getFlags(node.modifiers());
		info.modifiers |= Flags.AccTemplate;
		info.name = node.getName().getFullyQualifiedName().toCharArray();
		info.nameSourceStart = startOf(node.getName());
		info.nameSourceEnd = endOf(node.getName());		
		info.secondary = !foundType;
		info.superclass = CharOperation.NO_CHAR;
		info.typeParameters = getTypeParameters(node.templateParameters());
		
		foundType = true;
		
		requestor.enterType(info);
		
		return true;
	}
	
	@Override
	public void endVisit(TemplateDeclaration node) {
		requestor.exitType(endOf(node));
	}
	
	@Override
	public boolean visit(ConstructorDeclaration node) {
		if (node.getKind() == ConstructorDeclaration.Kind.STATIC_CONSTRUCTOR) {
			requestor.enterInitializer(startOf(node), getFlags(node.modifiers()), CharOperation.NO_CHAR);
		} else if (node.getKind() == ConstructorDeclaration.Kind.STATIC_DESTRUCTOR) {
			requestor.enterInitializer(startOf(node), getFlags(node.modifiers()) | Flags.AccStaticDestructor, CharOperation.NO_CHAR);
		} else {
			MethodInfo info = new MethodInfo();
			info.annotationPositions = NO_LONG;
			info.categories = CharOperation.NO_CHAR_CHAR;
			info.declarationStart = startOf(node);
			info.exceptionTypes = CharOperation.NO_CHAR_CHAR;
			info.modifiers = getFlags(node.modifiers());
			switch(node.getKind()) {
			case CONSTRUCTOR:
				info.modifiers |= Flags.AccConstructor;
				break;
			case DESTRUCTOR:
				info.modifiers |= Flags.AccDestructor;
				break;
			case NEW:
				info.modifiers |= Flags.AccNew;
				break;
			case DELETE:
				info.modifiers |= Flags.AccDelete;
				break;
			}				
			info.name = CharOperation.NO_CHAR;
			info.parameterNames = getParameterNames(node.arguments());
			info.parameterTypes = getParameterTypes(node.arguments());
			info.returnType = "void".toCharArray();
			info.typeParameters = new TypeParameterInfo[0];
			
			requestor.enterConstructor(info);
		}
		return true;
	}
	
	@Override
	public void endVisit(ConstructorDeclaration node) {
		if (node.getKind() == ConstructorDeclaration.Kind.STATIC_CONSTRUCTOR
				|| node.getKind() == ConstructorDeclaration.Kind.STATIC_DESTRUCTOR) {
			requestor.exitInitializer(endOf(node));
		} else {
			requestor.exitConstructor(endOf(node));
		}
	}
	
	@Override
	public boolean visit(FunctionDeclaration node) {
		MethodInfo info = new MethodInfo();
		info.annotationPositions = NO_LONG;
		info.categories = CharOperation.NO_CHAR_CHAR;
		info.declarationStart = startOf(node);
		info.exceptionTypes = CharOperation.NO_CHAR_CHAR;
		info.modifiers = getFlags(node.modifiers());
		info.name = node.getName().getIdentifier().toCharArray();
		info.nameSourceStart = startOf(node.getName());
		info.nameSourceEnd = endOf(node.getName());			
		info.parameterNames = getParameterNames(node.arguments());
		info.parameterTypes = getParameterTypes(node.arguments());
		info.returnType = node.getReturnType().toString().toCharArray();
		info.typeParameters = getTypeParameters(node.templateParameters());
		
		requestor.enterMethod(info);
		return true;
	}
	
	@Override
	public void endVisit(FunctionDeclaration node) {
		requestor.exitMethod(endOf(node), -1, -1);
	}
	
	@Override
	public boolean visit(VariableDeclarationFragment node) {
		// TODO JDT Java -> D
		VariableDeclaration var = (VariableDeclaration) node.getParent();
		
		ASTNode parent = var.getParent();
		if (!isFieldContainer(parent)) {
			return false;
		}
		
		FieldInfo info = new FieldInfo();
		info.annotationPositions = NO_LONG;
		info.categories = CharOperation.NO_CHAR_CHAR;
		
		if (var.fragments().get(0) == node) {
			info.declarationStart = startOf(var);
		} else {
			info.declarationStart = startOf(node);
		}
		
		info.modifiers = getFlags(var.modifiers());
		info.name = node.getName().getIdentifier().toCharArray();
		info.nameSourceStart = startOf(node.getName());
		info.nameSourceEnd = endOf(node.getName());
		if (var.getType() != null) {
			info.type = var.getType().toString().toCharArray();
		} else {
			info.type = CharOperation.NO_CHAR;
		}
		
		requestor.enterField(info);
		
		return false;
	}
	
	@Override
	public void endVisit(VariableDeclarationFragment node) {
		VariableDeclaration var = (VariableDeclaration) node.getParent();
		
		ASTNode parent = var.getParent();
		if (!isFieldContainer(parent)) {
			return;
		}
		
		int initializerStart = node.getInitializer() == null ? - 1 : startOf(node.getInitializer());
		int declarationSourceEnd = endOf(var);
		int declarationEnd = endOf(node.getName());
		
		requestor.exitField(initializerStart, declarationEnd, declarationSourceEnd);
	}
	
	@Override
	public boolean visit(AliasDeclarationFragment node) {
		// TODO JDT Java -> D
		AliasDeclaration var = (AliasDeclaration) node.getParent();
		
		ASTNode parent = var.getParent();
		if (!isFieldContainer(parent)) {
			return false;
		}
		
		FieldInfo info = new FieldInfo();
		info.annotationPositions = NO_LONG;
		info.categories = CharOperation.NO_CHAR_CHAR;
		
		if (var.fragments().get(0) == node) {
			info.declarationStart = startOf(var);
		} else {
			info.declarationStart = startOf(node);
		}
		
		info.modifiers = getFlags(var.modifiers());
		info.modifiers |= Flags.AccAlias;
		info.name = node.getName().getIdentifier().toCharArray();
		info.nameSourceStart = startOf(node.getName());
		info.nameSourceEnd = endOf(node.getName());			
		info.type = var.getType().toString().toCharArray();
		
		requestor.enterField(info);
		
		return false;
	}
	
	@Override
	public void endVisit(AliasDeclarationFragment node) {
		AliasDeclaration var = (AliasDeclaration) node.getParent();
		
		ASTNode parent = var.getParent();
		if (!isFieldContainer(parent)) {
			return;
		}
		
		int initializerStart = endOf(node.getName());
		int declarationSourceEnd = endOf(var);
		int declarationEnd = endOf(node.getName());
		
		requestor.exitField(initializerStart, declarationEnd, declarationSourceEnd);
	}
	
	@Override
	public boolean visit(TypedefDeclarationFragment node) {
		// TODO JDT Java -> D
		TypedefDeclaration var = (TypedefDeclaration) node.getParent();
		
		ASTNode parent = var.getParent();
		if (!isFieldContainer(parent)) {
			return false;
		}
		
		FieldInfo info = new FieldInfo();
		info.annotationPositions = NO_LONG;
		info.categories = CharOperation.NO_CHAR_CHAR;
		
		if (var.fragments().get(0) == node) {
			info.declarationStart = startOf(var);
		} else {
			info.declarationStart = startOf(node);
		}
		
		info.modifiers = getFlags(var.modifiers());
		info.modifiers |= Flags.AccTypedef;
		info.name = node.getName().getIdentifier().toCharArray();
		info.nameSourceStart = startOf(node.getName());
		info.nameSourceEnd = endOf(node.getName());			
		info.type = var.getType().toString().toCharArray();
		
		requestor.enterField(info);
		
		return false;
	}
	
	@Override
	public void endVisit(TypedefDeclarationFragment node) {
		TypedefDeclaration var = (TypedefDeclaration) node.getParent();
		
		ASTNode parent = var.getParent();
		if (!isFieldContainer(parent)) {
			return;
		}
		
		int initializerStart = node.getInitializer() == null ? - 1 : startOf(node.getInitializer());
		int declarationSourceEnd = endOf(var);
		int declarationEnd = endOf(node.getName());
		
		requestor.exitField(initializerStart, declarationEnd, declarationSourceEnd);
	}
	
	@Override
	public boolean visit(TemplateMixinDeclaration node) {
		FieldInfo info = new FieldInfo();
		info.annotationPositions = NO_LONG;
		info.categories = CharOperation.NO_CHAR_CHAR;
		info.declarationStart = startOf(node);
		info.modifiers = getFlags(node.modifiers());
		info.modifiers |= Flags.AccTemplateMixin;
		if (node.getName() != null) {
			info.name = node.getName().getIdentifier().toCharArray();
			info.nameSourceStart = startOf(node.getName());
			info.nameSourceEnd = endOf(node.getName());				
		} else {
			info.name = CharOperation.NO_CHAR;
		}
		info.type = node.getType().toString().toCharArray();
		
		requestor.enterField(info);
		return false;
	}
	
	@Override
	public void endVisit(TemplateMixinDeclaration node) {
		int declarationSourceEnd = endOf(node);
		int initializerStart = node.getName() == null ? declarationSourceEnd - 1 : endOf(node.getName());			
		
		requestor.exitField(initializerStart, declarationSourceEnd, declarationSourceEnd);
	}
	
	@Override
	public boolean visit(EnumMember node) {
		FieldInfo info = new FieldInfo();
		info.annotationPositions = NO_LONG;
		info.categories = CharOperation.NO_CHAR_CHAR;
		info.declarationStart = startOf(node);
		info.modifiers = Flags.AccEnum;
		info.name = node.getName().getIdentifier().toCharArray();
		info.nameSourceStart = startOf(node.getName());
		info.nameSourceEnd = endOf(node.getName());		
		
		EnumDeclaration enumDeclaration = (EnumDeclaration) node.getParent();
		if (enumDeclaration.getBaseType() != null) {
			info.type = enumDeclaration.getBaseType().toString().toCharArray();
		} else {
			info.type = "int".toCharArray();
		}
		
		requestor.enterField(info);
		
		return false;
	}
	
	@Override
	public void endVisit(EnumMember node) {
		int initializerStart = node.getValue() == null ? - 1 : startOf(node.getValue());
		int declarationEnd = endOf(node.getName());
		int declarationSourceEnd = endOf(node);
		
		requestor.exitField(initializerStart, declarationEnd, declarationSourceEnd);
	}
	
	@Override
	public boolean visit(InvariantDeclaration node) {
		requestor.enterInitializer(startOf(node), getFlags(node.modifiers()) | Flags.AccInvariant, CharOperation.NO_CHAR);
		return false;
	}
	
	@Override
	public void endVisit(InvariantDeclaration node) {
		requestor.exitInitializer(endOf(node));
	}
	
	@Override
	public boolean visit(UnitTestDeclaration node) {
		requestor.enterInitializer(startOf(node), getFlags(node.modifiers()) | Flags.AccUnitTest, CharOperation.NO_CHAR);
		return false;
	}
	
	@Override
	public void endVisit(UnitTestDeclaration node) {
		requestor.exitInitializer(endOf(node));
	}
	
	@Override
	public boolean visit(StaticAssert node) {
		requestor.enterInitializer(startOf(node), getFlags(node.modifiers()) | Flags.AccStaticAssert, node.getExpression().toString().toCharArray());
		return false;
	}
	
	@Override
	public void endVisit(StaticAssert node) {
		requestor.exitInitializer(endOf(node));
	}
	
	@Override
	public boolean visit(DebugAssignment node) {
		requestor.enterInitializer(startOf(node), getFlags(node.modifiers()) | Flags.AccDebugAssignment, node.getVersion().getValue().toCharArray());
		return false;
	}
	
	@Override
	public void endVisit(DebugAssignment node) {
		requestor.exitInitializer(endOf(node));
	}
	
	@Override
	public boolean visit(VersionAssignment node) {
		requestor.enterInitializer(startOf(node), getFlags(node.modifiers()) | Flags.AccVersionAssignment, node.getVersion().getValue().toCharArray());
		return false;
	}
	
	@Override
	public void endVisit(VersionAssignment node) {
		requestor.exitInitializer(endOf(node));
	}
	
	@Override
	public boolean visit(AlignDeclaration node) {
		requestor.enterInitializer(startOf(node), getFlags(node.modifiers()) | Flags.AccAlign, node.getAlign() == 0 ? CharOperation.NO_CHAR : String.valueOf(node.getAlign()).toCharArray());
		return true;
	}
	
	@Override
	public void endVisit(AlignDeclaration node) {
		requestor.exitInitializer(endOf(node));
	}
	
	@Override
	public boolean visit(ExternDeclaration node) {
		requestor.enterInitializer(startOf(node), getFlags(node.modifiers()) | Flags.AccExternDeclaration, String.valueOf(node.getLinkage().toString()).toCharArray());
		return true;
	}
	
	@Override
	public void endVisit(ExternDeclaration node) {
		requestor.exitInitializer(endOf(node));
	}
	
	@Override
	public boolean visit(MixinDeclaration node) {
		requestor.enterInitializer(startOf(node), getFlags(node.modifiers()) | Flags.AccMixin, String.valueOf(node.getExpression().toString()).toCharArray());
		return false;
	}
	
	@Override
	public void endVisit(MixinDeclaration node) {
		requestor.exitInitializer(endOf(node));
	}
	
	@Override
	public boolean visit(PragmaDeclaration node) {
		StringBuilder sb = new StringBuilder();
		sb.append(node.getName());
		if (node.arguments().size() > 0) {
			sb.append(": ");
			for(int i = 0; i < node.arguments().size(); i++) {
				if (i != 0)
					sb.append(", ");
				sb.append(node.arguments().get(i).toString());
			}
		}
		requestor.enterInitializer(startOf(node), getFlags(node.modifiers()) | Flags.AccPragma, sb.toString().toCharArray());
		return true;
	}
	
	@Override
	public void endVisit(PragmaDeclaration node) {
		requestor.exitInitializer(endOf(node));
	}
	
	@Override
	public boolean visit(DebugDeclaration node) {
		String displayString = node.getVersion() == null ? "" : node.getVersion().getValue();
		return visitConditionalDeclaration(node, Flags.AccDefault, displayString);
	}
	
	@Override
	public void endVisit(DebugDeclaration node) {
		requestor.exitConditional(endOf(node));
	}
	
	@Override
	public boolean visit(StaticIfDeclaration node) {
		String displayString = node.getExpression() == null ? "" : node.getExpression().toString();
		return visitConditionalDeclaration(node, Flags.AccStaticIfDeclaration, displayString);
	}
	
	@Override
	public void endVisit(StaticIfDeclaration node) {
		requestor.exitConditional(endOf(node));
	}
	
	@Override
	public boolean visit(IftypeDeclaration node) {
		return visitConditionalDeclaration(node, Flags.AccIftypeDeclaration, "");
	}
	
	@Override
	public void endVisit(IftypeDeclaration node) {
		requestor.exitConditional(endOf(node));
	}
	
	@Override
	public boolean visit(VersionDeclaration node) {
		String displayString = node.getVersion() == null ? "" : node.getVersion().getValue();
		return visitConditionalDeclaration(node, Flags.AccVersionDeclaration, displayString);
	}
	
	@Override
	public void endVisit(VersionDeclaration node) {
		requestor.exitConditional(endOf(node));
	}
	
	private boolean visitConditionalDeclaration(ConditionalDeclaration node, int flags, String displayString) {
		requestor.enterConditional(node.getStartPosition(), getFlags(node.modifiers()) | flags, displayString.toCharArray());
		
		List<Declaration> thenDeclarations = node.thenDeclarations();
		List<Declaration> elseDeclarations = node.elseDeclarations();
		
		if (!thenDeclarations.isEmpty()) {
			if (!elseDeclarations.isEmpty()) {
				requestor.enterConditionalThen(startOf(thenDeclarations.get(0)));
			}
			for(Declaration declaration : thenDeclarations) {
				declaration.accept(this);
			}
			if (!elseDeclarations.isEmpty()) {
				requestor.exitConditionalThen(endOf(thenDeclarations.get(thenDeclarations.size() - 1)));
			}
		}
		
		
		if (!elseDeclarations.isEmpty()) {
			requestor.enterConditionalElse(startOf(elseDeclarations.get(0)));
			for(Declaration declaration : elseDeclarations) {
				declaration.accept(this);
			}
			requestor.exitConditionalElse(endOf(elseDeclarations.get(elseDeclarations.size() - 1)));
		}
		
		return false;
	}
	
}
